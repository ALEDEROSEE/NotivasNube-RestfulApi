package com.tecsup.notivasnube.data

/** El servidor respondió, pero con un código que no es 2xx. */
class ApiException(val codigo: Int, mensaje: String) : Exception(mensaje)

interface NotaRepository {
    suspend fun listar(): List<Nota>
    suspend fun obtener(id: String): Nota
    suspend fun crear(cursoId: Int, nombre: String, peso: Int, valor: Double): Nota
    suspend fun actualizar(nota: Nota): Nota
    suspend fun borrar(id: String)
}

class RemoteNotaRepository(
    private val api: RestfulApi,
    private val ids: IdsStore
) : NotaRepository {

    override suspend fun listar(): List<Nota> {
        val mios = ids.leer()
        if (mios.isEmpty()) return emptyList()
        val remotas = api.porIds(mios).mapNotNull { it.aNota() }
        // Si alguien borró un objeto desde fuera, deja de venir en la respuesta: lo quito de mi lista de ids.
        val vivos = remotas.map { it.id }.toSet()
        if (vivos.size != mios.size) ids.guardar(mios.filter { it in vivos })
        val orden = mios.withIndex().associate { it.value to it.index }
        return remotas.sortedBy { orden[it.id] ?: Int.MAX_VALUE }
    }

    override suspend fun obtener(id: String): Nota {
        val r = api.porId(id)
        if (!r.isSuccessful) throw ApiException(r.code(), "GET objects/$id respondió ${r.code()}")
        return r.body()?.aNota() ?: throw ApiException(r.code(), "El objeto $id no tiene los datos de una nota")
    }

    override suspend fun crear(cursoId: Int, nombre: String, peso: Int, valor: Double): Nota {
        val r = api.crear(NotaRemota(name = nombre, data = NotaData(cursoId, peso, valor)))
        if (!r.isSuccessful) throw ApiException(r.code(), "POST objects respondió ${r.code()}")
        val nueva = r.body()?.aNota() ?: throw ApiException(r.code(), "El servidor no devolvió el id")
        ids.guardar(ids.leer() + nueva.id)
        return nueva
    }

    override suspend fun actualizar(nota: Nota): Nota {
        val r = api.actualizar(nota.id, nota.aRemota())
        if (!r.isSuccessful) throw ApiException(r.code(), "PUT objects/${nota.id} respondió ${r.code()}")
        return r.body()?.aNota()?.copy(id = nota.id) ?: nota
    }

    override suspend fun borrar(id: String) {
        val r = api.borrar(id)
        // 404 = ya no existía: el resultado que yo quería (que no esté) es el mismo.
        if (!r.isSuccessful && r.code() != 404) throw ApiException(r.code(), "DELETE objects/$id respondió ${r.code()}")
        ids.guardar(ids.leer() - id)
    }
}
