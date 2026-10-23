package com.tecsup.notivasnube.data

/**
 * Los cursos vienen de la matrícula del ciclo, no los crea el alumno: los dejo fijos igual que en NOTIVas.
 * Lo que sí se crea, se edita y se borra en el servidor son las notas.
 */
data class Curso(
    val id: Int,
    val nombre: String,
    val docente: String,
    val creditos: Int
)

object Cursos {
    val lista = listOf(
        Curso(1, "Programación en Móviles", "Benjamin Pareja", 4),
        Curso(2, "Aplicaciones Empresariales", "Jorge Beeck", 4),
        Curso(3, "Internet de las Cosas", "Jorge Miranda", 3),
        Curso(4, "Taller de Proyectos", "Hugo Ccoaquira", 3),
        Curso(5, "Inglés V", "Área de Idiomas", 2),
        Curso(6, "Ética Profesional", "Área de Humanidades", 2)
    )

    fun porId(id: Int): Curso = lista.firstOrNull { it.id == id } ?: lista.first()
}

/** Promedio ponderado de una lista de notas; null si todavía no hay ninguna o los pesos suman cero. */
fun promedioDe(notas: List<Nota>): Double? {
    if (notas.isEmpty()) return null
    val pesos = notas.sumOf { it.peso }
    if (pesos == 0) return null
    return notas.sumOf { it.valor * it.peso } / pesos
}
