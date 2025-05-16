package com.tpagiles.app_licencia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tpagiles.app_licencia.dto.TitularRecord;
import com.tpagiles.app_licencia.exception.ResourceAlreadyExistsException;
import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.FactorRh;
import com.tpagiles.app_licencia.model.enums.GrupoSanguineo;
import com.tpagiles.app_licencia.model.enums.TipoDocumento;
import com.tpagiles.app_licencia.repository.TitularRepository;
import com.tpagiles.app_licencia.service.impl.TitularService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TitularServiceTest {

    @Mock
    private TitularRepository repo;

    @InjectMocks
    private TitularService service;

    private TitularRecord validRecord;
    private Titular sampleEntity;

    @BeforeEach
    void setup() {
        validRecord = new TitularRecord(
                "Juan", "Pérez",
                LocalDate.of(1990, 1, 1),
                TipoDocumento.DNI, "12345678",
                GrupoSanguineo.O, FactorRh.POSITIVO,
                "Calle Falsa 123", true
        );
        sampleEntity = new Titular();
        sampleEntity.setId(1L);
        sampleEntity.setNombre("Juan");
        sampleEntity.setApellido("Pérez");
        sampleEntity.setFechaNacimiento(LocalDate.of(1990,1,1));
        sampleEntity.setTipoDocumento(TipoDocumento.DNI);
        sampleEntity.setNumeroDocumento("12345678");
        sampleEntity.setGrupoSanguineo(GrupoSanguineo.O);
        sampleEntity.setFactorRh(FactorRh.POSITIVO);
        sampleEntity.setDireccion("Calle Falsa 123");
        sampleEntity.setDonanteOrganos(true);
    }

    @Test
    @DisplayName("createTitular → éxito, devuelve entidad con id")
    void createTitular_success() {
        when(repo.existsByNumeroDocumento(validRecord.numeroDocumento()))
                .thenReturn(false);
        when(repo.save(any(Titular.class)))
                .thenAnswer(inv -> {
                    Titular t = inv.getArgument(0);
                    t.setId(42L);
                    return t;
                });

        Titular result = service.createTitular(validRecord);

        assertNotNull(result.getId());
        assertEquals("Juan", result.getNombre());
        verify(repo).save(any());
    }

    @Test
    @DisplayName("createTitular → documento duplicado → lanza ResourceAlreadyExistsException")
    void createTitular_duplicateThrows() {
        when(repo.existsByNumeroDocumento(validRecord.numeroDocumento()))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
                service.createTitular(validRecord)
        );
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("obtenerPorId → id existente → devuelve Titular")
    void obtenerPorId_found() {
        when(repo.findById(1L)).thenReturn(Optional.of(sampleEntity));

        Titular t = service.obtenerPorId(1L);
        assertEquals(1L, t.getId());
        assertEquals("Pérez", t.getApellido());
    }

    @Test
    @DisplayName("obtenerPorId → no existe → lanza ResourceNotFoundException")
    void obtenerPorId_notFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                service.obtenerPorId(99L)
        );
    }

    @Test
    @DisplayName("listarTodos → devuelve lista de titulares")
    void listarTodos_returnsList() {
        Titular otro = new Titular();
        otro.setId(2L);
        when(repo.findAll()).thenReturn(Arrays.asList(sampleEntity, otro));

        List<Titular> lista = service.listarTodos();
        assertEquals(2, lista.size());
        verify(repo).findAll();
    }

    @Test
    @DisplayName("actualizarTitular → éxito, actualiza y devuelve")
    void actualizarTitular_success() {
        TitularRecord updated = new TitularRecord(
                "Luis", "Gómez",
                LocalDate.of(1985, 5, 5),
                TipoDocumento.PASAPORTE, "X123",
                GrupoSanguineo.AB, FactorRh.NEGATIVO,
                "Av. Siempre Viva 742", false
        );
        when(repo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Titular modificado = service.actualizarTitular(1L, updated);
        assertEquals("Luis", modificado.getNombre());
        assertEquals("X123", modificado.getNumeroDocumento());
        verify(repo).save(modificado);
    }

    @Test
    @DisplayName("actualizarTitular → no existe → lanza ResourceNotFoundException")
    void actualizarTitular_notFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        TitularRecord dummy = validRecord;
        assertThrows(ResourceNotFoundException.class, () ->
                service.actualizarTitular(99L, dummy)
        );
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("eliminarTitular → éxito, llama a deleteById")
    void eliminarTitular_success() {
        when(repo.existsById(1L)).thenReturn(true);

        service.eliminarTitular(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarTitular → no existe → lanza ResourceNotFoundException")
    void eliminarTitular_notFoundThrows() {
        when(repo.existsById(5L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                service.eliminarTitular(5L)
        );
        verify(repo, never()).deleteById(anyLong());
    }
}
