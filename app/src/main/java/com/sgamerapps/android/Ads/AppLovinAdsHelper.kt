package com.sgamerapps.android.Ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.sgamerapps.android.R
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.TaskListener

class AppLovinAdsHelper constructor(var context:Context) {

    private var mInterstitialAd:MaxInterstitialAd?=null

     fun createInterstitialAd(){
        mInterstitialAd = null
        mInterstitialAd = MaxInterstitialAd(context.getString(R.string.intestial_Ad), context as Activity?)
        mInterstitialAd!!.loadAd()
    }

     fun showInterstitialAd( listener: TaskListener) {
         if(mInterstitialAd!=null){
             mInterstitialAd!!.setListener(object :MaxAdListener{
                 override fun onAdLoaded(p0: MaxAd?) {

                 }

                 override fun onAdDisplayed(p0: MaxAd?) {
                     listener!!.onTaskCompleted(true)
                 }

                 override fun onAdHidden(p0: MaxAd?) {
                 }

                 override fun onAdClicked(p0: MaxAd?) {
                     TODO("Not yet implemented")
                 }

                 override fun onAdLoadFailed(p0: String?, p1: MaxError?) {
                     listener!!.onTaskCompleted(false)
                 }

                 override fun onAdDisplayFailed(p0: MaxAd?, p1: MaxError?) {
                     listener!!.onTaskCompleted(false)

                 }

             })

         }

         if (mInterstitialAd != null && mInterstitialAd!!.isReady) {
            if(listener!=null){

            }
            mInterstitialAd!!.showAd()
        } else {

            DialogHelper.showLoading(context)
            mInterstitialAd!!.loadAd()
            Handler(Looper.getMainLooper()).postDelayed({
                DialogHelper.hideLoading()

                if (mInterstitialAd != null && mInterstitialAd!!.isReady) {
                    mInterstitialAd!!.showAd()
                }
            }, 2000)
        }
    }


}