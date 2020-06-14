package com.example.kiwariandroidtest

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kiwariandroidtest.model.Chat
import com.google.firebase.database.*


class ChatLiveData : LiveData<Chat> {
    private val query: Query
    private val listener =
        ChatEventListener()

    constructor(query: Query) {
        this.query = query
    }

    constructor(ref: DatabaseReference) {
        query = ref
    }

    override fun onActive() {
        Log.d(TAG, "onActive")
        query.addChildEventListener(listener)
    }

    override fun onInactive() {
        Log.d(TAG, "onInactive")
        query.removeEventListener(listener)
    }

    private inner class ChatEventListener : ChildEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException())
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
            Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
        }

        override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

            // A new comment has been added, add it to the displayed list
            val chat = dataSnapshot.getValue(Chat::class.java)
            Log.d(TAG, "onChildAdded value:" + chat?.messages)
            value = chat
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }

    }


    companion object {
        private const val TAG = "FBQueryChatsLiveData"
    }
}