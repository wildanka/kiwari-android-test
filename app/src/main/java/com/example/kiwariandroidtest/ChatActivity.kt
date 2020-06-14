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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kiwariandroidtest.adapter.ChatItemAdapter
import com.example.kiwariandroidtest.databinding.ActivityMainBinding
import com.example.kiwariandroidtest.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.toolbar_chat.view.*


class ChatActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatViewModel
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    private lateinit var userId: String
    private lateinit var opponentId: String
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ChatItemAdapter
    private lateinit var mChatDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        val liveDataParticipant = viewModel.getChatParticipantSnapshotLiveData()
        val chatLiveData = viewModel.getChatsLiveData()

        val chatToolbar = findViewById<Toolbar>(R.id.toolbar_chat)
        setSupportActionBar(chatToolbar)

        //get User Id
        userId = intent.extras?.getString("userId") ?: "user1"

        //Edit title to be opponent username
        opponentId = "opponentId"
        binding.toolbarChat.chat_bar_username.text = getString(R.string.default_username)

        liveDataParticipant.observe(this, Observer {
            if (it != null) { // update the UI here with values in the snapshot
                Log.e(TAG,"participant 0 : ${it.participant0}")
                opponentId = if (userId == it.participant0) {
                    it.participant1.toString()
                } else {
                    it.participant0.toString()
                }
                loadOpponentProfile(opponentId)
            }
        })

        //fetch chat history
        chatLiveData?.observe(this, Observer {
            Log.e(TAG, "chatLiveDataObserver ${it.toString()}")
            if (it != null) { // update the UI here with values in the snapshot
                Log.e(TAG,"chat lists contain 0 : ${it[0].messages}")
                adapter.setupChats(it)
            }
        })
        adapter = ChatItemAdapter(userId)
        binding.contentMain.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.contentMain.rvChatList.adapter = adapter


        binding.contentMain.fabSendChat.setOnClickListener {
            val messageText = binding.contentMain.etChat.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText, userId, opponentId)
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
                        val intent = Intent(this@ChatActivity, LoginActivity::class.java)
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
            System.currentTimeMillis()
        )
        databaseReference.child("chats").push().setValue(chat)
    }

    private fun loadOpponentProfile(opponentId: String){
        //search opponent ID
        val userReference = fireStore.collection("users").document(opponentId)
        userReference.get()
            .addOnSuccessListener {
                if (it.exists()) {
                    binding.toolbarChat.chat_bar_username.text = it.get("name").toString()
                    Glide.with(this@ChatActivity).load(it.get("avatar").toString())
                        .into(binding.toolbarChat.chat_bar_avatar)
                } else {
                    Toast.makeText(
                        this@ChatActivity,
                        "Opponent profile not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@ChatActivity,
                    "Error caused by \n ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
