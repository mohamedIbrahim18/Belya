package com.example.belya.service

import com.example.belya.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object FCM {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api by lazy {
        retrofit.create(Api::class.java)
    }
}