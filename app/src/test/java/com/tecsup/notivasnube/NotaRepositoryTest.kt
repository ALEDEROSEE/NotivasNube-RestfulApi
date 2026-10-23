package com.tecsup.notivasnube

import com.google.gson.Gson
import com.tecsup.notivasnube.data.ApiException
import com.tecsup.notivasnube.data.IdsStore
import com.tecsup.notivasnube.data.Nota
import com.tecsup.notivasnube.data.NotaData
import com.tecsup.notivasnube.data.NotaRemota
import com.tecsup.notivasnube.data.RemoteNotaRepository
import com.tecsup.notivasnube.data.RestfulApi
import com.tecsup.notivasnube.data.promedioDe
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response

/** Servicio falso: se comporta como restful-api.dev pero en memoria, sin tocar la red. */
private class ApiFalsa : RestfulApi {
    val objetos = linkedMapOf<String, NotaRemota>()
    var llamadasPorIds = 0
    private var siguiente = 1

    override suspend fun porIds(ids: List<String>): List<NotaRemota> {
        llamadasPorIds++
        return ids.mapNotNull { objetos[it] }
    }

    override suspend fun porId(id: String): Response<NotaRemota> =
        objetos[id]?.let { Response.success(it) } ?: Response.error(404, ResponseBody.create(null, "{}"))

    override suspend fun crear(nota: NotaRemota): Response<NotaRemota> {
        val id = "id-${siguiente++}"
        val guardada = nota.copy(id = id)
        objetos[id] = guardada
        return Response.success(guardada)
    }

    override suspend fun actualizar(id: String, nota: NotaRemota): Response<NotaRemota> {
        if (id !in objetos) return Response.error(404, ResponseBody.create(null, "{}"))
        objetos[id] = nota.copy(id = id)
        return Response.success(objetos[id])
    }

    override suspend fun borrar(id: String): Response<Unit> =
        if (objetos.remove(id) != null) Response.success(Unit) else Response.error(404, ResponseBody.create(null, "{}"))
}

private class IdsEnMemoria(var lista: List<String> = emptyList()) : IdsStore {
    override fun leer() = lista
    override fun guardar(ids: List<String>) {
        lista = ids
    }
}

class NotaRepositoryTest {

    @Test
    fun `crear guarda el id y la nota aparece al listar`() = runBlocking {
        val api = ApiFalsa()
        val ids = IdsEnMemoria()
        val repo = RemoteNotaRepository(api, ids)

        val nueva = repo.crear(1, "Examen parcial", 30, 16.5)

        assertEquals(listOf(nueva.id), ids.lista)
        assertEquals(listOf(nueva), repo.listar())
    }

    @Test
    fun `sin ids guardados no se llama al servidor`() = runBlocking {
        val api = ApiFalsa()
        val repo = RemoteNotaRepository(api, IdsEnMemoria())

        assertTrue(repo.listar().isEmpty())
        assertEquals(0, api.llamadasPorIds)
    }

    @Test
    fun `listar descarta los ids de objetos que ya no existen en el servidor`() = runBlocking {
        val api = ApiFalsa()
        val ids = IdsEnMemoria()
        val repo = RemoteNotaRepository(api, ids)
        val a = repo.crear(1, "Laboratorio 1", 10, 18.0)
        val b = repo.crear(1, "Laboratorio 2", 10, 15.0)
        api.objetos.remove(a.id)   // alguien lo borró desde fuera de la app

        assertEquals(listOf(b), repo.listar())
        assertEquals(listOf(b.id), ids.lista)
    }

    @Test
    fun `actualizar conserva el id y cambia los datos`() = runBlocking {
        val api = ApiFalsa()
        val repo = RemoteNotaRepository(api, IdsEnMemoria())
        val n = repo.crear(2, "Practica", 20, 12.0)

        val cambiada = repo.actualizar(Nota(n.id, 2, "Practica", 20, 14.5))

        assertEquals(n.id, cambiada.id)
        assertEquals(14.5, repo.obtener(n.id).valor, 0.0)
    }

    @Test
    fun `borrar quita el id y un 404 no es error`() = runBlocking {
        val api = ApiFalsa()
        val ids = IdsEnMemoria()
        val repo = RemoteNotaRepository(api, ids)
        val n = repo.crear(3, "Proyecto", 40, 17.0)

        repo.borrar(n.id)
        assertFalse(n.id in ids.lista)
        repo.borrar(n.id)   // ya no existe: el servidor responde 404 y el repositorio lo acepta
    }

    @Test
    fun `obtener lanza ApiException cuando el servidor responde 404`() = runBlocking {
        val repo = RemoteNotaRepository(ApiFalsa(), IdsEnMemoria())
        try {
            repo.obtener("no-existe")
            fail("debia lanzar ApiException")
        } catch (e: ApiException) {
            assertEquals(404, e.codigo)
        }
    }

    @Test
    fun `el JSON del POST no lleva id y guarda el resto en data`() {
        val json = Gson().toJson(NotaRemota(name = "Examen final", data = NotaData(1, 30, 16.5)))
        assertEquals("""{"name":"Examen final","data":{"cursoId":1,"peso":30,"valor":16.5}}""", json)
    }

    @Test
    fun `la respuesta del servidor ignora campos que la app no usa`() {
        val json = """{"id":"abc","name":"Examen","createdAt":1792789834463,"data":{"cursoId":2,"peso":10,"valor":13.0}}"""
        val n = Gson().fromJson(json, NotaRemota::class.java)
        assertEquals("abc", n.id)
        assertEquals(2, n.data?.cursoId)
    }

    @Test
    fun `un objeto de fabrica sin data no se convierte en nota`() {
        val n = Gson().fromJson("""{"id":"2","name":"Apple iPhone 12 Mini","data":null}""", NotaRemota::class.java)
        assertNull(n.data)
    }

    @Test
    fun `promedio ponderado`() {
        val notas = listOf(Nota("a", 1, "x", 30, 10.0), Nota("b", 1, "y", 70, 20.0))
        assertEquals(17.0, promedioDe(notas)!!, 1e-9)
        assertNull(promedioDe(emptyList()))
    }
}
