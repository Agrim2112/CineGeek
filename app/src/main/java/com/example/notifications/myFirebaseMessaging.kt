package com.example.notifications

import com.example.models.Chat
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.messaging


class myFirebaseMessaging:FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        val tok=Firebase.messaging.token
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }


}