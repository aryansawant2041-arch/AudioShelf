package com.example.audioshelf

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val api: LibriVoxApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://librivox.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LibriVoxApi::class.java)
    }
}