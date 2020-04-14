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
import com.example.kiwariandroidtest.adapter.ChatItemAdapter
import com.example.kiwariandroidtest.databinding.ActivityMainBinding
import com.example.kiwariandroidtest.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.toolbar_chat.view.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private lateinit var userId: String
    private lateinit var binding: ActivityMainBinding

    private lateinit var mChatDatabaseReference: DatabaseReference
    private lateinit var adapter : ChatItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val chatToolbar = findViewById<Toolbar>(R.id.toolbar_chat) as Toolbar
        setSupportActionBar(chatToolbar)

        //get User Id
        userId = intent.extras?.getString("userId") ?: "user1"


        mChatDatabaseReference = Firebase.database.reference.child("chats")
        mChatDatabaseReference.addListenerForSingleValueEvent(chatFirstListener)
        mChatDatabaseReference.addChildEventListener(chatEventListener)

        //fetch the data from the chatroom collection, we hardcoded it since the scenario is the user already on a private chatroom
//        /chatroom/UN4IF7Hwle1Db7DnLqd4

        Toast.makeText(this, "userId : $userId", Toast.LENGTH_SHORT).show()

        adapter = ChatItemAdapter(userId)
        binding.contentMain.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.contentMain.rvChatList.adapter = adapter

        //Edit title to be opponent username
        binding.toolbarChat.chat_bar_username.text = "Jarjit Singh"

        binding.contentMain.fabSendChat.setOnClickListener {
            val messageText = binding.contentMain.etChat.text.toString().trim()

            if (messageText.isNotEmpty()) {
                sendMessage(messageText, userId, "idJarjit")
                binding.contentMain.etChat.setText("")
            }
        }


//        adapter.setupMessages(dummyChats)
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
                        FirebaseAuth.getInstance().signOut()
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

    fun sendMessage(messageText: String, userId: String, opponentId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
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

            chat?.let { adapter.addRecentChat(it) }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            val newComment = dataSnapshot.getValue<Chat>()
            val commentKey = dataSnapshot.key

            // ...
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException())
            Toast.makeText(this@MainActivity, "Failed to load comments.",
                Toast.LENGTH_SHORT).show()
        }

    }

    private val chatFirstListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val chatList: MutableList<Chat> = mutableListOf()
            for (snapshot in dataSnapshot.children){
                val chat = snapshot.getValue<Chat>()
                println("ini ${chat?.messages}")
                chat?.let { chatList.add(it) }
            }

            adapter.setupChats(chatList)
        }

    }

}
