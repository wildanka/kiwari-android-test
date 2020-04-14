package com.example.kiwariandroidtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kiwariandroidtest.util.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //check the sharedpref whether user already logged in
//        val sharedPref = SharedPref(this)
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null){
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }else{
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            intent.putExtra("userId", auth.currentUser?.uid)
            startActivity(intent)
        }
    }
}
