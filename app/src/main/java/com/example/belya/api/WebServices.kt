package com.example.belya.api

import com.example.belya.api.modeApi.SearchRequestByCity
import com.example.belya.api.modeApi.SearchRequestByJob
import com.example.belya.api.modeApi.UserApiModelItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WebServices {

    @GET("/users_with_high_person_rate")
    suspend fun getPersonFromRate(): Response<List<UserApiModelItem>>

    @POST("/search_users")
    suspend fun searchUsersByJob(@Body request: SearchRequestByJob): Response<List<UserApiModelItem>>

    @POST("/search_users_by_city")
    suspend fun searchUserByCity(@Body request:SearchRequestByCity) : Response<List<UserApiModelItem>>
}