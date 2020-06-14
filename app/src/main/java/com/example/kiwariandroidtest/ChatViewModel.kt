package com.example.kiwariandroidtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kiwariandroidtest.model.ChatParticipant
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatViewModel : ViewModel() {

    companion object {
        private val CHAT_PARTICIPANT = Firebase.database.reference.child("participant")
    }

    private val liveData: ParticipantLiveData = ParticipantLiveData(CHAT_PARTICIPANT)

    fun getDataSnapshotLiveData(): LiveData<ChatParticipant?> {
        return liveData
    }
}