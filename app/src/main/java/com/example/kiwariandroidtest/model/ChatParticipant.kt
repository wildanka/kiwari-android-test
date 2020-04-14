package com.example.kiwariandroidtest.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatParticipant(
    var participant0: String? = null,
    var participant1: String? = null
)