package com.example.project_kotlin.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        fun getClient(URL:String): Retrofit {
            val retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create()) //lo que haces es convertir nuestro objeto json en un arreglo
                .build()
            return retrofit
        }
    }
}