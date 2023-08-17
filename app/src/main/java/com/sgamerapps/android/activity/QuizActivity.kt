package com.sgamerapps.android.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.sgamerapps.android.utils.Helper
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig

import com.sgamerapps.android.data.Count
import com.sgamerapps.android.data.User
import com.sgamerapps.android.databinding.ActivityQuiz2Binding
import com.sgamerapps.android.utils.Constants
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.JwtHelper
import com.sgamerapps.android.utils.PreferenceManager
import com.sgamerapps.android.utils.TaskListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    var firstNumber: Int = 0
    var secondNumber: Int = 0
    lateinit var quizCount: Count
    val countType = "quiz"
    val perCountReward = 10


    lateinit var binding: ActivityQuiz2Binding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuiz2Binding.inflate(layoutInflater)
        setContentView(binding.root)



        getTodayCount()

        binding.quizSubmitBtn.setOnClickListener {
            if (binding.resultEt.text.toString() == (firstNumber + secondNumber).toString()) {


                var message = "Congratulations! You won $perCountReward points"

                if ((quizCount.usedCount + 1) == 4) {
                    var tempCount = quizCount
                    tempCount.usedCount = quizCount.usedCount + 1
                    DialogHelper.showRewardDialog(this@QuizActivity, message, countType, true)
                } else {
                    DialogHelper.showRewardDialog(this, message, countType, false,object :
                        TaskListener {
                        override fun onTaskCompleted(isCompleted: Boolean) {
                            if(isCompleted){
                                increaseTodayCount()
                                addToWallet(perCountReward.toInt())
                            }
                            else{
                                Toast.makeText(this@QuizActivity, "Task failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                    },Constants.UNITY_ADS)
                }
                setQuiz()

                setQuiz()

            } else {
                Toast.makeText(this, "Oops! wrong ", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun increaseTodayCount() {

        DialogHelper.showLoading(this);
        ApiClient.getInstance().updateCount(countType)!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                quizCount = response.body()!!;
                updateCountUi()
                DialogHelper.hideLoading()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    DialogHelper.noInternetDialog(this@QuizActivity!!)

                }
                Toast.makeText(this@QuizActivity, "Please try again", Toast.LENGTH_SHORT).show()
                quizCount = Count(0, 0, 0)
//                updateCountUi()

            }

        })
    }

    private fun getTodayCount() {

        DialogHelper.showLoading(this);
        ApiClient.getInstance().getTodayCount(countType)!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                quizCount = response.body()!!;
                updateCountUi()
                setQuiz()
                DialogHelper.hideLoading()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    if (this@QuizActivity != null) {
                        DialogHelper.noInternetDialog(this@QuizActivity!!)
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
                    if (this@QuizActivity != null) {
                        DialogHelper.noInternetDialog(this@QuizActivity!!)

                    }

                }
            }

        })
    }

    private fun generateRandomNumber(): Int {
        return Random.nextInt(100)
    }

    private fun setQuiz() {
        if (quizCount.countsLeft == 0) {
            return
        }
        Helper.hideKeyboard(this@QuizActivity,binding.resultEt)

        firstNumber = generateRandomNumber()
        secondNumber = generateRandomNumber()
        binding.quizTv.text = "$firstNumber + $secondNumber"
        binding.resultEt.setText("")

    }

    private fun updateCountUi() {
        binding.quizLeftTv.text = quizCount.countsLeft.toString()
        if (quizCount.countsLeft == 0) {
            binding.quizTv.text = "No counts left"
            binding.quizSubmitBtn.isEnabled = false

        }
    }

}