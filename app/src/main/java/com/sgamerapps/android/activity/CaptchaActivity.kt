package com.sgamerapps.android.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig
import com.sgamerapps.android.data.Count
import com.sgamerapps.android.data.User
import com.sgamerapps.android.databinding.ActivityCaptchaBinding
import com.sgamerapps.android.utils.Constants
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.Helper
import com.sgamerapps.android.utils.JwtHelper
import com.sgamerapps.android.utils.PreferenceManager
import com.sgamerapps.android.utils.TaskListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CaptchaActivity : AppCompatActivity() {

    lateinit var captchaCount: Count
    val countType = "captcha"
    val perCountReward = 10
   lateinit var captcha:String

    lateinit var binding:ActivityCaptchaBinding
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCaptchaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTodayCount()

        binding.captchaSubmitBtn.setOnClickListener {
            if (binding.resultEt.text.toString() == captcha) {
                var message = "Congratulations! You won $perCountReward points"

                if ((captchaCount.usedCount + 1) == 4) {
                    var tempCount = captchaCount
                    tempCount.usedCount = captchaCount.usedCount + 1
                    DialogHelper.showRewardDialog(this@CaptchaActivity, message, countType, true)
                } else {
                    DialogHelper.showRewardDialog(this, message, countType, false,object :
                        TaskListener {
                        override fun onTaskCompleted(isCompleted: Boolean) {
                            if(isCompleted){
                                increaseTodayCount()
                                addToWallet(perCountReward.toInt())
                            }
                            else{
                                Toast.makeText(this@CaptchaActivity, "Task failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                    },Constants.UNITY_ADS)
                }

                setCaptcha()
            } else {
                Toast.makeText(this, "Oops! wrong ", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun  setCaptcha(){
        if(binding.resultEt.isFocused){
            binding.resultEt.clearFocus()
        }
        Helper.hideKeyboard(this@CaptchaActivity,binding.resultEt)
        binding.resultEt.setText("")
        captcha=getRandomCaptcha()
        binding.captchaTv.text = captcha
    }
    private fun getRandomCaptcha():String{
        return  kotlin.random.Random.nextInt(1000,9999).toString()
    }

    private fun increaseTodayCount() {

        DialogHelper.showLoading(this);
        ApiClient.getInstance().updateCount(countType)!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                captchaCount = response.body()!!;
                updateCountUi()
                DialogHelper.hideLoading()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    DialogHelper.noInternetDialog(this@CaptchaActivity!!)

                }
                Toast.makeText(this@CaptchaActivity, "Please try again", Toast.LENGTH_SHORT).show()
                captchaCount = Count(0, 0, 0)
//                updateCountUi()

            }

        })
    }

    private fun getTodayCount() {

        DialogHelper.showLoading(this);
        ApiClient.getInstance().getTodayCount(countType)!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                captchaCount = response.body()!!;
                updateCountUi()
                setCaptcha()
                DialogHelper.hideLoading()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    if (this@CaptchaActivity != null) {
                        DialogHelper.noInternetDialog(this@CaptchaActivity!!)
                    }

                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addToWallet(amount: Int) {

        DialogHelper.showLoading(this);

//        payload that will be encoded for jwt
        var data = HashMap<String, String>()
        data["amount"] = amount.toString()

//        Generated token that will sent to website
        var token = JwtHelper(AppConfig.jwtSecret!!).generateToken(data)

//        Body that will be contains token
        var body = HashMap<String, String>()
        body["data"] = token



        ApiClient.getInstance().addToWallet(body)!!.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                DialogHelper.hideLoading()


//                Updating user with wallet amount also
                PreferenceManager.saveUser(response.body()!!)
                AppConfig.getCurrentUser(true)

//                updateWallet()

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    if (this@CaptchaActivity != null) {
                        DialogHelper.noInternetDialog(this@CaptchaActivity!!)

                    }

                }
            }

        })
    }
    private fun updateCountUi() {
        binding.captchaLeftTv.text = captchaCount.countsLeft.toString()
        if (captchaCount.countsLeft == 0) {
            binding.captchaTv.text = "No counts left"
            binding.captchaSubmitBtn.isEnabled = false

        }
    }

}