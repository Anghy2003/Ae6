# test: completar suite, cobertura y evidencia de Ae6

**Base:** `main` · **Compare:** `ae6/suite-pruebas`

## Objetivo

Proteger con pruebas automatizadas las reglas críticas del módulo de reservas:
cancelación con 2 o más horas, descuentos por tipo de cliente, validación del
total y confirmación con disponibilidad, guardado y notificación. La suite
parte de los dos laboratorios de la Semana 7 y en la Ae6 se completa la
matriz, se vincula cada prueba con su caso y se agrega la prueba que salió del
análisis de cobertura.

## Cambios realizados

- `docs/01_MATRIZ_CASOS_PLANTILLA.md`: matriz unificada con 6 reglas y 28 casos (normales, alternativos, límite, inválidos, excepciones e interacciones).
- `ConfirmarReservaTest` y `ReservaTest`: cada prueba lleva su ID de la matriz (CP-16 a CP-27). Sin cambios de lógica.
- `ReservaServiceTest`: nueva `estudianteEnMinusculaTambienRecibeDescuento` (CP-28), agregada a partir del análisis.
- `docs/02_ANALISIS_COBERTURA_PLANTILLA.md`: análisis de JaCoCo e inserción de errores.
- `docs/ae6/`: CSV de JaCoCo de cada momento, resultado del experimento, script `mutantes.py` y capturas.
- No se modificó código productivo (`src/main`).

## Casos de prueba

| Área | Casos | Técnica |
|---|---|---|
| Cancelación | 5 h, 3 h, **2 h (límite)**, 1 h, 0 h, -1 h | Valores límite alrededor de 2 |
| Descuentos | NORMAL, VIP, ESTUDIANTE, `vip`, `estudiante`, PREMIUM, `null` | Clases de equivalencia |
| Total | 0 (válido), -0.01 y -1 (excepción con mensaje) | Frontera de la validación |
| Confirmación | disponible, no disponible, reserva nula, guardar falla | **Stub** de disponibilidad + **Mock** de repositorio y notificador |
| Interacciones | se consulta la misma reserva; guardar antes de notificar; sin disponibilidad no hay ninguna interacción | `verify`, `never()`, `InOrder`, `verifyNoInteractions` |
| Reserva | estado inicial, id nulo, id en blanco, tipo nulo, cancelar | Estado del dominio |

**Dobles de prueba:** `DisponibilidadClient` es un Stub (controlo su respuesta
con `when(...).thenReturn(...)` para elegir el camino); `ReservaRepository` y
`Notificador` son Mocks (sus métodos son `void` y la única evidencia de que se
guardó o notificó es `verify`). `Reserva` no se mockea: es un objeto simple
de dominio y se usa el real.

## Cómo verificar

```bash
git switch ae6/suite-pruebas
mvn clean test          # Tests run: 29, Failures: 0, Errors: 0 -> BUILD SUCCESS
# reporte: target/site/jacoco/index.html
python docs/ae6/mutantes.py salida.txt   # opcional: 12 errores, 12 detectados
```

## Cobertura

- 100 % de líneas (39/39) y ramas (18/18) en `ReservaService` y `Reserva`.
- El 100 % ya estaba al inicio de Ae6. Para ver qué faltaba introduje 12
  errores pequeños en el código: la suite detectó 11. Sobrevivió cambiar
  `equalsIgnoreCase` por `equals` en la rama ESTUDIANTE. CP-28 lo cubre y
  ahora se detectan 12 de 12. El porcentaje no cambió; la suite sí mejoró.

## Limitaciones

- `confirmar()` llama a `reserva.confirmar()` antes de `repository.guardar()`.
  Si guardar falla, la reserva queda CONFIRMADA en memoria sin estar
  guardada. Está documentado y no se corrige en este PR porque cambia código
  productivo.
- `confirmar()` no revisa el estado: una reserva CANCELADA con disponibilidad
  se vuelve a confirmar. No hay regla de negocio que diga qué debería pasar.
- `puedeCancelar()` solo recibe horas enteras; no hay casos con fracciones.
- Los dobles cubren el contrato de las interfaces; no hay pruebas de
  integración con una base de datos o un servidor de correo reales.
- El experimento de errores es manual (12 errores elegidos por mí), no una
  herramienta de mutación completa como PIT.

## Autorrevisión

- [x] Compila (`mvn clean test`, BUILD SUCCESS)
- [x] Pruebas en verde (29)
- [x] Sin archivos accidentales (`target/` ignorado; ver `docs/ae6/04_AUTORREVISION.md`)
- [x] Commits descriptivos (uno por paso)
- [x] Documentación actualizada (matriz, análisis, README)

## Uso de IA

Usé un asistente de inteligencia artificial como apoyo para
organizar la documentación y revisar la redacción. Diseñé los casos, escribí
y ejecuté las pruebas, hice el experimento de errores, los commits y este PR,
y puedo explicar cada prueba y cada decisión.
