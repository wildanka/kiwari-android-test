package com.example.kiwariandroidtest.util

import android.content.Context
import android.content.SharedPreferences

class SharedPref(private val mContext: Context) {
    private var mSharedPref : SharedPreferences = mContext.getSharedPreferences("filename", Context.MODE_PRIVATE)
    private lateinit var editor: SharedPreferences.Editor


    fun setLoginState(){
        editor = mSharedPref.edit()
        editor.putBoolean("LoginState", true)
        editor.apply()
    }

    fun isLogin(): Boolean {
        return mSharedPref.getBoolean("LoginState", false)
    }
}