package com.sgamerapps.android.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.sgamerapps.android.data.User

class PreferenceManager {


    companion object {


        private const val SHARED_PREF = "my_sharedpref"


        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private lateinit var gson: Gson


        fun init(context: Context) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
            editor = sharedPreferences.edit()
            gson = Gson()

        }

        fun saveToken(token: String) {
            editor.putString("token", token);
            editor.apply()
        }

        fun getToken(): String? {
            return sharedPreferences.getString("token", null);
        }

        fun saveUser(user: User) {
            editor.putString("user", gson.toJson(user));
            editor.apply()
        }

        fun getBoolean(key:String):Boolean{
            return sharedPreferences.getBoolean(key,false)
        }

        fun  saveBoolean(key: String,value: Boolean){
            editor.putBoolean(key, value);
            editor.apply()
        }

        fun getUser():User? {

            val userJson = sharedPreferences.getString("user", null)
            return userJson?.let { gson.fromJson(it, User::class.java) }
        }



    }
}