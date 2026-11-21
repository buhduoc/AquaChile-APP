package com.duoc.aquachileapp.data.Remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    /*
     * IMPORTANTE:
     * En Swagger se usa http://localhost:8080
     * Pero Android debe emular http://10.0.2.2:8080 por que para el emulador(localhost)
     * significa el propio emulador, mientras 10.0.2.2 significa(mi computador)
     * Desde el emulador Android,
     * 10.0.2.2 representa el computador.
     */
    private const val BASE_URL =
        "http://10.0.2.2:8080/"


    private val logging =
        HttpLoggingInterceptor().apply {

            level =
                HttpLoggingInterceptor.Level.BODY
        }


    private val cliente =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()


    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}