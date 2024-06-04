package com.example.models

import com.google.firebase.Timestamp

class ReceiverChatList(
    val userInfo:UserModel,
    val chatInfo:Chat,
    val timestamp: Long,
    val typing:Boolean
    )