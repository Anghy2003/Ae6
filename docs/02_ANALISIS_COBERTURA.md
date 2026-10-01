# Análisis de cobertura (JaCoCo)

Reporte generado con `mvn clean test` en `target/site/jacoco/index.html`.
Los CSV de cada momento están en `docs/lab2/evidencia/` y las capturas en
`docs/lab2/capturas/`.

## Resultado observado

| Momento | Pruebas | `ReservaService` líneas | `ReservaService` ramas | `Reserva` líneas | `Reserva` ramas | Total instrucciones |
|---|---|---|---|---|---|---|
| Antes (solo Actividad 1) | 16 | 13 / 21 (62 %) | 8 / 12 (67 %) | 0 / 14 (0 %) | 0 / 6 (0 %) | 46 / 138 |
| Con Stub y Mock en `confirmar()` | 22 | 21 / 21 (100 %) | 12 / 12 (100 %) | 9 / 14 (64 %) | 3 / 6 (50 %) | 121 / 138 (87 %) |
| Después de leer el reporte | 28 | 21 / 21 (100 %) | 12 / 12 (100 %) | 14 / 14 (100 %) | 6 / 6 (100 %) | 138 / 138 (100 %) |

- Método con menor cobertura al inicio: `confirmar(Reserva)`, 0 %. Nunca se
  ejecutaba porque depende de tres colaboradores y en la Actividad 1 el
  servicio se construía con `null`.
- Clase con menor cobertura después de las pruebas con Mockito: `Reserva`.

## Huecos relevantes (no solo líneas)

1. **La validación del id nunca se probó** (línea 11 en rojo, rama de la
   línea 10 a medias). Qué comportamiento faltaba: que una reserva sin id o
   con id en blanco no pueda existir. Son dos condiciones distintas
   (`id == null` y `id.isBlank()`), por eso JaCoCo marcaba la rama
   incompleta. Pruebas agregadas: `idNuloEsInvalido()` (además comprueba el
   mensaje) e `idEnBlancoEsInvalido()`.
2. **`cancelar()` nunca se ejecutó** (líneas 35–36). Qué faltaba: la
   transición a CANCELADA, que la Ae6 va a necesitar. Prueba agregada:
   `cancelarCambiaElEstadoACancelada()`.
3. **El valor por defecto del tipo** (rama de la línea 14 a medias): nadie
   comprobaba que `tipo == null` se convierte en `"NORMAL"`. Prueba agregada:
   `tipoNuloSeAsumeNormal()`.

## Código cubierto pero mal probado

Con 22 pruebas `ReservaService` ya estaba al 100 %. Aun así encontré un
comportamiento que la cobertura no mostraba: **qué pasa si guardar falla**.
Si el repositorio lanza una excepción, no se debe enviar el correo de
confirmación. Todas las líneas de `confirmar()` ya estaban en verde, así que
el reporte nunca me iba a señalar este caso. Agregué
`siGuardarFallaNoSeNotificaAlEstudiante()` con
`doThrow(...).when(repository).guardar(any())`.

Lo comprobé con un experimento: invertí temporalmente el orden de
`repository.guardar()` y `notificador.enviarConfirmacion()` en
`confirmar()`. Con ese error las líneas ejecutadas son las mismas, así que la
cobertura sigue en 100 %, pero fallaron dos pruebas:

```
[ERROR]   ConfirmarReservaTest.guardaAntesDeNotificar:118
[ERROR]   ConfirmarReservaTest.siGuardarFallaNoSeNotificaAlEstudiante:147
[ERROR] Tests run: 28, Failures: 2, Errors: 0, Skipped: 0
```

La prueba de la guía (`reservaDisponibleSeConfirmaGuardaYNotifica`) siguió
verde, porque `verify` sin `InOrder` no revisa el orden. Después restauré el
código (`docs/lab2/evidencia/05-experimento-orden.txt` y `06-final.txt`).

**Hallazgo que dejo anotado, sin cambiar el código:** en `confirmar()` se
llama a `reserva.confirmar()` *antes* de `repository.guardar()`. Si guardar
falla, la reserva queda en memoria como CONFIRMADA aunque no se haya
guardado. Es una decisión del código productivo, no de las pruebas; la
anoto como posible mejora para la Ae6 en lugar de corregirla aquí.

## Decisiones

- **¿Qué pruebas nuevas se añadieron?** Cinco en `ReservaTest` (id nulo, id
  en blanco, tipo nulo, `cancelar()`, datos de una reserva nueva) y
  `siGuardarFallaNoSeNotificaAlEstudiante()`.
- **¿Qué riesgo protegen?** Que existan reservas sin id, que se pierda la
  transición a CANCELADA, que el tipo por defecto cambie y que se notifique
  algo que no quedó guardado.
- **¿Por qué no basta con el porcentaje?** Porque mide qué líneas se
  ejecutaron, no qué se comprobó. El 100 % de `ReservaService` llegó antes
  que la prueba del fallo al guardar, y el experimento del orden demostró que
  un error real puede convivir con el 100 %. Lo que lo detecta es una
  aserción sobre el comportamiento, no la línea pintada de verde.
