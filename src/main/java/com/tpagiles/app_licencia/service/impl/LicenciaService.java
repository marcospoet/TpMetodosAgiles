package com.tpagiles.app_licencia.service.impl;

import com.tpagiles.app_licencia.dto.*;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Licencia;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.MotivoRenovacion;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.repository.LicenciaRepository;
import com.tpagiles.app_licencia.repository.TitularRepository;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import com.tpagiles.app_licencia.service.ILicenciaService;
import com.tpagiles.app_licencia.service.ITitularService;
import com.tpagiles.app_licencia.service.helper.CostoLicenciaHelper;
import com.tpagiles.app_licencia.service.helper.LicenciaHelper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicenciaService implements ILicenciaService {

    private final ITitularService titularService;
    private final LicenciaRepository licenciaRepo;
    private final CostoLicenciaHelper costoHelper;
    private final LicenciaHelper licenciaHelper;

    // Inyectamos el repo de Usuario directamente por ahora despues seria IUsuarioSerie
    private final UsuarioRepository usuarioRepo;
    private final TitularRepository titularRepository;

    @Override
    @Transactional
    public LicenciaResponseRecord emitirLicencia(LicenciaRecord req) {
        Titular titular = titularService.obtenerPorId(req.titularId());
        if (licenciaRepo.existsByTitularIdAndClaseAndVigenteTrueAndFechaVencimientoAfter(
                req.titularId(), req.clase(), LocalDate.now())) {
            throw new ResourceAlreadyExistsException(
                    "No es posible emitir licencia de clase " + req.clase() +
                            " para el titular con id " + req.titularId() +
                            ". Hay una vigente, debe renovarla."
            );
        }
        licenciaHelper.validarClaseYRestricciones(titular, req.clase());
        int vigencia = licenciaHelper.calcularVigencia(titular);

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = licenciaHelper
                .calcularFechaVencimiento(hoy, titular.getFechaNacimiento(), vigencia);

        double costo = costoHelper.obtenerCosto(req.clase(), vigencia);

        // Usamos ahora el repo directamente para buscar al emisor pero despues seria le IUsuarioService en el sprint 2
        Usuario emisor = usuarioRepo
                .findByMail(req.emisor())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario emisor no encontrado: " + req.emisor())
                );

        Licencia licencia = Licencia.builder()
                .titular(titular)
                .clase(req.clase())
                .vigenciaAnios(vigencia)
                .fechaEmision(hoy)
                .fechaVencimiento(vencimiento)
                .costo(costo)
                .emisor(emisor)
                .vigente(true)
                .build();

        return LicenciaResponseRecord.fromEntity(licenciaRepo.save(licencia));
    }

    @Transactional(readOnly = true)
    @Override
    public List<LicenciaResponseRecord> listarLicenciasVencidas() {
        LocalDate hoy = LocalDate.now();
        return licenciaRepo.findByFechaVencimientoBefore(hoy).stream()
                .map(LicenciaResponseRecord::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public long contarLicenciasVencidas() {
        return licenciaRepo.countByFechaVencimientoBefore(LocalDate.now());
    }

    @Transactional(readOnly = true)
    @Override
    public long contarTotalLicenciasEmitidas() {
        return licenciaRepo.count();
    }
    @Transactional(readOnly = true)
    @Override
    public TitularConLicenciasResponseRecord buscarPorTipoYNumeroDocumento(
            TipoDocumento tipoDocumento,
            String numeroDocumento
    ) {
        // 1. Obtenemos todas las licencias
        List<Licencia> licencias = licenciaRepo
                .findByTitularTipoDocumentoAndTitularNumeroDocumento(tipoDocumento, numeroDocumento);

        if (licencias.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontró ningún titular que tenga al menos una licencia con " +
                            tipoDocumento + " " + numeroDocumento
            );
        }

        // 2. Extraemos la entidad Titular del primer elemento
        var titularEntity = licencias.getFirst().getTitular();

        // 3. Convertimos a DTOs
        TitularResponseRecord titularDto = TitularResponseRecord.fromEntity(titularEntity);
        List<LicenciaResponseRecord> licenciasDto = licencias.stream()
                .map(LicenciaResponseRecord::fromEntityAlt)
                .toList();

        // 4. Devolvemos el record que agrupa ambos
        return TitularConLicenciasResponseRecord.from(titularDto, licenciasDto);
    }

    @Override
    @Transactional
    public LicenciaResponseRecord renovarLicencia(RenovarLicenciaRequest request) {
        // 1. Buscar la licencia existente
        Licencia licenciaExistente = licenciaRepo.findById(request.licenciaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Licencia no encontrada con ID: " + request.licenciaId())
                );

        // 2. Validar que esté vencida o requiera cambio de datos
        validarMotivoRenovacion(licenciaExistente, request.motivoRenovacion());
        validarCamposCambioDatos(request);
        if (request.motivoRenovacion() == MotivoRenovacion.VENCIDA) {
            validarLicenciaVencida(licenciaExistente);
        }

        // 3. Actualizar datos del titular si es necesario
        if (request.motivoRenovacion() == MotivoRenovacion.CAMBIO_DATOS) {
            actualizarDatosTitular(licenciaExistente.getTitular(), request);
        }

        // 4. Crear nueva licencia con fecha actualizada
        Licencia licenciaRenovada = crearLicenciaRenovada(licenciaExistente, request);

        // 5. Guardar en base de datos
        licenciaExistente.setVigente(false);
        licenciaRepo.save(licenciaExistente);
        Licencia nuevaLicencia = licenciaRepo.save(licenciaRenovada);

        // 6. Retornar respuesta
        return LicenciaResponseRecord.fromEntity(nuevaLicencia);
    }

    private void validarLicenciaVencida(Licencia licencia) {
        if (licencia.getFechaVencimiento().isAfter(LocalDate.now())) {
            throw new IllegalStateException("La licencia aún no está vencida");
        }
    }

    private void actualizarDatosTitular(Titular titular, RenovarLicenciaRequest request) {
        if (request.nuevoNombre() != null) titular.setNombre(request.nuevoNombre());
        if (request.nuevoApellido() != null) titular.setApellido(request.nuevoApellido());
        if (request.nuevaDireccion() != null) titular.setDireccion(request.nuevaDireccion());

        titularRepository.save(titular);
    }

    private void validarMotivoRenovacion(Licencia licencia, MotivoRenovacion motivo) {
        switch (motivo) {
            case VENCIDA:
                if (licencia.getFechaVencimiento().isAfter(LocalDate.now()) ||
                        licencia.getFechaVencimiento().equals(LocalDate.now())) {
                    throw new IllegalArgumentException(
                            "No se puede renovar por vencimiento. La licencia vence el: " +
                                    licencia.getFechaVencimiento()
                    );
                }
                break;
            case CAMBIO_DATOS:
                if (!licencia.isVigente()) {
                    throw new IllegalArgumentException(
                            "No se puede renovar una licencia inactiva por cambio de datos"
                    );
                }
                if (licencia.getFechaVencimiento().isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException(
                            "No se puede renovar una licencia vencida por cambio de datos. Use motivo VENCIDA"
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Motivo de renovación no válido: " + motivo);
        }
    }

    private void validarCamposCambioDatos(RenovarLicenciaRequest request) {
        if (request.motivoRenovacion() == MotivoRenovacion.CAMBIO_DATOS) {
            boolean tieneAlgunCambio = request.nuevoNombre() != null ||
                    request.nuevoApellido() != null ||
                    request.nuevaDireccion() != null;

            if (!tieneAlgunCambio) {
                throw new IllegalArgumentException(
                        "Para renovación por cambio de datos, debe especificar al menos un campo a actualizar"
                );
            }
        }
    }

    private Licencia crearLicenciaRenovada(Licencia licenciaAnterior, RenovarLicenciaRequest request) {
        Titular titular = licenciaAnterior.getTitular();
        int vigencia = licenciaHelper.calcularVigencia(titular);

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = licenciaHelper.calcularFechaVencimiento(
                hoy,
                titular.getFechaNacimiento(),
                vigencia
        );

        double costo = costoHelper.obtenerCosto(licenciaAnterior.getClase(), vigencia);

        Licencia licenciaOriginal = null;
        if (request.licenciaOriginalId() != null) {
            licenciaOriginal = licenciaRepo.findById(request.licenciaOriginalId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Licencia original no encontrada: " + request.licenciaOriginalId()
                    ));
        }

        return Licencia.builder()
                .titular(titular)
                .clase(licenciaAnterior.getClase())
                .vigenciaAnios(vigencia)
                .fechaEmision(hoy)
                .fechaVencimiento(vencimiento)
                .costo(costo)
                .emisor(licenciaAnterior.getEmisor())
                .vigente(true)
                .numeroCopia(request.numeroCopia())
                .motivoCopia(request.motivoCopia())
                .licenciaOriginal(licenciaOriginal)
                .build();
    }
    @Override
    @Transactional
    public LicenciaResponseRecord emitirCopia(EmitirCopiaRequest request) {
        Licencia original = licenciaRepo.findById(request.licenciaOriginalId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Licencia original no encontrada con ID: " + request.licenciaOriginalId())
                );

        if (!original.isVigente() || original.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede emitir copia de una licencia vencida o inactiva.");
        }

        Titular titular = original.getTitular();

        Usuario emisor = usuarioRepo.findByMail(request.emisor())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario emisor no encontrado: " + request.emisor()));

        int nuevoNroCopia = (original.getNumeroCopia() != null ? original.getNumeroCopia() : 0) + 1;

        Licencia copia = Licencia.builder()
                .titular(titular)
                .clase(original.getClase())
                .vigenciaAnios(original.getVigenciaAnios())
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(original.getFechaVencimiento())
                .costo(50.0)
                .numeroCopia(nuevoNroCopia)
                .motivoCopia(request.motivo())
                .licenciaOriginal(original)
                .emisor(emisor)
                .vigente(true)
                .build();

        return LicenciaResponseRecord.fromEntity(licenciaRepo.save(copia));
    }

}




