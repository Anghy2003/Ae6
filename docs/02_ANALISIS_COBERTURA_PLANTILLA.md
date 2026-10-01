# Análisis de cobertura · Ae6

Reporte: `mvn clean test` → `target/site/jacoco/index.html`. Los CSV de cada
momento están en `docs/ae6/evidencia/` y las capturas en `docs/ae6/capturas/`.

## Resultado observado

| Momento | Pruebas | `ReservaService` líneas | `ReservaService` ramas | `Reserva` líneas | `Reserva` ramas |
|---|---|---|---|---|---|
| Proyecto base | 1 | 0 / 21 (0 %) | 0 / 12 (0 %) | 0 / 14 (0 %) | 0 / 6 (0 %) |
| Después de la Actividad 1 | 16 | 13 / 21 (62 %) | 8 / 12 (67 %) | 0 / 14 (0 %) | 0 / 6 (0 %) |
| Inicio de Ae6 (tras Actividad 2) | 28 | 21 / 21 (100 %) | 12 / 12 (100 %) | 14 / 14 (100 %) | 6 / 6 (100 %) |
| Final de Ae6 | 29 | 21 / 21 (100 %) | 12 / 12 (100 %) | 14 / 14 (100 %) | 6 / 6 (100 %) |

- Cobertura de líneas final: 39 / 39 (100 %). Ramas: 18 / 18 (100 %).
- Clase analizada: `ReservaService` (4 métodos) y `Reserva`.

## Métodos y ramas con menor cobertura

En el proyecto base todo estaba en 0 %: la única prueba era `assertTrue(true)`.
Después de la Actividad 1, el método con menor cobertura era
`confirmar(Reserva)` con **0 %**, y las 4 ramas que faltaban de
`ReservaService` eran las suyas (líneas 44 y 48: reserva nula y
disponibilidad). No se ejecutaba porque depende de tres colaboradores y en la
Actividad 1 el servicio se construía con `null`. `Reserva` también estaba en
0 %. Esos huecos los cubrí en la Actividad 2 con Stub, Mock y `ReservaTest`.

## Hueco relevante que el porcentaje no muestra

Al empezar la Ae6 el reporte ya estaba al 100 %: no quedaba ninguna línea roja
ni rombo amarillo. Entonces cambié la pregunta: en vez de buscar líneas sin
ejecutar, busqué **errores que la suite dejaría pasar**. Introduje uno por uno
12 errores pequeños y realistas en el código productivo (cambiar `>=` por `>`,
cruzar los factores de descuento, invertir guardar y notificar, aceptar id en
blanco, etc.), corrí la suite con cada uno y restauré el código. El script es
`docs/ae6/mutantes.py`.

Resultado (`docs/ae6/evidencia/mutantes-inicio.txt`): **11 de 12 detectados**.
Sobrevivió uno:

| Error | Línea | Qué pasó |
|---|---|---|
| M07: `"ESTUDIANTE".equalsIgnoreCase(tipo)` → `"ESTUDIANTE".equals(tipo)` | 36 | Ninguna prueba falló. La línea 36 estaba en verde y su rama (◆) cubierta en los dos sentidos, porque `estudianteRecibeDiezPorCiento` usa `"ESTUDIANTE"` en mayúsculas y otras pruebas usan tipos que no son ESTUDIANTE. |

El comportamiento que faltaba proteger es la regla R2: el tipo no distingue
mayúsculas. Para VIP ya existía `tipoVipEnMinusculaTambienRecibeDescuento`
(CP-13), pero para ESTUDIANTE no. Con el error, un estudiante registrado como
`"estudiante"` pagaría 100 en lugar de 90.

## Prueba incorporada a partir del análisis

```java
@Test
void estudianteEnMinusculaTambienRecibeDescuento() {       // CP-28
    // Act
    double total = servicio.calcularTotal("estudiante", 100);

    // Assert
    assertEquals(90.0, total, DELTA);
}
```

Volví a correr el experimento (`mutantes-final.txt`): **12 de 12
detectados**; M07 ahora lo detecta CP-28. La cobertura no cambió (seguía en
100 %), pero la suite sí mejoró.

## Por qué cobertura alta no garantiza corrección

JaCoCo cuenta qué líneas y ramas se ejecutaron, no qué se comprobó al
ejecutarlas. En este proyecto hubo tres pruebas de eso:

1. Con 100 % de líneas y ramas, el error M07 pasaba sin que fallara nada.
2. En la Actividad 2, invertir `guardar` y `notificar` mantiene la cobertura
   en 100 % (se ejecutan las mismas líneas); solo lo detectan las pruebas con
   `InOrder` y `doThrow` (M09).
3. Una prueba que llama a un método sin hacer ninguna aserción sube la
   cobertura exactamente igual que una buena prueba.

La cobertura sirve para encontrar código que **seguro** no está probado; no
sirve para afirmar que lo que está en verde está bien probado.

## Decisiones

- **¿Qué prueba nueva se añadió?** `estudianteEnMinusculaTambienRecibeDescuento` (CP-28).
- **¿Qué riesgo protege?** Que un estudiante pierda su descuento por cómo se escribió su tipo.
- **¿Por qué no basta con el porcentaje?** Porque el hueco existía con 100 %
  y la prueba que lo cierra no movió el porcentaje.
