package com.tpagiles.app_licencia.service.impl;

import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Licencia;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.repository.LicenciaRepository;
import com.tpagiles.app_licencia.repository.TitularRepository;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import com.tpagiles.app_licencia.repository.TarifarioLicenciaRepository;
import com.tpagiles.app_licencia.service.ILicenciaService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LicenciaService implements ILicenciaService {

    private final LicenciaRepository licenciaRepository;
    private final TitularRepository titularRepository;
    private final UsuarioRepository usuarioRepository;
    private final TarifarioLicenciaRepository tarifarioLicenciaRepository;

    @Override
    @Transactional
    public LicenciaResponseRecord emitirLicencia(LicenciaRecord request) {
        Titular titular = titularRepository.findById(request.titularId())
                .orElseThrow(() -> new ResourceNotFoundException("Titular no encontrado"));

        if (!(request.clase() == ClaseLicencia.A || request.clase() == ClaseLicencia.B)) {
            throw new IllegalArgumentException("Solo se permite emitir licencias clase A o B.");
        }

        int edad = java.time.Period.between(titular.getFechaNacimiento(), java.time.LocalDate.now()).getYears();
        if (edad < 17) {
            throw new IllegalArgumentException("Edad mínima para clase A o B: 17 años.");
        }

        int vigencia = calcularVigencia(edad, titular);
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate vencimiento = hoy.withMonth(titular.getFechaNacimiento().getMonthValue())
                .withDayOfMonth(titular.getFechaNacimiento().getDayOfMonth())
                .plusYears(vigencia);

        double costoBase = tarifarioLicenciaRepository
                .findByClaseLicenciaAndVigenciaAnios(request.clase(), vigencia)
                .orElseThrow(() -> new RuntimeException("Tarifa no encontrada"))
                .getCosto();

        double costoTotal = costoBase + 8;

        Usuario emisor = usuarioRepository.findByUsername("admin") // reemplazar con request.emisorUsername() si lo agregás
                .orElseThrow(() -> new ResourceNotFoundException("Usuario emisor no encontrado"));

        Licencia licencia = request.toEntity(titular, emisor);
        licencia.setFechaEmision(hoy);
        licencia.setVigenciaAnios(vigencia);
        licencia.setFechaVencimiento(vencimiento);
        licencia.setCosto(costoTotal);
        licencia.setVigente(true);

        licenciaRepository.save(licencia);
        return LicenciaResponseRecord.fromEntity(licencia);
    }

    public int calcularVigencia(int edad, Titular titular) {
        if (edad < 21) {
            boolean yaTieneLicencia = licenciaRepository.findByTitularIdAndClase(titular.getId(), ClaseLicencia.A) ||
                    licenciaRepository.findByTitularIdAndClase(titular.getId(), ClaseLicencia.B);
            return yaTieneLicencia ? 3 : 1;
        } else if (edad <= 46) return 5;
        else if (edad <= 60) return 4;
        else if (edad <= 70) return 3;
        else return 1;
    }
}




