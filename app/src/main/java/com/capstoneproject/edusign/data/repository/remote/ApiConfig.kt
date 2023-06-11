package com.capstoneproject.edusign.data.repository.remote

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object{
        fun getApiService(): ApiService {
            val loggingInterceptor = if(BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(180, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(60, TimeUnit.SECONDS) // Read timeout
                .build()
            val retrofit = Retrofit.Builder()
                //.baseUrl(// Jika type = baseurl1 load url 1 // jika type = baseurl2 load url 2)
                .baseUrl("https://flaskapp-cr-v1-cky5j3e4sq-et.a.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}