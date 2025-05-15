package com.tpagiles.app_licencia.service.helper;

import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.repository.TarifarioLicenciaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CostoLicenciaHelper {

    private final TarifarioLicenciaRepository tarifarioLicenciaRepository;
    /**
     * por si necesitan un ejemplo visual del map. Att: wizard.
     * tarifas = {
     *   A → { 1 → 100.0,  5 → 450.0 },
     *   B → { 1 → 120.0,  3 → 300.0 }
     *   // C y D probablemente sin clave si no hay datos
     * }
     */
    private final Map<ClaseLicencia, Map<Integer, Double>> tarifas = new EnumMap<>(ClaseLicencia.class);
    private static final double GASTOS_ADMIN = 8.0;

    @PostConstruct
    private void cargarTarifas() {
        tarifarioLicenciaRepository.findAll().forEach(t ->
                tarifas
                        .computeIfAbsent(t.getClaseLicencia(), c -> new HashMap<>())
                        .put(t.getVigenciaAnios(), t.getCosto())
        );
    }

    public double obtenerCosto(ClaseLicencia clase, int anios) {
        Map<Integer, Double> porVigencia = tarifas.get(clase);
        if (porVigencia == null || !porVigencia.containsKey(anios)) {
            throw new ResourceNotFoundException(
                    "Tarifa no encontrada para clase " + clase + " y vigencia " + anios + " años"
            );
        }
        return porVigencia.get(anios) + GASTOS_ADMIN;
    }
}
