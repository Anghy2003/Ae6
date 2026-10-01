package edu.uees.testing.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Suite del Laboratorio 1 de Semana 7: casos de docs/01_MATRIZ_CASOS.md.
 *
 * puedeCancelar() y calcularTotal() no usan los colaboradores, por eso
 * construyo el servicio con null solo en este laboratorio.
 */
class ReservaServiceTest {

    private static final double DELTA = 0.001;

    private final ReservaService servicio =
            new ReservaService(null, null, null);

    @Test
    void entornoJUnitFunciona() {
        assertTrue(true);
    }

    // ------------------------------------------------ puedeCancelar

    @Test
    void cincoHorasPermitenCancelar() {                        // CP-01
        // Arrange
        int horas = 5;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void dosHorasEsElLimitePermitido() {                       // CP-02
        // Arrange
        int horas = 2;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void unaHoraNoPermiteCancelar() {                          // CP-03
        // Arrange
        int horas = 1;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void ceroHorasNoPermiteCancelar() {                        // CP-04
        // Arrange
        int horas = 0;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void anticipacionNegativaNoPermiteCancelar() {             // CP-05
        // Arrange
        int horas = -1;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void tresHorasPermitenCancelar() {                         // CP-06
        // Arrange
        int horas = 3;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertTrue(resultado);
    }

    // ------------------------------------------------ calcularTotal

    @Test
    void normalNoRecibeDescuento() {                           // CP-07
        // Act
        double total = servicio.calcularTotal("NORMAL", 100);

        // Assert
        assertEquals(100.0, total, DELTA);
    }

    @Test
    void vipRecibeQuincePorCiento() {                          // CP-08
        // Act
        double total = servicio.calcularTotal("VIP", 100);

        // Assert
        assertEquals(85.0, total, DELTA);
    }

    @Test
    void estudianteRecibeDiezPorCiento() {                     // CP-09
        // Act
        double total = servicio.calcularTotal("ESTUDIANTE", 100);

        // Assert
        assertEquals(90.0, total, DELTA);
    }

    @Test
    void totalCeroEsValidoYDevuelveCero() {                    // CP-10
        // Act
        double total = assertDoesNotThrow(
                () -> servicio.calcularTotal("VIP", 0));

        // Assert
        assertEquals(0.0, total, DELTA);
    }

    @Test
    void totalNegativoEsInvalido() {                           // CP-11
        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.calcularTotal("NORMAL", -1));

        // Assert
        assertEquals("Total base inválido", ex.getMessage());
    }

    @Test
    void totalApenasNegativoTambienEsInvalido() {              // CP-12
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.calcularTotal("VIP", -0.01));
    }

    @Test
    void tipoVipEnMinusculaTambienRecibeDescuento() {          // CP-13
        // Act
        double total = servicio.calcularTotal("vip", 100);

        // Assert
        assertEquals(85.0, total, DELTA);
    }

    @Test
    void tipoDesconocidoNoRecibeDescuento() {                  // CP-14
        // Act
        double total = servicio.calcularTotal("PREMIUM", 100);

        // Assert
        assertEquals(100.0, total, DELTA);
    }

    @Test
    void tipoNuloSeTrataComoNormal() {                         // CP-15
        // Act
        double total = assertDoesNotThrow(
                () -> servicio.calcularTotal(null, 100));

        // Assert
        assertEquals(100.0, total, DELTA);
    }
}
