package com.sgamerapps.android.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.sgamerapps.android.R
import com.sgamerapps.android.activity.MainActivity
import com.sgamerapps.android.api.ApiClient
import com.sgamerapps.android.config.AppConfig
import com.sgamerapps.android.data.User
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.JwtHelper
import com.sgamerapps.android.utils.PreferenceManager
import com.sgamerapps.android.utils.TaskListener
import com.sgamerapps.android.data.Count
import com.sgamerapps.android.databinding.FragmentSpin3Binding
import com.sgamerapps.android.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class Spin2Fragment : Fragment() {

    private lateinit var binding: FragmentSpin3Binding
    private lateinit var spinCount: Count
    var winOptions = mutableListOf<Int>(5, 5, 6, 25, 14, 10, 9, 5, 1, 10)

    var colors = mutableListOf<Long>(
        0xffE8A0BF,
        0xffFFA559,
        0xff27E1C1,
        0xffB2A4FF,
        0xff9A208C,
        0xffE49393,
        0xff200235,
        0xfff6abb6,
    )

    var rewardList = mutableListOf<String>(
        "25", "10", "6", "14", "5", "15", "1", "9"
    )

    lateinit var itemList: MutableList<com.sgamerapps.android.SpinWheel.model.LuckyItem>

    var ADS_TYPE=Constants.APP_LOVIN_ADS

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpin3Binding.inflate(inflater, container, false)
        ADS_TYPE = if(AppConfig.isToponEnabled()){
            Constants.TOPON_ADS
        } else{
            Constants.APP_LOVIN_ADS

        }
//        Setting up wheel
        addWheelData()

        setWheelView()


        getTodayCount()


        com.sgamerapps.android.Ads.AdsManager.bannerAd(requireActivity(), binding.bannerAd)


        binding.spinButton.setOnClickListener {
            binding.luckyWheel.startLuckyWheelWithTargetIndex(rewardList.indexOf(winOptions[spinCount.usedCount].toString()))
        }
        return binding.root;
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setWheelView() {
        binding.luckyWheel.setBorderColor(Color.TRANSPARENT)
        binding.luckyWheel.setRound(10)
        binding.luckyWheel.setData(itemList)

        binding.luckyWheel.setLuckyRoundItemSelectedListener {
            var points = rewardList[it];
            var message = "Congratulations! You won $points diamonds"
            if ((spinCount.usedCount + 1) == 4) {
                DialogHelper.showRewardDialog(requireContext(), message, "spin2", true)
            } else {
                DialogHelper.showRewardDialog(
                    requireContext(), message, "spin2", false, object : TaskListener {
                        override fun onTaskCompleted(isCompleted: Boolean) {
                            if (isCompleted) {
                                increaseTodayCount(points)
                                addToWallet(points.toInt())
                            } else {
                                Toast.makeText(requireContext(), "Task failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }, ADS_TYPE
                )


            }

//
        }

    }

    private fun addWheelData() {
        itemList = mutableListOf()
        for (i in rewardList.indices) {
            val color = colors[i]
            val rewardText = rewardList[i]
            val item = com.sgamerapps.android.SpinWheel.model.LuckyItem()
            item.icon = R.drawable.diamond
            item.color = color.toInt()
            item.topText = rewardText
            item.textColor = Color.WHITE
            itemList.add(item)


        }

    }


    private fun getTodayCount() {

        DialogHelper.showLoading(requireContext());
        ApiClient.getInstance().getTodayCount("spin2")!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                spinCount = response.body()!!;
                updateCountUi()
                DialogHelper.hideLoading()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()

                if (t.message!!.contains("internet")) {
                    if (context != null) {
                        DialogHelper.noInternetDialog(context!!)

                    }
                }
            }

        })
    }

    private fun increaseTodayCount(points: String) {

        DialogHelper.showLoading(requireContext());
        ApiClient.getInstance().updateCount("spin2")!!.enqueue(object : Callback<Count> {
            override fun onResponse(call: Call<Count>, response: Response<Count>) {
                spinCount = response.body()!!;
                updateCountUi()
                DialogHelper.hideLoading()
            }

            override fun onFailure(call: Call<Count>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    if (context != null) {
                        DialogHelper.noInternetDialog(context!!)
                    }

                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addToWallet(amount: Int) {

        DialogHelper.showLoading(requireContext());

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

                updateWallet()

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                DialogHelper.hideLoading()
                if (t.message!!.contains("internet")) {
                    if (context != null) {
                        DialogHelper.noInternetDialog(context!!)

                    }

                }
                Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show()
                spinCount = Count(0, 0, 0)
                updateCountUi()
            }

        })
    }


    private fun updateCountUi() {
        binding.spinLeftTv.text = spinCount.countsLeft.toString()

        if (spinCount.countsLeft <= 0) {
            binding.spinButton.text = "No counts left"
            binding.spinButton.isEnabled = false
            binding.spinButton.isClickable = false
        }
    }

    private fun updateWallet() {
        try {
            var mainActivity: MainActivity = activity as MainActivity
            mainActivity.updateWallet()
        } catch (e: Exception) {

        }

    }

    override fun onResume() {
        super.onResume()
        getTodayCount()
    }

}