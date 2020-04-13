package com.example.kiwariandroidtest

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.toolbar_chat.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var chatToolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatToolbar = findViewById<Toolbar>(R.id.toolbar_chat) as Toolbar
        setSupportActionBar(chatToolbar)

        //Edit title to be opponent username
        chatToolbar.chat_bar_username.text = "Jarjit Singh"
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
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                builder.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
