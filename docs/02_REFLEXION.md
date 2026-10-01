# Microexperimento y reflexión

## Microexperimento: `>= 2` → `> 2`

Cambié temporalmente la regla de cancelación:

```java
return horasAnticipacion > 2;   // original: >= 2
```

y ejecuté `mvn test`:

```
[ERROR] ReservaServiceTest.dosHorasEsElLimitePermitido:52 expected: <true> but was: <false>
[ERROR] Tests run: 16, Failures: 1, Errors: 0, Skipped: 0
[INFO] BUILD FAILURE
```

Falló **una sola prueba**, la del límite. Las de 5 horas y 3 horas siguieron
verdes porque para cualquier valor mayor que 2 los operadores `>` y `>=` dan
el mismo resultado; la de 1 y 0 horas también, porque en ambos casos da
`false`. Después restauré el código con `git checkout` y la suite volvió a
`Tests run: 16, Failures: 0` (`docs/evidencia/02` y `03`, captura
`docs/capturas/cap3-experimento-frontera.png`).

## ¿Qué prueba detecta mejor un error de frontera y por qué?

`dosHorasEsElLimitePermitido()` (CP-02). Es la única entrada donde `>` y `>=`
dan resultados distintos: con 2 horas, la regla correcta dice `true` y la
regla con el error dice `false`. Cualquier otro valor da lo mismo con los dos
operadores, así que podría tener cien pruebas con valores normales y ninguna
vería el error. El experimento lo demostró: de 16 pruebas falló solo esa.

Por eso en la matriz probé 1, 2 y 3 horas, y en el descuento -0,01 y 0: los
errores de frontera solo aparecen justo en el borde, y un caso límite bien
elegido vale más que muchos casos normales repetidos.
