package edu.uees.testing.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas agregadas a partir del reporte JaCoCo del Laboratorio 2.
 * Antes de esta clase, Reserva tenia 5 lineas y 3 ramas sin ejecutar.
 */
class ReservaTest {

    @Test
    void reservaNuevaIniciaPendienteYConservaSusDatos() { // CP-23
        // Arrange & Act
        Reserva reserva = new Reserva("R-001", "VIP");

        // Assert
        assertEquals("R-001", reserva.getId());
        assertEquals("VIP", reserva.getTipo());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    void idNuloEsInvalido() {                          // CP-24
        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva(null, "NORMAL"));

        // Assert
        assertEquals("Id obligatorio", ex.getMessage());
    }

    @Test
    void idEnBlancoEsInvalido() {                      // CP-25
        // Act & Assert: segunda parte del ||, distinta del null
        assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva("   ", "NORMAL"));
    }

    @Test
    void tipoNuloSeAsumeNormal() {                     // CP-26
        // Act
        Reserva reserva = new Reserva("R-002", null);

        // Assert
        assertEquals("NORMAL", reserva.getTipo());
    }

    @Test
    void cancelarCambiaElEstadoACancelada() {          // CP-27
        // Arrange
        Reserva reserva = new Reserva("R-003", "NORMAL");

        // Act
        reserva.cancelar();

        // Assert
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }
}
