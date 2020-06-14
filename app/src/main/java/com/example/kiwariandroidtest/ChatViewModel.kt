package com.example.kiwariandroidtest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kiwariandroidtest.model.Chat
import com.example.kiwariandroidtest.model.ChatParticipant
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatViewModel : ViewModel() {
    private val chats: MutableList<Chat> = mutableListOf()
    private var chatListLiveData : MutableLiveData<List<Chat>>? = null
    companion object {
        private val CHAT_PARTICIPANT = Firebase.database.reference.child("participant")
        private val CHAT = Firebase.database.reference.child("chats")
        private val TAG = "ChatViewModel"
    }

    private val participantLiveData: ParticipantLiveData = ParticipantLiveData(CHAT_PARTICIPANT)
    private val chatLiveData: ChatLiveData = ChatLiveData(CHAT)

    fun getChatParticipantSnapshotLiveData(): LiveData<ChatParticipant?> {
        return participantLiveData
    }

    fun getChatsLiveData(): LiveData<List<Chat>>?{
        Log.e(TAG, "getChatsLiveData")
        chatLiveData.value?.let { chats.add(it) }
        chatListLiveData?.value = chats
        Log.e(TAG, "getChatsLiveData $chats")
        return chatListLiveData
    }


}