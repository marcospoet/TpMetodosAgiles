package com.tpagiles.app_licencia.service.helper;

import com.tpagiles.app_licencia.exception.InvalidLicenseException;
import com.tpagiles.app_licencia.model.Licencia;
import com.tpagiles.app_licencia.model.Titular;
import com.tpagiles.app_licencia.model.enums.ClaseLicencia;
import com.tpagiles.app_licencia.repository.LicenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LicenciaHelper {

    private static final int EDAD_MINIMA_GENERAL = 17;
    private static final int EDAD_MINIMA_PROFESIONAL = 21;
    private static final int EDAD_MAXIMA_PROFESIONAL = 65;

    private final LicenciaRepository licenciaRepository;

    /**
     * Valida si un titular puede obtener la clase solicitada según su edad y condiciones
     */
    public void validarClaseYRestricciones(Titular titular, ClaseLicencia clase) {
        int edad = titular.getEdad();

        switch (clase) {
            case C, D, E -> {
                if (edad < EDAD_MINIMA_PROFESIONAL)
                    throw new InvalidLicenseException("La edad mínima para clase " + clase + " es 21 años.");

                if (!titularTieneLicenciaB(titular))
                    throw new InvalidLicenseException("Para obtener clase " + clase + " debe tener una licencia clase B vigente con al menos 1 año.");

                if (edad > EDAD_MAXIMA_PROFESIONAL)
                    throw new InvalidLicenseException("No se puede emitir clase " + clase + " a mayores de 65 años por ser profesional.");
            }
            case A, B, F, G -> {
                if (edad < EDAD_MINIMA_GENERAL)
                    throw new InvalidLicenseException("La edad mínima para clase " + clase + " es 17 años.");
            }
            default -> throw new InvalidLicenseException("Clase de licencia inválida: " + clase);
        }
    }

    private boolean titularTieneLicenciaB(Titular titular) {
        List<Licencia> licenciasB = licenciaRepository.findByTitularAndClase(titular, ClaseLicencia.B);
        return licenciasB.stream()
                .anyMatch(lic -> lic.isVigente()
                        && lic.getFechaEmision().isBefore(LocalDate.now().minusYears(1)));
    }

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
    public LocalDate calcularFechaVencimiento(LocalDate fechaEmision,
                                              LocalDate fechaNacimiento,
                                              int vigenciaAnios) {
        return fechaEmision
                .withMonth(fechaNacimiento.getMonthValue())
                .withDayOfMonth(fechaNacimiento.getDayOfMonth())
                .plusYears(vigenciaAnios);
    }
}
