package com.sgamerapps.android.Ads;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.sgamerapps.android.R;

public class AdsManager {




    public static void nativeBannerAd(Activity activity, ViewGroup container) {
        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(activity.getString(R.string.applovinNativeBanner), activity);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {

                container.setVisibility(View.VISIBLE);
                container.removeAllViews();
                container.addView(nativeAdView);
            }

            @Override
            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                // We recommend retrying with exponentially higher delays up to a maximum delay
            }

            @Override
            public void onNativeAdClicked(final MaxAd ad) {
                // Optional click callback
            }
        });
        nativeAdLoader.loadAd();

    }

    public static void bannerAd(Activity activity, ViewGroup bannerContainer) {
        MaxAdView adView = new MaxAdView(activity.getString(R.string.applovinBanner), MaxAdFormat.BANNER, activity);
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                bannerContainer.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });
        bannerContainer.addView(adView);
        adView.loadAd();
    }
}



