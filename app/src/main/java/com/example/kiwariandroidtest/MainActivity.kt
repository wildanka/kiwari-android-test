package com.example.kiwariandroidtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kiwariandroidtest.adapter.ChatItemAdapter
import com.example.kiwariandroidtest.databinding.ActivityMainBinding
import com.example.kiwariandroidtest.model.Chat
import com.example.kiwariandroidtest.model.ChatParticipant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.toolbar_chat.view.*
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val chatList: MutableList<Chat> = mutableListOf()

    private lateinit var userId: String
    private lateinit var opponentId: String
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ChatItemAdapter
    private lateinit var mChatDatabaseReference: DatabaseReference
    private lateinit var dbParticipantReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val chatToolbar = findViewById<Toolbar>(R.id.toolbar_chat) as Toolbar
        setSupportActionBar(chatToolbar)

        //get User Id
        userId = intent.extras?.getString("userId") ?: "user1"

        //Edit title to be opponent username
        dbParticipantReference = Firebase.database.reference.child("participant")
        dbParticipantReference.addListenerForSingleValueEvent(participantEventListener)
        binding.toolbarChat.chat_bar_username.text = "Friends Username"

        //fetch chat history
        mChatDatabaseReference = Firebase.database.reference.child("chats")
        mChatDatabaseReference.addChildEventListener(chatEventListener)

        adapter = ChatItemAdapter(userId)
        binding.contentMain.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.contentMain.rvChatList.adapter = adapter


        binding.contentMain.fabSendChat.setOnClickListener {
            val messageText = binding.contentMain.etChat.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText, userId, "idJarjit")
                binding.contentMain.etChat.setText("")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Logout")
                    .setMessage("Are you sure want to logout?")

                builder.setCancelable(true)
                    .setNegativeButton(android.R.string.no) { _, _ ->
                        Toast.makeText(this, "Logout Cancelled", Toast.LENGTH_SHORT).show()
                    }.setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                builder.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendMessage(messageText: String, userId: String, opponentId: String) {
        val databaseReference = db.reference
        val chat = Chat(
            userId,
            opponentId,
            messageText,
            "10:25"
        )
        databaseReference.child("chats").push().setValue(chat)
    }

    private val chatEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

            // A new comment has been added, add it to the displayed list
            val chat = dataSnapshot.getValue<Chat>()
            Log.d(TAG, "onChildAdded value:" + chat?.messages)
            chat?.let { chatList.add(it) }
            adapter.setupChats(chatList)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException())
            Toast.makeText(
                this@MainActivity, "Failed to load comments.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val participantEventListener = object : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val participant = dataSnapshot.getValue(ChatParticipant::class.java)
            Log.e(
                TAG,
                "participant0 ${participant?.participant0} dan participant1 ${participant?.participant1}"
            )

            opponentId = if (userId == participant?.participant0) {
                participant.participant1.toString()
            } else {
                participant?.participant0.toString()
            }
            loadOpponentProfile(opponentId)
        }
    }

    private fun loadOpponentProfile(opponentId: String){
        //search opponent ID
        val userReference = fireStore.collection("users").document(opponentId)
        userReference.get()
            .addOnSuccessListener {
                if(it.exists()){
                    binding.toolbarChat.chat_bar_username.text = it.get("name").toString()
                    Glide.with(this@MainActivity).load(it.get("avatar").toString()).into(binding.toolbarChat.chat_bar_avatar)
                }else{
                    Toast.makeText(this@MainActivity, "Opponent profile not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.e("ERROR", "pedah ieu : ${it.localizedMessage}")
                Toast.makeText(this@MainActivity, "Error caused by \n ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

}
