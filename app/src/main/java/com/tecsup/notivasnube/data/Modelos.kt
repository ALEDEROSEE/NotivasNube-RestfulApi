package com.tecsup.notivasnube.data

import com.google.gson.annotations.SerializedName

/**
 * Lo que de la tabla `nota` de NOTIVas no cabe en el campo `name` del servidor.
 * restful-api.dev guarda cualquier JSON dentro de `data`, así que ahí van el curso, el peso y el valor.
 */
data class NotaData(
    @SerializedName("cursoId") val cursoId: Int,
    @SerializedName("peso") val peso: Int,
    @SerializedName("valor") val valor: Double
)

/** Un objeto tal cual lo manda y lo recibe el servidor. `name` es el nombre de la evaluación. */
data class NotaRemota(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("data") val data: NotaData?
)

/** La fila que ve la interfaz: la misma idea que la entidad `Nota` de Room, pero con id de texto. */
data class Nota(
    val id: String,
    val cursoId: Int,
    val nombre: String,
    val peso: Int,
    val valor: Double
)

fun NotaRemota.aNota(): Nota? {
    val d = data ?: return null
    val ident = id ?: return null
    return Nota(ident, d.cursoId, name, d.peso, d.valor)
}

fun Nota.aRemota(): NotaRemota = NotaRemota(id = null, name = nombre, data = NotaData(cursoId, peso, valor))
