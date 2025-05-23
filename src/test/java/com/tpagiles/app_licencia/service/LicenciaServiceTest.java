package com.tpagiles.app_licencia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tpagiles.app_licencia.dto.LicenciaRecord;
import com.tpagiles.app_licencia.dto.LicenciaResponseRecord;
import com.tpagiles.app_licencia.dto.TitularConLicenciasResponseRecord;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Licencia;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.Usuario;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.repository.LicenciaRepository;
import com.tpagiles.app_licencia.repository.UsuarioRepository;
import com.tpagiles.app_licencia.service.impl.LicenciaService;
import com.tpagiles.app_licencia.service.helper.CostoLicenciaHelper;
import com.tpagiles.app_licencia.service.helper.LicenciaHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LicenciaServiceTest {

    @Mock
    private ITitularService titularService;

    @Mock
    private LicenciaRepository licenciaRepo;

    @Mock
    private CostoLicenciaHelper costoHelper;

    @Mock
    private LicenciaHelper licenciaHelper;

    @Mock
    private UsuarioRepository usuarioRepo;

    @InjectMocks
    private LicenciaService service;

    private Titular titular;
    private Usuario emisor;
    private LicenciaRecord record;
    private Licencia licenciaEntity;

    @BeforeEach
    void setUp() {
        titular = new Titular();
        titular.setId(10L);
        titular.setNombre("Juan");
        titular.setApellido("Pérez");
        titular.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        titular.setTipoDocumento(TipoDocumento.DNI);
        titular.setNumeroDocumento("12345678");
        titular.setGrupoSanguineo(GrupoSanguineo.O);
        titular.setFactorRh(FactorRh.POSITIVO);
        titular.setDireccion("Calle Falsa 123");
        titular.setDonanteOrganos(true);

        emisor = new Usuario();
        emisor.setId(5L);
        emisor.setUsername("admin");

        record = new LicenciaRecord(
                titular.getId(),
                ClaseLicencia.A,
                null,
                null,
                emisor.getUsername()
        );

        // Preparamos una licencia “real” con ese titular completo
        licenciaEntity = Licencia.builder()
                .id(20L)
                .titular(titular)
                .clase(ClaseLicencia.A)
                .vigenciaAnios(5)
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusYears(5))
                .costo(100.0)
                .emisor(emisor)
                .vigente(true)
                .build();
    }


    @Test
    @DisplayName("emitirLicencia → éxito, retorna LicenciaResponseRecord")
    void emitirLicencia_success() {
        when(titularService.obtenerPorId(record.titularId()))
                .thenReturn(titular);
        when(licenciaRepo.existsByTitularIdAndClaseAndVigenteTrueAndFechaVencimientoAfter(
                eq(record.titularId()), eq(record.clase()), any(LocalDate.class)))
                .thenReturn(false);
        doNothing().when(licenciaHelper).validarClaseBasica(record.clase());
        doNothing().when(licenciaHelper).validarEdadMinima(titular);
        when(licenciaHelper.calcularVigencia(titular)).thenReturn(5);
        when(costoHelper.obtenerCosto(record.clase(), 5)).thenReturn(100.0);
        when(usuarioRepo.findByUsername(record.emisor()))
                .thenReturn(java.util.Optional.of(emisor));
        when(licenciaRepo.save(any(Licencia.class)))
                .thenReturn(licenciaEntity);

        LicenciaResponseRecord resp = service.emitirLicencia(record);

        assertEquals(licenciaEntity.getId(), resp.id());
        assertEquals("A", resp.clase());
        verify(licenciaRepo).save(any());
    }

    @Test
    @DisplayName("emitirLicencia → ya existe vigente → lanza ResourceAlreadyExistsException")
    void emitirLicencia_existingActiveLicenseThrows() {
        when(titularService.obtenerPorId(record.titularId()))
                .thenReturn(titular);
        when(licenciaRepo.existsByTitularIdAndClaseAndVigenteTrueAndFechaVencimientoAfter(
                anyLong(), any(), any()))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> service.emitirLicencia(record));
        verify(licenciaRepo, never()).save(any());
    }

    @Test
    @DisplayName("emitirLicencia → emisor no encontrado → lanza ResourceNotFoundException")
    void emitirLicencia_emisorNotFoundThrows() {
        when(titularService.obtenerPorId(record.titularId()))
                .thenReturn(titular);
        when(licenciaRepo.existsByTitularIdAndClaseAndVigenteTrueAndFechaVencimientoAfter(
                anyLong(), any(), any()))
                .thenReturn(false);
        when(licenciaHelper.calcularVigencia(titular)).thenReturn(5);
        when(costoHelper.obtenerCosto(record.clase(), 5)).thenReturn(100.0);
        when(usuarioRepo.findByUsername(record.emisor()))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.emitirLicencia(record));
    }

    @Test
    @DisplayName("listarLicenciasVencidas → retorna lista de DTOs")
    void listarLicenciasVencidas_returnsList() {
        LocalDate hoy = LocalDate.now();
        Licencia l1 = licenciaEntity;
        Licencia l2 = Licencia.builder()
                .id(21L).titular(titular).clase(ClaseLicencia.B)
                .vigenciaAnios(3).fechaEmision(hoy.minusYears(4))
                .fechaVencimiento(hoy.minusDays(1))
                .costo(80.0).emisor(emisor).vigente(false)
                .build();

        when(licenciaRepo.findByFechaVencimientoBefore(hoy))
                .thenReturn(List.of(l1, l2));

        var list = service.listarLicenciasVencidas();
        assertEquals(2, list.size());
        verify(licenciaRepo).findByFechaVencimientoBefore(hoy);
    }

    @Test
    @DisplayName("contarLicenciasVencidas → devuelve conteo")
    void contarLicenciasVencidas_returnsCount() {
        when(licenciaRepo.countByFechaVencimientoBefore(any(LocalDate.class)))
                .thenReturn(7L);
        assertEquals(7L, service.contarLicenciasVencidas());
    }

    @Test
    @DisplayName("contarTotalLicenciasEmitidas → devuelve conteo total")
    void contarTotalLicenciasEmitidas_returnsCount() {
        when(licenciaRepo.count()).thenReturn(42L);
        assertEquals(42L, service.contarTotalLicenciasEmitidas());
    }

    @Test
    @DisplayName("buscarPorTipoYNumeroDocumento → éxito, retorna DTO combinado")
    void buscarPorTipoYNumeroDocumento_success() {
        Licencia l1 = licenciaEntity;
        Licencia l2 = Licencia.builder()
                .id(22L).titular(titular).clase(ClaseLicencia.A)
                .vigenciaAnios(5).fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusYears(5))
                .costo(100.0).emisor(emisor).vigente(true)
                .build();

        when(licenciaRepo.findByTitularTipoDocumentoAndTitularNumeroDocumento(
                TipoDocumento.DNI, "123"))
                .thenReturn(List.of(l1, l2));

        TitularConLicenciasResponseRecord dto =
                service.buscarPorTipoYNumeroDocumento(TipoDocumento.DNI, "123");

        assertEquals(titular.getId(), dto.titular().id());
        assertEquals(2, dto.licencias().size());
    }

    @Test
    @DisplayName("buscarPorTipoYNumeroDocumento → no existe, lanza ResourceNotFoundException")
    void buscarPorTipoYNumeroDocumento_notFoundThrows() {
        when(licenciaRepo.findByTitularTipoDocumentoAndTitularNumeroDocumento(
                TipoDocumento.DNI, "123"))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> service.buscarPorTipoYNumeroDocumento(TipoDocumento.DNI, "123"));
    }
}
