package com.tecsup.notivasnube.data

import android.content.Context

/**
 * El servidor no sabe listar "mis" objetos, así que la app recuerda los ids que ha creado.
 * Es la única parte que guarda algo en el teléfono; los datos de las notas viven en el servidor.
 */
interface IdsStore {
    fun leer(): List<String>
    fun guardar(ids: List<String>)
}

class PrefsIdsStore(context: Context) : IdsStore {
    private val prefs = context.getSharedPreferences("notas_nube", Context.MODE_PRIVATE)

    override fun leer(): List<String> =
        prefs.getString("ids", "").orEmpty().split(",").filter { it.isNotBlank() }

    override fun guardar(ids: List<String>) {
        prefs.edit().putString("ids", ids.joinToString(",")).apply()
    }
}
