package com.example.kiwariandroidtest.util

object ValidateString {
    fun validateEmail(email: String): Result {
        if (email.isEmpty()) {
            return Result(false, "Email address cannot be empty")
        }
        return Result(true, null)
    }

    fun validatePassword(password: String): Result {
        return when {
            password.isEmpty() -> {
                Result(false, "Password cannot be empty")
            }
            password.length<6 -> {
                Result(false, "Password must contain more than 6 character")
            }
            password.length>30 -> {
                Result(false, "Password cannot contain more than 30 character")
            }
            else -> Result(true, null)
        }
    }
}