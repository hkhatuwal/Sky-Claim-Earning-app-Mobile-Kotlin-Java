package com.sgamerapps.android.config

import android.app.Application
import android.content.Context
import com.anythink.core.api.ATDebuggerConfig
import com.anythink.core.api.ATSDK
import com.applovin.sdk.AppLovinSdk
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal
import com.sgamerapps.android.BuildConfig
import com.sgamerapps.android.R
import com.sgamerapps.android.utils.PreferenceManager
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds


const val ONESIGNAL_APP_ID = "e6c3db52-3573-45ea-a481-5bfcc578f824"

class MyApp : Application(), IUnityAdsInitializationListener {
    companion object {
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        OneSignal.promptForPushNotifications()



        FirebaseApp.initializeApp(this)
        PreferenceManager.init(applicationContext)
        context = applicationContext

//        Applovin
        AppLovinSdk.getInstance(applicationContext).mediationProvider = "max"
        AppLovinSdk.initializeSdk(applicationContext)



//        Unity

        // Initialize the SDK:
        UnityAds.initialize(applicationContext, getString(R.string.unityGameId), BuildConfig.DEBUG, this);

//        Topon

        Stetho.initializeWithDefaults(applicationContext);
        ATSDK.init(applicationContext,getString(R.string.topon_app_id),getString(R.string.topon_app_key))
//        ATSDK.setNetworkLogDebug(true)
//        AudienceNetworkAds.initialize(context);
//        AdSettings.addTestDevice("452bed59-45be-461f-a339-e122cf56e467");


    }

    override fun onInitializationComplete() {
    }
    override fun onInitializationFailed(
        error: UnityAds.UnityAdsInitializationError?,
        message: String?
    ) {
    }
}