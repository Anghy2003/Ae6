# Matriz de casos · Ae6

Matriz unificada de la Ae6. Reúne los casos de la Actividad 1 (CP-01 a CP-15,
detalle en `01_MATRIZ_CASOS.md`) y los de la Actividad 2 (CP-16 a CP-27), con
una sola numeración. Cada caso tiene una prueba JUnit que lleva su ID como
comentario.

## Reglas de negocio

| Regla | Método | Fuente |
|---|---|---|
| R1. Se puede cancelar con 2 o más horas de anticipación | `ReservaService.puedeCancelar(int)` | `return horasAnticipacion >= 2;` |
| R2. VIP 15 % de descuento, ESTUDIANTE 10 %, el resto sin descuento; el tipo no distingue mayúsculas | `ReservaService.calcularTotal(String, double)` | `equalsIgnoreCase`, factores 0.85 y 0.90 |
| R3. El total base no puede ser negativo | `ReservaService.calcularTotal(String, double)` | `IllegalArgumentException("Total base inválido")` |
| R4. Solo se confirma si hay disponibilidad; al confirmar se guarda y luego se notifica | `ReservaService.confirmar(Reserva)` | depende de 3 colaboradores |
| R5. La reserva es obligatoria | `ReservaService.confirmar(Reserva)` | `IllegalArgumentException("Reserva obligatoria")` |
| R6. Una reserva necesita id; sin tipo es NORMAL; nace PENDIENTE | `Reserva` | constructor |

## Casos

| ID | Regla | Escenario | Entrada | Esperado | Tipo | Riesgo que protege | Doble |
|---|---|---|---|---|---|---|---|
| CP-01 | R1 | Anticipación habitual | 5 | `true` | Normal | Regla general | — |
| CP-02 | R1 | Límite permitido | 2 | `true` | Límite | Cambiar `>=` por `>` | — |
| CP-03 | R1 | Justo debajo del límite | 1 | `false` | Límite | Frontera inferior | — |
| CP-04 | R1 | Sin anticipación | 0 | `false` | Extremo | Cancelación inmediata | — |
| CP-05 | R1 | Anticipación negativa | -1 | `false` | Inválido | Hora ya pasada | — |
| CP-06 | R1 | Justo encima del límite | 3 | `true` | Límite | Completa 2-1, 2, 2+1 | — |
| CP-07 | R2 | NORMAL | `NORMAL`, 100 | 100 | Normal | Sin descuento | — |
| CP-08 | R2 | VIP | `VIP`, 100 | 85 | Alternativo | Factor 0,85 | — |
| CP-09 | R2 | ESTUDIANTE | `ESTUDIANTE`, 100 | 90 | Alternativo | Factor 0,90; cruzar factores | — |
| CP-10 | R3 | Total cero | `VIP`, 0 | 0 | Límite | 0 es válido | — |
| CP-11 | R3 | Total negativo | `NORMAL`, -1 | `IllegalArgumentException` + mensaje | Excepción | La validación existe | — |
| CP-12 | R3 | Negativo mínimo | `VIP`, -0.01 | `IllegalArgumentException` | Límite / excepción | Condición corrida (`< -1`) | — |
| CP-13 | R2 | VIP en minúscula | `vip`, 100 | 85 | Alternativo | Uso de `equalsIgnoreCase` | — |
| CP-14 | R2 | Tipo desconocido | `PREMIUM`, 100 | 100 | Alternativo | Rama sin descuento | — |
| CP-15 | R2 | Tipo nulo | `null`, 100 | 100 | Inválido | Sin `NullPointerException` | — |
| CP-16 | R4 | Reserva disponible | `R-001` + disponible | CONFIRMADA; guarda y notifica esa reserva | Normal | Se pierde guardar o notificar | Stub `true` + 2 Mocks |
| CP-17 | R4 | Sin disponibilidad | `R-002` + no disponible | `IllegalStateException`; sigue PENDIENTE; no guarda ni notifica | Alternativo / excepción | Guardar algo rechazado | Stub `false` + `never()` |
| CP-18 | R5 | Reserva nula | `null` | `IllegalArgumentException`; no consulta ninguna dependencia | Inválido / excepción | Consultar servicios con datos inválidos | 3 Mocks + `never()` |
| CP-19 | R4 | Consulta la reserva correcta | `R-003` | `estaDisponible(R-003)` | Interacción | Preguntar por otra reserva | Stub con argumento exacto |
| CP-20 | R4 | Orden guardar → notificar | `R-004` + disponible | guardar antes que notificar | Interacción | Avisar algo no guardado | `InOrder` |
| CP-21 | R4 | Sin disponibilidad, cero interacciones | `R-005` + no disponible | ninguna llamada a repositorio ni notificador | Alternativo | Efectos laterales inesperados | `verifyNoInteractions` |
| CP-22 | R4 | Guardar falla | `R-006` + `guardar` lanza error | no se notifica | Excepción | Correo de algo no guardado | `doThrow` |
| CP-23 | R6 | Reserva nueva | `R-001`, `VIP` | PENDIENTE; conserva id y tipo | Normal | Estado inicial | — |
| CP-24 | R6 | Id nulo | `null`, `NORMAL` | `IllegalArgumentException("Id obligatorio")` | Inválido / excepción | Reserva sin id | — |
| CP-25 | R6 | Id en blanco | `"   "` | `IllegalArgumentException` | Límite / inválido | Segunda parte del `\|\|` | — |
| CP-26 | R6 | Tipo nulo | `R-002`, `null` | tipo `NORMAL` | Alternativo | Valor por defecto | — |
| CP-27 | R6 | Cancelar | `R-003` | CANCELADA | Normal | Transición de estado | — |
| CP-28 | R2 | ESTUDIANTE en minúscula | `estudiante`, 100 | 90 | Alternativo | `equalsIgnoreCase` en la rama ESTUDIANTE. **Agregado en Ae6 a partir del análisis de cobertura** | — |

## Resumen

| Tipo | Casos |
|---|---|
| Normal | CP-01, CP-07, CP-16, CP-23, CP-27 |
| Alternativo | CP-08, CP-09, CP-13, CP-14, CP-17, CP-21, CP-26, CP-28 |
| Límite | CP-02, CP-03, CP-06, CP-10, CP-12, CP-25 |
| Extremo | CP-04 |
| Inválido / excepción | CP-05, CP-11, CP-15, CP-18, CP-22, CP-24 |
| Interacción | CP-19, CP-20 |

28 casos (mínimo recomendado: 10). CP-28 se agregó después del análisis de cobertura. Los mínimos del enunciado están cubiertos:
cancelación normal, límite válido y límite inválido (CP-01, CP-02, CP-03);
NORMAL, VIP, ESTUDIANTE y negativo (CP-07, CP-08, CP-09, CP-11); disponible,
no disponible y nula (CP-16, CP-17, CP-18).
