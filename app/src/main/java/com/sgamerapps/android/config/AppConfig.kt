package com.sgamerapps.android.config

import com.sgamerapps.android.data.User
import com.sgamerapps.android.utils.Constants
import com.sgamerapps.android.utils.PreferenceManager

class AppConfig {

    companion object {
        private lateinit var instance: AppConfig

        private var token:String?=null
        private var currentUser:User?=null
         var jwtSecret:String?=null







        fun  getToken():String?{
            if(token==null){
                token=PreferenceManager.getToken()
            }
            return  token;
        }
        fun  getCurrentUser(refresh:Boolean=false):User?{
            if(currentUser==null || refresh){
                currentUser=PreferenceManager.getUser()
            }
            return  currentUser;
        }

        fun  isToponEnabled():Boolean{
            return  PreferenceManager.getBoolean(Constants.TOPON_ENABLED)
        }


    }
}