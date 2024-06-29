package com.example.api

import com.example.models.ApiResponse
import com.example.models.SendMessageDto
import com.example.models.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FCMApi {
//    @POST("/send")
//    suspend fun sendMessage(
//        @Body body: SendMessageDto
//    )

    @POST("/user")
    suspend fun signUp(@Body body: UserModel):Response<ApiResponse>
}