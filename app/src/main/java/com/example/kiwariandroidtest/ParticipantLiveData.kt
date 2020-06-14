package com.example.kiwariandroidtest

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kiwariandroidtest.model.ChatParticipant
import com.google.firebase.database.*


class ParticipantLiveData : LiveData<ChatParticipant?> {
    private val query: Query
    private val listener =
        MyValueEventListener()

    constructor(query: Query) {
        this.query = query
    }

    constructor(ref: DatabaseReference) {
        query = ref
    }

    override fun onActive() {
        Log.d(LOG_TAG, "onActive")
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        Log.d(LOG_TAG, "onInactive")
        query.removeEventListener(listener)
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val participant = dataSnapshot.getValue(ChatParticipant::class.java)
          /*  opponentId = if (userId == participant?.participant0) {
                participant?.participant1.toString()
            } else {
                participant?.participant0.toString()
            }*/
            value = participant

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(
                LOG_TAG,
                "Can't listen to query $query",
                databaseError.toException()
            )
        }
    }

    companion object {
        private const val LOG_TAG = "FirebaseQueryLiveData"
    }
}