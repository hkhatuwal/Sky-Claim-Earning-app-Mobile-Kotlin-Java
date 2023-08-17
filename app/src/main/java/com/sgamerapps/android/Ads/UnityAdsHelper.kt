package com.sgamerapps.android.Ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.sgamerapps.android.R
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.TaskListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.UnityAdsLoadError
import com.unity3d.ads.UnityAds.UnityAdsShowCompletionState
import com.unity3d.ads.UnityAds.UnityAdsShowError
import com.unity3d.ads.UnityAdsShowOptions
import com.unity3d.services.core.properties.ClientProperties.getApplicationContext
import kotlin.math.log


class UnityAdsHelper  constructor(var context: Context){

    fun  loadAndShowrewardedAds(listener: TaskListener){
        val loadListener: IUnityAdsLoadListener = object : IUnityAdsLoadListener {

            override fun onUnityAdsAdLoaded(placementId: String) {
                DialogHelper.hideLoading()
                Log.d("himanshu", "onUnityAdsAdLoaded: $placementId")

                val showListener: IUnityAdsShowListener = object : IUnityAdsShowListener {
                    override fun onUnityAdsShowFailure(
                        placementId: String,
                        error: UnityAdsShowError,
                        message: String
                    ) {
                        Log.d("himanshu", "onUnityAdsShowFailure: $message")
                        listener.onTaskCompleted(false)
                    }

                    override fun onUnityAdsShowStart(placementId: String) {

                    }

                    override fun onUnityAdsShowClick(placementId: String) {
                    }

                    override fun onUnityAdsShowComplete(
                        placementId: String,
                        state: UnityAdsShowCompletionState
                    ) {
                        Log.d("himanshu", "onUnityAdsShowComplete: ")
                        if (state == UnityAdsShowCompletionState.COMPLETED) {
                            listener.onTaskCompleted(true)


                        } else {
                            listener.onTaskCompleted(false)

                        }
                    }
                }
                UnityAds.show(
                    context as Activity,
                    placementId,
                    UnityAdsShowOptions(),
                    showListener
                )
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAdsLoadError,
                message: String
            ) {
                DialogHelper.hideLoading()
                Log.d("himanshu", "onUnityAdsFailedToLoad: "+message)
                Log.d("himanshu", "onUnityAdsFailedToLoad: "+error.toString())
                listener.onTaskCompleted(false)

            }
        }

        UnityAds.load(context.getString(R.string.unityInterstitial), loadListener);
        DialogHelper.showLoading(context)
    }




}