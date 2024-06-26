package com.example.belya.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManger {
   companion object{
       private var retrofit: Retrofit? = null
       fun getInstance(): Retrofit {
           if (retrofit == null) {
               retrofit = Retrofit.Builder().baseUrl("http://172.187.225.210:8080/")
                   .addConverterFactory(GsonConverterFactory.create())
                   .build()
           }
           return retrofit!!
       }
       fun getApis(): WebServices {
           return getInstance().create(WebServices::class.java)
       }
   }
}