# Reflexión: diferencia práctica entre Stub y Mock

En `confirmar()` usé los dos con la misma herramienta (Mockito), pero para
cosas distintas.

**El Stub controla lo que entra al servicio.** `DisponibilidadClient` es una
pregunta: ¿está disponible? Con `when(disponibilidad.estaDisponible(any())).thenReturn(true)`
decido la respuesta sin tener un servicio externo real, y así puedo llevar
al código por el camino que quiero probar. Con `true` pruebo la
confirmación; con `false`, el rechazo. Al Stub no le hago `verify` de la
consulta en cada prueba: lo que compruebo es lo que el servicio hace con esa
respuesta.

**El Mock comprueba lo que sale del servicio.** `ReservaRepository.guardar()`
y `Notificador.enviarConfirmacion()` no devuelven nada (`void`), así que no
hay un valor que revisar con `assertEquals`. La única forma de saber si el
servicio guardó o notificó es preguntarle al doble si lo llamaron:
`verify(repository).guardar(reserva)`. Y cuando el flujo se detiene, lo
importante es lo contrario: `verify(repository, never()).guardar(any())`.

La diferencia práctica que noté: si quito el `when(...)` de una prueba, el
Stub devuelve `false` por defecto y el servicio lanza una excepción; la
prueba falla porque **cambió el camino**. Si quito un `verify(...)`, la
prueba sigue pasando aunque el servicio deje de guardar; se pierde
protección sin que nada avise. Por eso los `verify` son los que hay que
elegir con más cuidado.

También aprendí que `verify` normal no revisa el orden. La prueba de la guía
siguió verde cuando invertí guardar y notificar; solo la detectaron las
pruebas con `InOrder` y con `doThrow`. Un Mock verifica exactamente lo que yo
le pido, y nada más.
