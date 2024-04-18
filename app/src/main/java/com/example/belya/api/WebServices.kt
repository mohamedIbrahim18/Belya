package com.example.belya.api

import com.example.belya.api.modeApi.UserApiModelItem
import retrofit2.Response
import retrofit2.http.GET

interface WebServices {

    @GET("users_with_high_person_rate")
    suspend fun getPersonFromRate(): Response<List<UserApiModelItem>>
}