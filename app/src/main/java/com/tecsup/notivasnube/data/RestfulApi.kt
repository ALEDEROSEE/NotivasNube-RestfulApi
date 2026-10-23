package com.tecsup.notivasnube.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Los cuatro verbos sobre https://api.restful-api.dev/objects.
 *
 * Ojo: `GET objects` solo devuelve los 13 objetos de fábrica del servicio, nunca los que yo creo.
 * Los míos se piden por id repitiendo el parámetro: `objects?id=A&id=B`.
 */
interface RestfulApi {
    @GET("objects")
    suspend fun porIds(@Query("id") ids: List<String>): List<NotaRemota>

    @GET("objects/{id}")
    suspend fun porId(@Path("id") id: String): Response<NotaRemota>

    @POST("objects")
    suspend fun crear(@Body nota: NotaRemota): Response<NotaRemota>

    @PUT("objects/{id}")
    suspend fun actualizar(@Path("id") id: String, @Body nota: NotaRemota): Response<NotaRemota>

    @DELETE("objects/{id}")
    suspend fun borrar(@Path("id") id: String): Response<Unit>
}
