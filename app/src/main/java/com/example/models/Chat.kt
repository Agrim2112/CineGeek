package com.example.models

data class Chat(
    val sender:String="",
    val message:String="",
    val receiver:String="",
    val seen:Boolean = false,
    val url:String="",
    val messageId:String=""
)