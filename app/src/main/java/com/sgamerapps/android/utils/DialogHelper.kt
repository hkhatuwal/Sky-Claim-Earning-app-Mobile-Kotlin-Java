package com.sgamerapps.android.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialListener
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.sgamerapps.android.Ads.AdsManager
import com.sgamerapps.android.Ads.AppLovinAdsHelper
import com.sgamerapps.android.Ads.ToponAdsHelper
import com.sgamerapps.android.Ads.UnityAdsHelper
import com.sgamerapps.android.R
import com.unity3d.ads.UnityAds


interface TaskListener {
    fun onTaskCompleted(isCompleted: Boolean)
}

class DialogHelper {
    companion object {
        private lateinit var rewardDialog: AlertDialog
        private var progressDialog: ProgressDialog? = null


        fun noInternetDialog(context: Context) {
            val alertDialog = AlertDialog.Builder(context).create()

            alertDialog.setTitle("No Internet")
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again")
            alertDialog.setIcon(R.drawable.error)
            alertDialog.setButton(
                "OK"
            ) { dialog, which ->
                alertDialog.dismiss()
                (context as Activity).finish()
            }
            try {
                alertDialog.show()

            } catch (e: Exception) {

            }
        }

        fun showRewardDialog(
            context: Context,
            message: String = "Congratulation! You won  diamonds",
            type: String,
            openClickActivity: Boolean = false,
            listener: TaskListener? = null,
            adsProvider: Int = Constants.APP_LOVIN_ADS
        ) {

            var isTimerRunning = true
            val totalTime: Long = 7000  // time in ms


            var adsHelper: AppLovinAdsHelper? = null
            var toponAdsHelper: ToponAdsHelper? = null
            var unityAdsHelper: UnityAdsHelper? = null

            when (adsProvider) {
                Constants.APP_LOVIN_ADS -> {
                    adsHelper = AppLovinAdsHelper(context)
                    adsHelper.createInterstitialAd()
                }

                Constants.TOPON_ADS -> {
                    toponAdsHelper = ToponAdsHelper(context)
                    toponAdsHelper.loadAdInterstitialAd()

                }

                Constants.UNITY_ADS -> {
                    unityAdsHelper = UnityAdsHelper(context)
                }

            }


            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val view =
                LayoutInflater.from(context).inflate(R.layout.reward_dialog_vieew, null, false)
            builder.setView(view)

            val closeBtn = view.findViewById<Button>(R.id.closeBtn)
            val descriptionText = view.findViewById<TextView>(R.id.description)
            val nativeAd = view.findViewById<RelativeLayout>(R.id.native_ad_container)
            AdsManager.nativeBannerAd(context as Activity?, nativeAd)

            descriptionText.text = message
            closeBtn.setOnClickListener {
                if (!isTimerRunning) {
                    rewardDialog.dismiss()
                    if (openClickActivity) {
                        var intent = Intent(
                            context,
                            com.sgamerapps.android.activity.ClickActivity::class.java
                        )
                        intent.putExtra("type", type)
                        intent.putExtra(Constants.FROM, (context as Activity).localClassName)
                        context.startActivity(intent)
                    } else {
                        when (adsProvider) {
                            Constants.APP_LOVIN_ADS -> {
                                if (listener != null) {
                                    adsHelper!!.showInterstitialAd(listener)
                                }
                            }

                            Constants.TOPON_ADS -> {
                                if (listener != null) {
                                    toponAdsHelper!!.showInterstitialAd(listener)
                                }

                            }

                            Constants.UNITY_ADS -> {
                                if (listener != null) {
                                    unityAdsHelper!!.loadAndShowrewardedAds(listener)
                                }

                            }
                        }
                    }
                }

            }

            rewardDialog = builder.create()
            rewardDialog.setCancelable(false)
            rewardDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            rewardDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rewardDialog.show()

            object : CountDownTimer(totalTime, 1000) {

                override fun onTick(millisUntilFinished: Long) {

                    closeBtn.text = "Please wait ${(millisUntilFinished / 1000).toInt()}"
                }

                override fun onFinish() {
                    isTimerRunning = false
                    closeBtn.text = "Continue"

                }
            }.start()
        }

        fun showLoading(context: Context, message: String = "Please wait..") {
            hideLoading()
            progressDialog = ProgressDialog(context)
            progressDialog!!.setMessage(message)
            progressDialog!!.setCancelable(false)
            progressDialog!!.show();
        }

        fun hideLoading() {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss();

            }
        }
    }
}