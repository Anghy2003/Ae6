import subprocess, sys
SVC = "src/main/java/edu/uees/testing/service/ReservaService.java"
RES = "src/main/java/edu/uees/testing/domain/Reserva.java"
M = [
 ("M01", SVC, "return horasAnticipacion >= 2;", "return horasAnticipacion > 2;", "Cancelación: >= cambiado por >"),
 ("M02", SVC, "if (totalBase < 0)", "if (totalBase <= 0)", "Total cero tratado como inválido"),
 ("M03", SVC, "if (totalBase < 0)", "if (totalBase < -1)", "Validación de negativos corrida"),
 ("M04", SVC, "return totalBase * 0.85;", "return totalBase * 0.90;", "VIP recibe el factor de ESTUDIANTE"),
 ("M05", SVC, "return totalBase * 0.90;", "return totalBase * 0.85;", "ESTUDIANTE recibe el factor de VIP"),
 ("M06", SVC, '"VIP".equalsIgnoreCase(tipo)', '"VIP".equals(tipo)', "VIP sensible a mayúsculas"),
 ("M07", SVC, '"ESTUDIANTE".equalsIgnoreCase(tipo)', '"ESTUDIANTE".equals(tipo)', "ESTUDIANTE sensible a mayúsculas"),
 ("M08", SVC, "        reserva.confirmar();", "", "confirmar() no cambia el estado"),
 ("M09", SVC, "repository.guardar(reserva);@@notificador.enviarConfirmacion(reserva);", None, "Notifica antes de guardar"),
 ("M10", SVC, "if (!disponibilidad.estaDisponible(reserva))", "if (false && !disponibilidad.estaDisponible(reserva))", "Ignora la disponibilidad"),
 ("M11", RES, "if (id == null || id.isBlank())", "if (id == null)", "Acepta id en blanco"),
 ("M12", RES, 'tipo == null ? "NORMAL" : tipo', 'tipo == null ? "VIP" : tipo', "Tipo por defecto incorrecto"),
]
def mutar(texto, a, b):
    if b is None:  # intercambiar dos lineas consecutivas
        x, y = a.split("@@")
        i, j = texto.index(x), texto.index(y)
        assert i < j
        return texto[:i] + y + texto[i + len(x):j] + x + texto[j + len(y):]
    assert a in texto, a
    return texto.replace(a, b, 1)
res = []
for mid, f, a, b, desc in M:
    orig = open(f, "rb").read()
    mut = mutar(orig.decode("utf-8"), a, b).encode("utf-8")
    assert mut != orig, mid
    open(f, "wb").write(mut)
    try:
        r = subprocess.run("mvn -q test", shell=True, capture_output=True)
        out = (r.stdout + r.stderr).decode("utf-8", "replace")
        fallan = sorted({l.split()[1].split(":")[0] for l in out.splitlines()
                         if l.startswith("[ERROR]   ") and "Test." in l})
        estado = "DETECTADO" if r.returncode != 0 else "SOBREVIVE"
    finally:
        open(f, "wb").write(orig)
    if open(f, "rb").read() != orig:
        sys.exit("NO SE RESTAURO " + mid)
    res.append((mid, desc, estado, ", ".join(fallan) or "-"))
    print(mid, estado, "|", res[-1][3], flush=True)
with open(sys.argv[1], "w", encoding="utf-8") as fh:
    fh.write("ID | Error introducido | Resultado | Pruebas que fallaron\n")
    fh.write("\n".join(" | ".join(r) for r in res) + "\n")
    fh.write("Detectados: %d de %d\n" % (sum(r[2] == "DETECTADO" for r in res), len(res)))
