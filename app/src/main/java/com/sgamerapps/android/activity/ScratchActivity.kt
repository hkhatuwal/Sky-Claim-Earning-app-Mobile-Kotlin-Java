package com.sgamerapps.android.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.anupkumarpanwar.scratchview.ScratchView
import com.anupkumarpanwar.scratchview.ScratchView.GONE
import com.anupkumarpanwar.scratchview.ScratchView.IRevealListener
import com.anupkumarpanwar.scratchview.ScratchView.VISIBLE
import com.applovin.mediation.ads.MaxInterstitialAd
import com.sgamerapps.android.Ads.AdsManager
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig
import com.sgamerapps.android.data.Count
import com.sgamerapps.android.data.User
import com.sgamerapps.android.databinding.ActivityScratchBinding
import com.sgamerapps.android.utils.Constants
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.JwtHelper
import com.sgamerapps.android.utils.PreferenceManager
import com.sgamerapps.android.utils.TaskListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScratchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScratchBinding
    private var scratchLeft = 0
    private var mInterstitialAd: MaxInterstitialAd? = null

    private lateinit var scratchCount: Count
    var winOptions = mutableListOf<Int>(5, 15, 8, 25, 12, 4, 6, 10, 10, 5)
    var ADS_TYPE = Constants.APP_LOVIN_ADS


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScratchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AdsManager.bannerAd(this, binding.bannerAd)
        ADS_TYPE = if (AppConfig.isToponEnabled()) {
            Constants.TOPON_ADS
        } else {
            Constants.APP_LOVIN_ADS

        }

        getTodayCount()
        binding.scratchView.setRevealListener(object : IRevealListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onRevealed(scratchView: ScratchView?) {
//                var wonPoints = getRandomScratchPoints()
                var wonPoints = winOptions[scratchCount.usedCount]

                var message = "Congratulations! You won $wonPoints points"

                if ((scratchCount.usedCount + 1) == 4) {
                    DialogHelper.showRewardDialog(this@ScratchActivity, message, "scratch", true)
                } else {
                    DialogHelper.showRewardDialog(
                        this@ScratchActivity,
                        message,
                        "scratch",
                        false,
                        object : TaskListener {
                            override fun onTaskCompleted(isCompleted: Boolean) {
                                if (isCompleted) {
                                    increaseCount(wonPoints.toString())
                                    addToWallet(wonPoints)
                                } else {
                                    Toast.makeText(
                                        this@ScratchActivity,
                                        "Task not completed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        },ADS_TYPE)


                }



                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    binding.scratchView.mask()
                }, 1000)

            }

            override fun onRevealPercentChangedListener(scratchView: ScratchView?, percent: Float) {
                if (percent > 0.2) {
                    scratchView!!.reveal()
                }

            }

        })


    }


    private fun getTodayCount() {

        DialogHelper.showLoading(this);
        ApiClient.getInstance().getTodayCount("scratch")!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                scratchCount = response.body()!!;
                updateCountUi()
                DialogHelper.hideLoading()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    DialogHelper.noInternetDialog(this@ScratchActivity)
                }

            }

        })
    }

    private fun increaseCount(points: String) {

        DialogHelper.showLoading(this);
        ApiClient.getInstance().updateCount("scratch")!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                scratchCount = response.body()!!;
                updateCountUi()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    DialogHelper.noInternetDialog(this@ScratchActivity)
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


            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    DialogHelper.noInternetDialog(this@ScratchActivity)
                }
                Toast.makeText(this@ScratchActivity, "Please try again", Toast.LENGTH_SHORT).show()
                scratchCount = Count(0, 0, 0)
                updateCountUi()

            }

        })
    }


    private fun updateCountUi() {
        binding.scratchLeftTv.text = scratchCount.countsLeft.toString()
        if (scratchCount.countsLeft <= 0) {
            binding.scratchViewLayout.visibility = GONE
            binding.noScratchLeftTv.visibility = VISIBLE
//            Hide scratch view
        } else {
            binding.scratchViewLayout.visibility = VISIBLE
            binding.noScratchLeftTv.visibility = GONE
        }
    }

    override fun onResume() {
        super.onResume()
        getTodayCount()
    }
}