package com.example.api

import com.example.models.SendMessageDto
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMApi {
    @POST("/send")
    suspend fun sendMessage(
        @Body body: SendMessageDto
    )
}