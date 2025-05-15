package com.tpagiles.app_licencia.service.helper;

import com.tpagiles.app_licencia.exception.InvalidLicenseException;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.repository.LicenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LicenciaHelper {

    private static final int EDAD_MINIMA = 17;

    private final LicenciaRepository licenciaRepository;

    /**
     * Valida que sea A o B para este sprint
     */
    public void validarClaseBasica(ClaseLicencia clase) {
        if (clase != ClaseLicencia.A && clase != ClaseLicencia.B) {
            throw new InvalidLicenseException("Solo se permite emitir licencias clase A o B.");
        }
    }

    public void validarEdadMinima(Titular titular) {
        if (titular.getEdad() < EDAD_MINIMA) {
            throw new InvalidLicenseException(
                    "Edad mínima para licencia básica: " + EDAD_MINIMA + " años."
            );
        }
    }

    /**
     * Calcula cuántos años de vigencia corresponden según la edad
     * y si ya existe una A o B anterior.
     */
    public int calcularVigencia(Titular titular) {
        int edad = titular.getEdad();
        if (edad < 21) {
            boolean yaTiene = licenciaRepository.existsByTitularAndClase(titular, ClaseLicencia.A) || licenciaRepository.existsByTitularAndClase(titular, ClaseLicencia.B);
            return yaTiene ? 3 : 1;
        } else if (edad <= 46) {
            return 5;
        } else if (edad <= 60) {
            return 4;
        } else if (edad <= 70) {
            return 3;
        } else {
            return 1;
        }
    }

    /**
     * Ajusta la fecha de vencimiento para que caiga en el mismo
     * día y mes de nacimiento, más los años de vigencia.
     */
    public LocalDate calcularFechaVencimiento(LocalDate fechaEmision,
                                              LocalDate fechaNacimiento,
                                              int vigenciaAnios) {
        return fechaEmision
                .withMonth(fechaNacimiento.getMonthValue())
                .withDayOfMonth(fechaNacimiento.getDayOfMonth())
                .plusYears(vigenciaAnios);
    }
}
