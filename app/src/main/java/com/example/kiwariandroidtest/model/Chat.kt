package com.example.kiwariandroidtest.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
    var senderId: String? = null,
    var receiverId: String? = null,
    var messages: String? = null,
    var timestamp: String? = null
)