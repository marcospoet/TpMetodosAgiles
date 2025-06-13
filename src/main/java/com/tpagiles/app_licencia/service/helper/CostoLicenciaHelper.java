package com.tpagiles.app_licencia.service.helper;

import com.tpagiles.app_licencia.exception.ResourceNotFoundException;
import com.tpagiles.app_licencia.model.TarifarioLicencia;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.repository.TarifarioLicenciaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@DependsOn("dataSourceScriptDatabaseInitializer")
@RequiredArgsConstructor
public class CostoLicenciaHelper {

    private final TarifarioLicenciaRepository tarifarioLicenciaRepository;

    /**
     * Gasto administrativo por defecto (inyectable desde properties).
     */
    @Value("${app.licencia.gastos-admin:8.0}")
    private double gastosAdmin;

    /**
     * Mapa inmutable:
     *   ClaseLicencia → (VigenciaAños → CostoBase)
     */
    private Map<ClaseLicencia, Map<Integer, Double>> tarifas;

    @PostConstruct
    private void init() {
        // Cargamos y agrupamos todas las filas en un EnumMap inmutable
        Map<ClaseLicencia, Map<Integer, Double>> cargadas =
                tarifarioLicenciaRepository.findAll().stream()
                        .collect(Collectors.groupingBy(
                                TarifarioLicencia::getClaseLicencia,
                                () -> new EnumMap<>(ClaseLicencia.class),
                                Collectors.toUnmodifiableMap(
                                        TarifarioLicencia::getVigenciaAnios,
                                        TarifarioLicencia::getCosto,
                                        (existing, replacement) -> existing  // merge: nos quedamos con el primero
                                )
                        ));

        this.tarifas = Collections.unmodifiableMap(cargadas);
    }

    /**
     * Devuelve el costo total = costo base + gastosAdmin.
     *
     * @throws ResourceNotFoundException si no existe tarifa para (clase, años)
     */
    public double obtenerCosto(ClaseLicencia clase, int anios) {
        Map<Integer, Double> porVigencia = tarifas.getOrDefault(clase, Collections.emptyMap());
        Double costoBase = porVigencia.get(anios);
        if (costoBase == null) {
            Set<Integer> opciones = porVigencia.keySet();
            throw new ResourceNotFoundException(
                    String.format(
                            "Tarifa no encontrada para clase %s y vigencia %d años. Vigencias disponibles: %s",
                            clase, anios, opciones
                    )
            );
        }
        return costoBase + gastosAdmin;
    }
}
