# Matriz de casos de prueba

Diseñé estos casos antes de escribir cualquier `@Test`. Cada fila sale de una
regla de `ReservaService` y dice qué error detectaría si fallara.

## Regla 1 · `puedeCancelar(int horasAnticipacion)`

Regla: se permite cancelar con **2 o más** horas de anticipación (`>= 2`).

| ID | Escenario | Entrada | Esperado | Tipo | Riesgo / justificación | Prueba |
|---|---|---|---|---|---|---|
| CP-01 | Anticipación habitual | 5 | `true` | Normal | Comprueba la regla general. | `cincoHorasPermitenCancelar` |
| CP-02 | Límite permitido | 2 | `true` | Límite | Detecta si alguien escribe `>` en vez de `>=`. | `dosHorasEsElLimitePermitido` |
| CP-03 | Justo debajo del límite | 1 | `false` | Límite | Protege la frontera inferior. | `unaHoraNoPermiteCancelar` |
| CP-04 | Sin anticipación | 0 | `false` | Extremo | No debe permitir cancelación inmediata. | `ceroHorasNoPermiteCancelar` |
| CP-05 | Anticipación negativa | -1 | `false` | Inválido | Una hora ya pasada no puede habilitar la cancelación. | `anticipacionNegativaNoPermiteCancelar` |
| CP-06 | Justo encima del límite | 3 | `true` | Límite | Completa el análisis de valores límite (2-1, 2, 2+1). | `tresHorasPermitenCancelar` |

## Regla 2 · `calcularTotal(String tipo, double totalBase)`

Regla: VIP 15 % de descuento, ESTUDIANTE 10 %, cualquier otro tipo sin
descuento; total base negativo lanza `IllegalArgumentException("Total base inválido")`.

| ID | Escenario | Entrada | Esperado | Tipo | Riesgo / justificación | Prueba |
|---|---|---|---|---|---|---|
| CP-07 | NORMAL | `NORMAL`, 100 | 100 | Normal | Sin descuento. | `normalNoRecibeDescuento` |
| CP-08 | VIP | `VIP`, 100 | 85 | Alternativo | Factor 0,85. | `vipRecibeQuincePorCiento` |
| CP-09 | ESTUDIANTE | `ESTUDIANTE`, 100 | 90 | Alternativo | Factor 0,90; detecta cruzar los dos factores. | `estudianteRecibeDiezPorCiento` |
| CP-10 | Total cero | `VIP`, 0 | 0 | Límite | Frontera de la validación: 0 es válido, no lanza excepción. | `totalCeroEsValidoYDevuelveCero` |
| CP-11 | Total negativo | `NORMAL`, -1 | `IllegalArgumentException` | Inválido | La validación existe y su mensaje es el correcto. | `totalNegativoEsInvalido` |
| CP-12 | Negativo mínimo | `VIP`, -0.01 | `IllegalArgumentException` | Límite / inválido | Detecta si la condición pasa de `< 0` a `< -1` o similar. | `totalApenasNegativoTambienEsInvalido` |
| CP-13 | Tipo en minúscula | `vip`, 100 | 85 | Alternativo | La regla usa `equalsIgnoreCase`; protege esa decisión. | `tipoVipEnMinusculaTambienRecibeDescuento` |
| CP-14 | Tipo desconocido | `PREMIUM`, 100 | 100 | Alternativo | Un tipo no reconocido cae en la rama sin descuento. | `tipoDesconocidoNoRecibeDescuento` |
| CP-15 | Tipo nulo | `null`, 100 | 100 | Inválido / extremo | No lanza `NullPointerException`; se trata como NORMAL. | `tipoNuloSeTrataComoNormal` |

## Resumen por tipo

| Tipo | Casos |
|---|---|
| Normal | CP-01, CP-07 |
| Alternativo | CP-08, CP-09, CP-13, CP-14 |
| Límite | CP-02, CP-03, CP-06, CP-10, CP-12 |
| Extremo | CP-04 |
| Inválido | CP-05, CP-11, CP-15 |

15 casos diseñados (mínimo pedido: 8). `confirmar(Reserva)` queda fuera de este
laboratorio porque depende de colaboradores externos; se prueba con Stub y Mock
en el Laboratorio 2.
