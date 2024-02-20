package com.example.belya.service

import com.example.belya.Constant
import com.example.belya.model.notifiactionFCM.MyResponse
import com.example.belya.model.notifiactionFCM.Sender
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {
    @Headers(
        "Content-Type:${Constant.CONTENT_TYPE}",
        "Authorization:key=${Constant.SERVER_KEY}"
    )
    @POST("fcm/send")
    suspend fun sendNotification(@Body sender : Sender) : Response<MyResponse>
}
