# Autorrevisión técnica · Ae6

Pull Request: https://github.com/Anghy2003/Ae6/pull/1 (`ae6/suite-pruebas` → `main`)

## Checklist del enunciado

| Punto | Resultado | Cómo lo comprobé |
|---|---|---|
| Revisé *Files changed* línea por línea | Sí | `git diff main..ae6/suite-pruebas`: 19 archivos, solo `src/test` y `docs` |
| No hay `target/` ni archivos generados | Sí | `git ls-files` sin `target/`, `.class`, `jacoco.exec`, `.idea` ni `.iml`; `target/` está en `.gitignore` |
| No hay credenciales o datos sensibles | Sí | búsqueda de `password`, `secret`, `token`, `ghp_`, `api_key` sin resultados |
| Los nombres de pruebas son expresivos | Sí | todos describen escenario y resultado; ninguno tipo `test1()` |
| Las aserciones comprueban comportamiento | Sí | revisé las 29 pruebas: todas tienen `assert…` o `verify`; ninguna llama a un método sin comprobar nada |
| Los casos límite están presentes | Sí | 1, 2 y 3 horas; total 0 y -0.01; id en blanco |
| Los mocks verifican interacciones relevantes | Sí | solo se verifican efectos observables (guardar, notificar, consultar disponibilidad) y su orden; `Reserva` no se mockea |
| La cobertura está interpretada | Sí | `docs/02_ANALISIS_COBERTURA_PLANTILLA.md` |
| Los commits explican la evolución | Sí | 10 commits en la rama, uno por paso |
| El PR indica cómo verificar | Sí | sección «Cómo verificar» del PR con `mvn clean test` |
| No se modificó código productivo | Sí | `git diff main..ae6/suite-pruebas -- src/main` vacío |

## Problemas que encontré y corregí

1. **El script del experimento no restauraba bien los archivos.** La primera
   versión leía y escribía el código convirtiendo los saltos de línea; al
   terminar, Git marcaba `Reserva.java` como modificado. Eso podía
   contaminar los resultados. Lo detecté con `git status`, restauré los
   archivos con `git checkout`, reescribí el script para leer y escribir
   byte a byte y para comprobar después de cada error que el archivo quedó
   idéntico, y repetí el experimento completo. Los resultados finales
   (`mutantes-inicio.txt`) son los de la versión corregida.
2. **Un hueco real en la suite (M07).** Con 100 % de cobertura, un error en la
   rama ESTUDIANTE pasaba sin que fallara nada. Lo corregí con CP-28.
3. **Las pruebas de los laboratorios no tenían ID de la matriz.** No se podía
   ir de un caso a su prueba. Agregué el ID como comentario (CP-16 a CP-27).
4. **El PR se publicó con el título automático y sin descripción.** Lo
   corregí: cambié el título a `test: completar suite, cobertura y evidencia
   de Ae6` y pegué la descripción completa (objetivo, cambios, casos, cómo
   verificar, cobertura, limitaciones y uso de IA), que también está en
   `docs/03_PULL_REQUEST_PLANTILLA.md`.
5. **La autoría de los commits.** Los commits llevaban una línea de coautor de
   la herramienta de IA. La quité de todo el historial del repositorio para
   que la autoría quede a mi nombre; el uso de IA está declarado en el PR y en
   el reporte.

## Lo que revisé y decidí no cambiar

- `entornoJUnitFunciona()` es `assertTrue(true)`: no prueba nada del
  negocio. La dejé porque viene del proyecto base y el enunciado pide no
  eliminar pruebas existentes; no la cuento entre los casos de la matriz.
- `confirmar()` cambia el estado antes de guardar, y no revisa si la reserva
  está CANCELADA. Son decisiones del código productivo y este PR solo agrega
  pruebas; quedan como limitaciones documentadas.
