package com.sgamerapps.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sgamerapps.android.activity.MainActivity
import com.sgamerapps.android.activity.StartActivity
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.data.User
import com.sgamerapps.android.databinding.ActivityLoginBinding
import com.sgamerapps.android.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var googleClient: GoogleSignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var signOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_auth_secret))
            .requestEmail().build()

        googleClient = GoogleSignIn.getClient(this, signOption)

        binding.loginBtn.setOnClickListener {

            Toast.makeText(this, "Please wait", Toast.LENGTH_LONG).show()
            startActivityForResult(googleClient.signInIntent, 1001)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ")
        Log.d(TAG, "onActivityResult: $requestCode")

        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                try {
                    var task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    var account = task.result
                    loginOrSignUpWithMailAndSaveUser(account.email!!, account.displayName!!)

                } catch (e: ApiException) {
                    Log.d(TAG, "onActivityResult: " + e.message)
                }
            } else {
                data.toString()
            }
        }
    }


    private fun loginOrSignUpWithMailAndSaveUser(mail: String, name: String) {

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

                    goToMainActivity()
                } else {
                    googleClient.signOut()
                }
                Toast.makeText(
                    this@LoginActivity,
                    response.body()!!.get("message").asString,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }
        })
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

