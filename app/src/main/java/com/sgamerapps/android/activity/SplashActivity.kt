package com.sgamerapps.android.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sgamerapps.android.BuildConfig
import com.sgamerapps.android.R
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig
import com.sgamerapps.android.data.User
import com.sgamerapps.android.utils.Constants
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val configDb = FirebaseDatabase.getInstance().getReference("config")


        if (isOnline(this)) {
            configDb.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var baseUrl = snapshot.child("baseUrl").value.toString()
                    var jwtSecret = snapshot.child("jwt_secret").value.toString()
                    var appVersion = snapshot.child("app_version").value


                    val pInfo: PackageInfo =
                        packageManager.getPackageInfo(packageName, 0)
                    val verCode = pInfo.versionCode
                    if (Integer.parseInt(appVersion.toString()) > verCode && !BuildConfig.DEBUG) {
                        startActivity(Intent(this@SplashActivity, UpdateActivity::class.java))
                        finish()
                        return
                    }

                    ApiClient.setBaseUrl(baseUrl)
                    AppConfig.jwtSecret = jwtSecret
                    getConfigData()

                    Handler(Looper.getMainLooper()!!).postDelayed(Runnable {
                        var user = AppConfig.getCurrentUser()

                        if (user != null) {
                            updateUser(user.email.toString(), user.name.toString())
                        } else {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    com.sgamerapps.android.LoginActivity::class.java
                                )
                            )
                            finish()

                        }
                    }, 3000)

                }

                override fun onCancelled(error: DatabaseError) {

                    Log.e("himanshu", "onCancelled: " + error.message)
                }

            })

        } else {
            DialogHelper.noInternetDialog(this)
        }


    }

    private fun updateUser(mail: String, name: String) {

        var deviceId = com.sgamerapps.android.utils.DeviceHelper.getDeviceIMEI(this)
        val data = mapOf(
            "email" to mail,
            "name" to name,
            "device_id" to deviceId
        )
        ApiClient.getInstance().loginWithMail(data)!!.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.body()!!.get("success").asBoolean) {
                    var token = response.body()!!.get("token").asString
                    var userJson = response.body()!!.get("data").toString()
                    PreferenceManager.saveToken(token)
                    PreferenceManager.saveUser(Gson().fromJson(userJson, User::class.java))
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()

                } else {
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }
        })
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }

    private fun getConfigData() {
        ApiClient.getInstance().config()!!.enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {

                for (i in 0 until response.body()!!.size()) {
                    if (response.body()!!
                            .get(i).asJsonObject.get("key").asString == Constants.TOPON_ENABLED
                    ) {

                        var isToponEnabled =
                            response.body()!!.get(i).asJsonObject.get("value").asInt == 1
                        PreferenceManager.saveBoolean(Constants.TOPON_ENABLED, isToponEnabled)

                    }
                }


            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
            }

        })
    }
}