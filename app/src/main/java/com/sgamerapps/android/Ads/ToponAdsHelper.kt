package com.sgamerapps.android.Ads


import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialListener
import com.anythink.nativead.api.ATNative
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.NativeAd
import com.sgamerapps.android.R
import com.sgamerapps.android.utils.DialogHelper
import com.sgamerapps.android.utils.TaskListener


class ToponAdsHelper constructor(var context: Context) {

    var atNatives: ATNative? = null
    var anyThinkNativeAdView: ATNativeAdView? = null
    var mNativeAd: NativeAd? = null
    var adContainer: ViewGroup? = null
    var adViewWidth = 0
    var adViewHeight = 0


    //Note: A globally referenced ad object must be created. If the ad object is a temporary variable, the ad may be recycled during the ad loading process and cannot receive ad event callbacks
    var toponInterstitial: ATInterstitial? = null

     fun loadAdInterstitialAd() {
        if (toponInterstitial == null) {
            toponInterstitial = ATInterstitial(context, context.getString(R.string.topon_inter))
        }
         if (!toponInterstitial!!.isAdReady){
             toponInterstitial!!.setAdListener(object :ATInterstitialListener{
                 override fun onInterstitialAdLoaded() {
                     Log.d("himanshu", "onInterstitialAdLoaded: ")
                 }

                 override fun onInterstitialAdLoadFail(p0: AdError?) {
                     Log.d("himanshu", "onInterstitialAdLoadFail: "+p0.toString())
                 }

                 override fun onInterstitialAdClicked(p0: ATAdInfo?) {

                 }

                 override fun onInterstitialAdShow(p0: ATAdInfo?) {
                 }

                 override fun onInterstitialAdClose(p0: ATAdInfo?) {

                 }

                 override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
                     TODO("Not yet implemented")
                 }

                 override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {

                 }

                 override fun onInterstitialAdVideoError(p0: AdError?) {
                     Log.d("himanshu", "onInterstitialAdLoaded: "+p0.toString())

                 }

             })

             toponInterstitial!!.load()
         }
    }


    fun showInterstitialAd(listener: TaskListener) {
        toponInterstitial!!.setAdListener(object :ATInterstitialListener{
            override fun onInterstitialAdLoaded() {
                Log.d("himanshu", "onInterstitialAdLoaded1: ")
            }

            override fun onInterstitialAdLoadFail(p0: AdError?) {
                Log.d("himanshu", "onInterstitialAdLoadFail1: "+p0.toString())
            }

            override fun onInterstitialAdClicked(p0: ATAdInfo?) {

            }

            override fun onInterstitialAdShow(p0: ATAdInfo?) {
            }

            override fun onInterstitialAdClose(p0: ATAdInfo?) {
                listener!!.onTaskCompleted(true)

            }

            override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
                TODO("Not yet implemented")
            }

            override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {

            }

            override fun onInterstitialAdVideoError(p0: AdError?) {
                Log.d("himanshu", "onInterstitialAdLoaded1: "+p0.toString())

                listener!!.onTaskCompleted(false)
            }

        })
        if (toponInterstitial!!.isAdReady) {
            toponInterstitial!!.show(context as Activity);
        }
        else{
            DialogHelper.showLoading(context)
            toponInterstitial!!.load()
            Handler(Looper.getMainLooper()).postDelayed({
                DialogHelper.hideLoading()
                if (toponInterstitial != null && toponInterstitial!!.isAdReady) {
                    toponInterstitial!!.show(context as Activity)
                }
                else{
                    listener!!.onTaskCompleted(false)

                }
            }, 4000)
        }

    }

    fun loadBannerAds(context: Context, container: ViewGroup) {
        val mBannerView = ATBannerView(context)
        mBannerView.setPlacementId("")

        val width: Int =
            context.resources.displayMetrics.widthPixels //Set a width value, such as screen width

        val height = (width / (320 / 50f)).toInt()

        val localMap: MutableMap<String, Any> = HashMap()
        localMap[ATAdConst.KEY.AD_WIDTH] = width
        localMap[ATAdConst.KEY.AD_HEIGHT] = height
        mBannerView.setLocalExtra(localMap)
        container.addView(mBannerView)

        mBannerView.loadAd()

    }

//    fun initNativeAd(container: ViewGroup) {
//        adContainer = container
//        atNatives = ATNative(
//            context,
//            context.getString(R.string.topon_native),
//            object : ATNativeNetworkListener {
//                override fun onNativeAdLoaded() {
//                    showNativeAd()
//                }
//
//                override fun onNativeAdLoadFail(adError: AdError) {
//                }
//            })
//        if (anyThinkNativeAdView == null) {
//            anyThinkNativeAdView = ATNativeAdView(context)
//        }
//        loadNativeAd()
//
//    }
//
//    private fun loadNativeAd() {
//        if (atNatives != null) {
//            val padding = dip2px(context, 10f)
//            var adViewWidth = context.resources.displayMetrics.widthPixels - 2 * padding
//            var adViewHeight = dip2px(context, 340f) - 2 * padding
//            val localMap: MutableMap<String, Any> = HashMap()
//            localMap[ATAdConst.KEY.AD_WIDTH] = adViewWidth
//            localMap[ATAdConst.KEY.AD_HEIGHT] = adViewHeight
//            atNatives!!.setLocalExtra(localMap)
//            atNatives!!.makeAdRequest()
//        }
//    }
//
//    private fun showNativeAd() {
//        if (atNatives == null) {
//            return
//        }
//
//        val nativeAd: NativeAd? = atNatives!!.nativeAd
//        if (nativeAd != null) {
//            anyThinkNativeAdView?.removeAllViews()
//
//            // Add to your layout
//            if (anyThinkNativeAdView?.parent == null) {
//                adContainer?.addView(
//                    anyThinkNativeAdView,
//                    FrameLayout.LayoutParams(adViewWidth, adViewHeight)
//                )
//            }
//
//            mNativeAd?.destory()
//
//            mNativeAd = nativeAd
//            mNativeAd?.setNativeEventListener(object : ATNativeEventListener {
//                override fun onAdImpressed(view: ATNativeAdView, atAdInfo: ATAdInfo) {
//                    // You can get network and ad unit id of networks in ATAdInfo
//                    // refer to https://docs.toponad.com/#/en-us/android/android_doc/android_sdk_callback_access?id=callback_info
//                }
//
//                override fun onAdClicked(view: ATNativeAdView, atAdInfo: ATAdInfo) {
//                }
//
//                override fun onAdVideoStart(view: ATNativeAdView) {
//                }
//
//                override fun onAdVideoEnd(view: ATNativeAdView) {
//                }
//
//                override fun onAdVideoProgress(view: ATNativeAdView, progress: Int) {
//                }
//            })
//            mNativeAd?.setDislikeCallbackListener(object : ATNativeDislikeListener() {
//                override fun onAdCloseButtonClick(view: ATNativeAdView, atAdInfo: ATAdInfo) {
//                    if (view.parent != null) {
//                        (view.parent as ViewGroup).removeView(view)
//                    }
//                }
//            })
//
//            var height = adViewWidth * 600 / 1024
//            height = if (height <= 0) FrameLayout.LayoutParams.WRAP_CONTENT else height
//
//            var nativePrepareInfo: ATNativePrepareInfo? = null
//
//            if (mNativeAd?.isNativeExpress() == true) {
//                // Template rendering
//                mNativeAd?.renderAdContainer(anyThinkNativeAdView, null)
//            } else {
//                // Self rendering
//                nativePrepareInfo = ATNativePrepareInfo()
//
//                val selfRenderView =
//                    LayoutInflater.from(context).inflate(R.layout.native_ad_item, null)
//                bindSelfRenderView(
//                    context,
//                    mNativeAd?.adMaterial!!,
//                    selfRenderView,
//                    nativePrepareInfo,
//                    height
//                )
//
//                mNativeAd?.renderAdContainer(anyThinkNativeAdView, selfRenderView)
//            }
//
//            mNativeAd?.prepare(anyThinkNativeAdView, nativePrepareInfo)
//        }
//    }

//   private fun bindSelfRenderView(
//        context: Context,
//        adMaterial: ATNativeMaterial,
//        selfRenderView: View,
//        nativePrepareInfo: ATNativePrepareInfo,
//        height: Int
//    ) {
//        val titleView = selfRenderView.findViewById<View>(R.id.native_ad_title) as TextView
//        val descView = selfRenderView.findViewById<View>(R.id.native_ad_desc) as TextView
//        val ctaView = selfRenderView.findViewById<View>(R.id.native_ad_install_btn) as TextView
//        val adFromView = selfRenderView.findViewById<View>(R.id.native_ad_from) as TextView
//        val iconArea = selfRenderView.findViewById<View>(R.id.native_ad_image) as FrameLayout
//        val contentArea =
//            selfRenderView.findViewById<View>(R.id.native_ad_content_image_area) as FrameLayout
//        val logoView = selfRenderView.findViewById<View>(R.id.native_ad_logo) as ATNativeImageView
//        val closeView = selfRenderView.findViewById<View>(R.id.native_ad_close)
//        val clickViewList: MutableList<View> = ArrayList() //click views
//
//        // bind view
//        val title = adMaterial.title
//        val descriptionText = adMaterial.descriptionText
//        val adIconView = adMaterial.adIconView
//        val iconImageUrl = adMaterial.iconImageUrl
//        val callToActionText = adMaterial.callToActionText
//        val mediaView = adMaterial.getAdMediaView(contentArea)
//        val adChoiceIconUrl = adMaterial.adChoiceIconUrl
//        val adFrom = adMaterial.adFrom
//        val adLogoView = adMaterial.adLogoView
//
//        // title
//        titleView.text = title
//        nativePrepareInfo.titleView = titleView //bind title
//        clickViewList.add(titleView)
//
//        // desc
//        descView.text = descriptionText
//        nativePrepareInfo.descView = descView //bind desc
//        clickViewList.add(descView)
//
//        // cta button
//        if (!TextUtils.isEmpty(callToActionText)) {
//            ctaView.text = callToActionText
//        } else {
//            ctaView.visibility = View.GONE
//        }
//        nativePrepareInfo.ctaView = ctaView //bind cta button
//        clickViewList.add(ctaView)
//
//        // icon
//        val iconView = ATNativeImageView(context)
//        if (adIconView == null) {
//            iconArea.addView(iconView)
//            iconView.setImage(iconImageUrl)
//            nativePrepareInfo.iconView = iconView //bind icon
//            clickViewList.add(iconView)
//        } else {
//            iconArea.addView(adIconView)
//            nativePrepareInfo.iconView = adIconView //bind icon
//            clickViewList.add(adIconView)
//        }
//
//        // media view
//        if (mediaView != null) {
//            if (mediaView.parent != null) {
//                (mediaView.parent as ViewGroup).removeView(mediaView)
//            }
//            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height)
//            params.gravity = Gravity.CENTER
//            mediaView.layoutParams = params
//            contentArea.addView(mediaView, params)
//            clickViewList.add(mediaView)
//        } else {
//            if (!TextUtils.isEmpty(adMaterial.videoUrl)) {
//                val playerView = initializePlayer(context, adMaterial.videoUrl)
//                val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height)
//                params.gravity = Gravity.CENTER
//                playerView.layoutParams = params
//                contentArea.addView(playerView, params)
//                clickViewList.add(playerView)
//            } else {
//                val imageView = ATNativeImageView(context)
//                imageView.setImage(adMaterial.mainImageUrl)
//                val params: ViewGroup.LayoutParams =
//                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height)
//                imageView.layoutParams = params
//                contentArea.addView(imageView, params)
//                nativePrepareInfo.mainImageView = imageView //bind main image
//                clickViewList.add(imageView)
//            }
//        }
//
//
//        // ad from
//        if (!TextUtils.isEmpty(adFrom)) {
//            adFromView.text = adFrom
//        } else {
//            adFromView.visibility = View.GONE
//        }
//        nativePrepareInfo.adFromView = adFromView //bind ad from
//
//        // ad choice
//        if (!TextUtils.isEmpty(adChoiceIconUrl)) {
//            logoView.setImage(adChoiceIconUrl)
//            nativePrepareInfo.adLogoView = logoView //bind ad choice
//        } else if (adLogoView != null) {
//            val logoContainer =
//                selfRenderView.findViewById<FrameLayout>(R.id.native_ad_logo_container)
//            if (logoContainer != null) {
//                logoContainer.visibility = View.VISIBLE
//                logoContainer.addView(adLogoView)
//                nativePrepareInfo.adLogoView = adLogoView //bind ad choice
//            }
//        }
//        val domain = adMaterial.domain //(v6.1.20+) Yandex domain
//
//        val warning = adMaterial.warning //(v6.1.20+) Yandex warning
//
//        val layoutParams =
//            FrameLayout.LayoutParams(dip2px(context, 40f), dip2px(context, 10f)) //ad choice
//        layoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
//        nativePrepareInfo.choiceViewLayoutParams = layoutParams //bind layout params for ad choice
//        nativePrepareInfo.closeView = closeView //bind close button
//        nativePrepareInfo.clickViewList = clickViewList //bind click view list
//        if (nativePrepareInfo is ATNativePrepareExInfo) {
//            val creativeClickViewList: MutableList<View> = ArrayList() //click views
//            creativeClickViewList.add(ctaView)
//            nativePrepareInfo.creativeClickViewList = creativeClickViewList //bind custom view list
//        }
//    }
//
//    private fun initializePlayer(context: Context, url: String): View {
//        val videoView = VideoView(context)
//        videoView.setVideoURI(Uri.parse(url))
//        videoView.setOnPreparedListener { }
//        videoView.start()
//        return videoView
//    }
//
//    private fun dip2px(context: Context, dipValue: Float): Int {
//        val scale = context.resources.displayMetrics.density
//        return (dipValue * scale + 0.5f).toInt()
//    }


}