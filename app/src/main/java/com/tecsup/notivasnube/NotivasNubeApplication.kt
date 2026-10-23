package com.tecsup.notivasnube

import android.app.Application
import com.tecsup.notivasnube.data.NotaRepository
import com.tecsup.notivasnube.data.PrefsIdsStore
import com.tecsup.notivasnube.data.RemoteNotaRepository
import com.tecsup.notivasnube.data.RestfulApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Inyección de dependencias a mano: un solo lugar donde se arma Retrofit y el repositorio. */
class AppContainer(application: Application) {
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api: RestfulApi = Retrofit.Builder()
        .baseUrl("https://api.restful-api.dev/")
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RestfulApi::class.java)

    val repositorio: NotaRepository = RemoteNotaRepository(api, PrefsIdsStore(application))
}

class NotivasNubeApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
