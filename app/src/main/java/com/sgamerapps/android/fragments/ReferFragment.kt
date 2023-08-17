package com.sgamerapps.android.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sgamerapps.android.R
import com.sgamerapps.android.activity.MainActivity
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig
import com.sgamerapps.android.data.User
import com.sgamerapps.android.databinding.FragmentReferBinding
import com.sgamerapps.android.utils.Helper
import com.sgamerapps.android.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReferFragment : Fragment() {


    lateinit var currentUser: User
    lateinit var binding: FragmentReferBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReferBinding.inflate(inflater)

        currentUser= AppConfig.getCurrentUser(true)!!


        binding.myReferTv.text = currentUser.referralCode

        if (currentUser.referradBy != null) {
            binding.validateReferCode.isEnabled = false
            binding.referCodeEt.isEnabled = false
        }

        binding.copyRefercodeBtn.setOnClickListener {
            Helper.copyTextToClipboard(requireContext(), currentUser.referralCode.toString())
            Toast.makeText(requireContext(), "Code Copied To Clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.validateReferCode.setOnClickListener {
            validateCode(binding.referCodeEt.text.toString())
        }


//        Share click handle
        binding.share.setOnClickListener {
            shareCodeWithUrl()
        }
        binding.shareFb.setOnClickListener {
            shareCodeWithUrl()
        }
        binding.shareWhatsApp.setOnClickListener {
            shareCodeWithUrl()
        }
        binding.shareMessenger.setOnClickListener {
            shareCodeWithUrl()
        }
        binding.shareTelegram.setOnClickListener {
            shareCodeWithUrl()
        }




        return binding.root
    }

    private fun updateUser(mail: String, name: String) {

        var deviceId = com.sgamerapps.android.utils.DeviceHelper.getDeviceIMEI(requireContext())
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

                    currentUser= AppConfig.getCurrentUser(true)!!
                    (activity as MainActivity).updateWallet()
                } else {
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }
        })
    }

    private fun validateCode(code: String) {
        val data = mapOf(
            "code" to code,

            )
        ApiClient.getInstance().addReferral(data)!!.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Toast.makeText(
                    requireContext(),
                    response.body()!!.get("message").asString,
                    Toast.LENGTH_SHORT
                ).show()
                if(response.body()!!.get("success").asBoolean){
                    updateUser(currentUser.email.toString(),currentUser.name.toString())
                    binding.referCodeEt.isEnabled=false
                    binding.validateReferCode.isEnabled=false
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }
        })
    }

    private fun shareCodeWithUrl(){
        var shareString=getString(R.string.referral_share)
      shareString=shareString.replace("__refer_code__",currentUser.referralCode.toString())
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareString)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


}