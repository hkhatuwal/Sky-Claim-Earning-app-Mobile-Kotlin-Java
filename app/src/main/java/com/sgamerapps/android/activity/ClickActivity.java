package com.sgamerapps.android.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.sgamerapps.android.R;
import com.sgamerapps.android.api.ApiClient;
import com.sgamerapps.android.config.AppConfig;
import com.sgamerapps.android.data.Count;
import com.sgamerapps.android.data.User;
import com.sgamerapps.android.utils.DialogHelper;
import com.sgamerapps.android.utils.JwtHelper;
import com.sgamerapps.android.utils.PreferenceManager;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClickActivity extends AppCompatActivity implements MaxAdListener {

    private MaxInterstitialAd mAppLovinInterstitialAd;
    private int retryAttempt;
    ATInterstitial mAtInterstitialAd;

    private boolean unityAdLoaded = false;
    CountDownTimer countDownTimer;
    boolean timeRunning = false, timerComplete = false;
    String type;

    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error) {
        handleAdFailure();
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error) {
        mAppLovinInterstitialAd.loadAd();
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
        mAppLovinInterstitialAd.loadAd();
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
        handleAdClick();
    }

    @Override
    public void onAdHidden(final MaxAd maxAd) {
        mAppLovinInterstitialAd.loadAd();
    }

    Activity activity;
    public static boolean isClicked = false;
    public static boolean verify = false;
    ProgressDialog pb;

    private Button install;


    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            retryAttempt = 0;
            unityAdLoaded = true;


        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            handleAdFailure();
            unityAdLoaded = false;

        }
    };

    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            handleAdClick();
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                // Reward the user for watching the ad to completion
            } else {
                // Do not reward the user for skipping the ad
            }
        }
    };


    private ATInterstitialListener atInterstitialListener = new ATInterstitialListener() {
        @Override
        public void onInterstitialAdLoaded() {
            retryAttempt = 0;
        }

        @Override
        public void onInterstitialAdLoadFail(AdError adError) {
            handleAdFailure();
        }

        @Override
        public void onInterstitialAdClicked(ATAdInfo atAdInfo) {
            handleAdClick();
        }

        @Override
        public void onInterstitialAdShow(ATAdInfo atAdInfo) {
            mAtInterstitialAd.load();
        }

        @Override
        public void onInterstitialAdClose(ATAdInfo atAdInfo) {
        }

        @Override
        public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {
        }

        @Override
        public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {
        }

        @Override
        public void onInterstitialAdVideoError(AdError adError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);
        activity = this;
        type = getIntent().getStringExtra("type");


        pb = new ProgressDialog(activity);
        pb.setMessage("Loading Please Wait....");
        pb.setCancelable(false);

        createInterstitialAd();
        install = findViewById(R.id.installBtn);


        install.setOnClickListener(view -> {
            if (verify) {
                if (isClicked && timerComplete && !timeRunning) {
                    increaseCount();
                    addToWallet(25);
                } else {
                    Toast.makeText(activity, "Not Completed. Try Again", Toast.LENGTH_SHORT).show();
                    createInterstitialAd();
                    verify = false;
                    install.setText("Install Now");
                }
            } else {
                if (isAdReady()) {
                    showAd();
                } else {
                    createInterstitialAd();
                    pb.show();
                    new Handler().postDelayed(() -> {
                        if (isAdReady()) {
                            showAd();
                            pb.hide();

                        } else {
                            Toast.makeText(activity, "No Ads Available. Try Again", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }, 4000);
                }
            }

        });
    }


    private void createInterstitialAd() {

        if (type.equals("spin") || type.equals("spin2") || type.equals("scratch")) {
            if (AppConfig.Companion.isToponEnabled()) {
                createToponInterstitialAd();
            } else {
                createApplovinInterstitial();
            }

        } else {
            createUnityInterstitial();
        }


    }

    private void showAd() {

        if (type.equals("spin") || type.equals("spin2") || type.equals("scratch")) {
            if (AppConfig.Companion.isToponEnabled()) {
                mAtInterstitialAd.show(this);
            } else {
                mAppLovinInterstitialAd.showAd();
            }

        } else {
           showUnityInterstitalAd();
        }
    }

    private boolean isAdReady() {
        if (type.equals("spin") || type.equals("spin2") || type.equals("scratch")) {
            if (AppConfig.Companion.isToponEnabled()) {

                return mAtInterstitialAd.isAdReady();
            } else {
                return mAppLovinInterstitialAd.isReady();
            }

        } else {
            return unityAdLoaded;
        }
    }

    private void createUnityInterstitial() {
        UnityAds.load(getString(R.string.unityInterstitial), loadListener);

    }

    private void createApplovinInterstitial() {
        mAppLovinInterstitialAd = null;
        mAppLovinInterstitialAd = new MaxInterstitialAd(getString(R.string.intestial_Ad), ClickActivity.this);
        mAppLovinInterstitialAd.setListener(this);
        mAppLovinInterstitialAd.loadAd();
    }

    private void createToponInterstitialAd() {
        if (mAtInterstitialAd == null) {
            mAtInterstitialAd = new ATInterstitial(this, getString(R.string.topon_inter));
            mAtInterstitialAd.setAdListener(atInterstitialListener);

        }
        mAtInterstitialAd.load();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void increaseCount() {

        DialogHelper.Companion.showLoading(ClickActivity.this, "Please wait..");

        Intent intent = getIntent();
        ApiClient.Companion.getInstance().updateCount(intent.getStringExtra("type")).enqueue(new Callback<Count>() {
            @Override
            public void onResponse(Call<Count> call, Response<Count> response) {

            }

            @Override
            public void onFailure(Call<Count> call, Throwable t) {

            }
        });
    }

    private void addToWallet(int amount) {
        DialogHelper.Companion.showLoading(ClickActivity.this, "Please wait..");

        // Payload that will be encoded for JWT
        HashMap<String, String> data = new HashMap<>();
        data.put("amount", Integer.toString(amount));

        // Generated token that will be sent to the website
        String token = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            token = new JwtHelper(AppConfig.Companion.getJwtSecret()).generateToken(data);
        }

        // Body that will contain the token
        HashMap<String, String> body = new HashMap<>();
        body.put("data", token);

        ApiClient.Companion.getInstance().addToWallet(body).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                install.setEnabled(false);
                install.setFocusable(false);
                DialogHelper.Companion.hideLoading();

                // Updating user with wallet amount also
                PreferenceManager.Companion.saveUser(response.body());
                AppConfig.Companion.getCurrentUser(true);

                Toast.makeText(activity, "Reward added to wallet successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                DialogHelper.Companion.hideLoading();
            }
        });
    }

    private void showUnityInterstitalAd() {
        UnityAds.show((Activity) getApplicationContext(), getString(R.string.unityInterstitial), new UnityAdsShowOptions(), showListener);

    }

    private void handleAdClick() {
        isClicked = true;
        timeRunning = true;
        new Handler().postDelayed(() -> {
            install.setText("Collect Bonus");
            verify = true;
        }, 10000);
        countDownTimer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long l) {
                timeRunning = true;
            }

            @Override
            public void onFinish() {
                timeRunning = false;
                timerComplete = true;
            }
        }.start();
    }
    //Note: A globally referenced ad object must be created. If the ad object is a temporary variable, the ad may be recycled during the ad loading process and cannot receive ad event callbacks

    private void loadToponInterstitialAd() {
        if (mAtInterstitialAd == null) {
            mAtInterstitialAd = new ATInterstitial(this, getString(R.string.topon_inter));
            mAtInterstitialAd.setAdListener(atInterstitialListener);
        }
        mAtInterstitialAd.load();
    }

    private void handleAdFailure() {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               createInterstitialAd();
            }
        }, delayMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}