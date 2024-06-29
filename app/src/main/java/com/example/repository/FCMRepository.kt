package com.example.repository

import com.example.api.ApiService
import com.example.api.FCMApi
import com.example.models.ApiResponse
import com.example.models.SendMessageDto
import com.example.models.UserModel
import okhttp3.Response
import javax.inject.Inject

class FCMRepository
@Inject constructor(private val fcmApi: FCMApi){
//    suspend fun sendMessage(sendMessageDto: SendMessageDto) = apiService.sendMessage(sendMessageDto)

    suspend fun signUp(userModel: UserModel)=fcmApi.signUp(userModel)
}