package com.example.foodapplication.retrofit.api

import com.example.belya.api.modeApi.UserApiModel
import retrofit2.Response
import retrofit2.http.GET

interface WebServices {

    @GET("users_with_high_person_rate")
    suspend fun getPersonFromRate(): Response<UserApiModel>
}