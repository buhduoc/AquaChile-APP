package com.duoc.aquachileapp.data.Remote
import okhttp3.OkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.duoc.aquachileapp.BuildConfig
object RetrofitClient {
    val BASE_URL = BuildConfig.AQUA_API_URL
    val cliente = OkHttpClient.Builder().addInterceptor { chain ->
        val req = chain.request()
        val base = BASE_URL.toHttpUrl()
        val token = SesionSegura.token
        val builder = req.newBuilder()
        if (req.url.host == base.host && req.url.port == base.port && req.url.scheme == base.scheme && token != null && req.url.encodedPath != "/api/auth/login") builder.header("Authorization", "Bearer $token")
        chain.proceed(builder.build())
    }.build()
    val api: ApiService by lazy { Retrofit.Builder().baseUrl(BASE_URL).client(cliente).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java) }
    fun foto(valor: Any?): Any? = if (valor is String && valor.startsWith("/api/fotos/")) BASE_URL.trimEnd('/') + valor else valor
}
