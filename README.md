# UEES UCOM0310 — Semana 7 — Proyecto base Ae6

Proyecto base para las actividades individuales de Semana 7.

## Requisitos
- Java 21
- Maven 3.9+
- Git

## Verificación inicial
```bash
mvn clean test
```

## Cobertura
```bash
mvn clean test
```

Luego abrir:
`target/site/jacoco/index.html`

## Regla de trabajo
No modifiques el código productivo solo para hacer pasar una prueba sin justificar el cambio.
Primero diseña el caso, luego implementa la prueba y finalmente interpreta el resultado.

---

## Actividad 1 · Laboratorio de diseño de casos y JUnit 5

Autora: Andrea Illescas · Diseño de Software (UCOM0310) · Semana 7 · Repositorio: https://github.com/Anghy2003/Ae6

| Entregable | Dónde |
|---|---|
| Matriz de casos (15 casos: normales, alternativos, límite, extremo e inválidos) | [`docs/01_MATRIZ_CASOS.md`](docs/01_MATRIZ_CASOS.md) |
| Pruebas JUnit 5 con AAA (15 + prueba de entorno) | [`src/test/java/edu/uees/testing/service/ReservaServiceTest.java`](src/test/java/edu/uees/testing/service/ReservaServiceTest.java) |
| Evidencia de `mvn clean test` | `docs/evidencia/01-suite-verde.txt`, `docs/capturas/cap2-suite-verde.png` |
| Microexperimento `>= 2` → `> 2` | `docs/evidencia/02-experimento-frontera.txt`, `docs/capturas/cap3-experimento-frontera.png` |
| Reflexión | [`docs/02_REFLEXION.md`](docs/02_REFLEXION.md) |

```bash
mvn clean test    # Tests run: 16, Failures: 0 -> BUILD SUCCESS
```

---

## Actividad 2 · Stub, Mock, JaCoCo y Git

Rama: `test/lab2-dobles-cobertura` (creada desde `main`, conserva las pruebas de la Actividad 1).

| Entregable | Dónde |
|---|---|
| Pruebas de `confirmar()` con Stub y Mock (7) | [`ConfirmarReservaTest.java`](src/test/java/edu/uees/testing/service/ConfirmarReservaTest.java) |
| Pruebas agregadas a partir de JaCoCo (5) | [`ReservaTest.java`](src/test/java/edu/uees/testing/domain/ReservaTest.java) |
| Análisis de cobertura | [`docs/02_ANALISIS_COBERTURA.md`](docs/02_ANALISIS_COBERTURA.md) |
| Reflexión Stub vs Mock | [`docs/03_REFLEXION_STUB_MOCK.md`](docs/03_REFLEXION_STUB_MOCK.md) |
| Salidas de `mvn clean test`, CSV de JaCoCo y capturas | `docs/lab2/` |

```bash
git switch test/lab2-dobles-cobertura
mvn clean test                       # 28 pruebas, BUILD SUCCESS
start target/site/jacoco/index.html  # 100 % lineas y ramas
```
