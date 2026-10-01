package edu.uees.testing.service;

import edu.uees.testing.availability.DisponibilidadClient;
import edu.uees.testing.domain.EstadoReserva;
import edu.uees.testing.domain.Reserva;
import edu.uees.testing.notification.Notificador;
import edu.uees.testing.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Laboratorio 2 de Semana 7: confirmar(Reserva) con dobles de prueba.
 *
 * - disponibilidad: Stub. Con when(...).thenReturn(...) controlo la respuesta
 *   que recibe el servicio, sin un servicio externo real.
 * - repository y notificador: Mocks. Con verify(...) compruebo que el
 *   servicio los llamo (o no), sin base de datos ni servidor de correo.
 */
class ConfirmarReservaTest {

    private DisponibilidadClient disponibilidad;
    private ReservaRepository repository;
    private Notificador notificador;
    private ReservaService servicio;

    @BeforeEach
    void preparar() {
        disponibilidad = mock(DisponibilidadClient.class);
        repository = mock(ReservaRepository.class);
        notificador = mock(Notificador.class);
        servicio = new ReservaService(disponibilidad, repository, notificador);
    }

    @Test
    void reservaDisponibleSeConfirmaGuardaYNotifica() {
        // Arrange
        when(disponibilidad.estaDisponible(any())).thenReturn(true);   // Stub
        Reserva reserva = new Reserva("R-001", "NORMAL");

        // Act
        servicio.confirmar(reserva);

        // Assert
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        verify(repository).guardar(reserva);                           // Mock
        verify(notificador).enviarConfirmacion(reserva);               // Mock
    }

    @Test
    void reservaNoDisponibleNoSeGuardaNiNotifica() {
        // Arrange
        when(disponibilidad.estaDisponible(any())).thenReturn(false);  // Stub
        Reserva reserva = new Reserva("R-002", "NORMAL");

        // Act
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.confirmar(reserva));

        // Assert
        assertEquals("Horario no disponible", ex.getMessage());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        verify(repository, never()).guardar(any());                    // Mock
        verify(notificador, never()).enviarConfirmacion(any());        // Mock
    }

    @Test
    void reservaNulaNoConsultaDependencias() {
        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.confirmar(null));

        // Assert
        assertEquals("Reserva obligatoria", ex.getMessage());
        verify(disponibilidad, never()).estaDisponible(any());
        verify(repository, never()).guardar(any());
        verify(notificador, never()).enviarConfirmacion(any());
    }

    @Test
    void consultaLaDisponibilidadDeLaMismaReserva() {
        // Arrange
        Reserva reserva = new Reserva("R-003", "VIP");
        when(disponibilidad.estaDisponible(reserva)).thenReturn(true);

        // Act
        servicio.confirmar(reserva);

        // Assert: se pregunto por ESTA reserva, no por cualquiera
        verify(disponibilidad).estaDisponible(reserva);
    }

    @Test
    void guardaAntesDeNotificar() {
        // Arrange
        when(disponibilidad.estaDisponible(any())).thenReturn(true);
        Reserva reserva = new Reserva("R-004", "NORMAL");

        // Act
        servicio.confirmar(reserva);

        // Assert: no se avisa al estudiante de algo que aun no se guardo
        InOrder orden = inOrder(repository, notificador);
        orden.verify(repository).guardar(reserva);
        orden.verify(notificador).enviarConfirmacion(reserva);
    }

    @Test
    void sinDisponibilidadNoSeTocanRepositorioNiNotificador() {
        // Arrange
        when(disponibilidad.estaDisponible(any())).thenReturn(false);
        Reserva reserva = new Reserva("R-005", "NORMAL");

        // Act
        assertThrows(IllegalStateException.class, () -> servicio.confirmar(reserva));

        // Assert: ni siquiera otra llamada distinta a guardar/enviar
        verifyNoInteractions(repository, notificador);
    }
}
