package com.capstoneproject.edusign.data.repository.remote2


import com.capstoneproject.edusign.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig2 {
    companion object {
        fun getApiService2(): ApiService2 {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
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
                .baseUrl("https://edusignapp-bangkit2023.et.r.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService2::class.java)
        }
    }
}