package com.example.kiwariandroidtest

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.kiwariandroidtest.databinding.ActivityLoginBinding
import com.example.kiwariandroidtest.util.ValidateString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    //region textWatcher
    private val twEmail: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) {
            val email = ValidateString.validateEmail(s.toString())
            if (email.status) {
                binding.tilEmail.error = null
            } else {
                binding.tilEmail.error = email.message
            }
        }
    }
    private val twPassword: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

        override fun afterTextChanged(s: Editable) {
            val password = ValidateString.validatePassword(s.toString())
            if (password.status) {
                binding.tilPassword.error = null
            } else {
                binding.tilPassword.error = password.message
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.tietEmail.addTextChangedListener(twEmail)
        binding.etPassword.addTextChangedListener(twPassword)

        binding.btnLogin.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val resultEmail = ValidateString.validateEmail(email)
            val resultPassword = ValidateString.validatePassword(password)
            if (resultEmail.status && resultPassword.status) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                getString(R.string.successfully_logged_in),
                                Toast.LENGTH_SHORT
                            ).show()
                            val userId = auth.currentUser?.uid
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("userId",userId)
                            startActivity(intent)
                            finish()
                        }

                    }
                    .addOnFailureListener { e ->

                        if (e is FirebaseAuthInvalidCredentialsException) {
                            binding.tilPassword.error = getString(R.string.invalid_password)
                        } else if (e is FirebaseAuthInvalidUserException) {
                            when (e.errorCode) {
                                "ERROR_USER_NOT_FOUND" -> {
                                    binding.tilEmail.error =
                                        getString(R.string.no_matching_account_found)
                                }
                                "ERROR_USER_DISABLED" -> {
                                    binding.tilEmail.error =
                                        getString(R.string.user_account_has_been_disabled)
                                }
                                else -> {
                                    val err = e.getLocalizedMessage()
                                    Log.e("TAG", "error caused by: $err")
                                    Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
            } else {
                if (!resultEmail.status) binding.tilEmail.error = resultEmail.message
                if (!resultPassword.status) binding.tilPassword.error = resultPassword.message
            }
        }

    }
}
