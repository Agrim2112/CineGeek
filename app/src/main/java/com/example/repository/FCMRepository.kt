package com.example.repository

import com.example.api.ApiService
import com.example.api.FCMApi
import com.example.models.SendMessageDto
import javax.inject.Inject

class FCMRepository
@Inject constructor(private val apiService: FCMApi){
    suspend fun sendMessage(sendMessageDto: SendMessageDto) = apiService.sendMessage(sendMessageDto)
}