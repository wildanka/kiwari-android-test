package com.example.kiwariandroidtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.kiwariandroidtest.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnLogin.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val email = binding.tietEmail.text.toString()
            val password = binding.etPassword.text.toString()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener{task  ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }else{
                    Log.e("Login", task.result.toString())
                    Toast.makeText(this, "Login Failed "+task.result, Toast.LENGTH_SHORT).show()
                }

            })
        }

    }
}
