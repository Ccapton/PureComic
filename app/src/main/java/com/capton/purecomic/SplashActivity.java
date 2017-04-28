package com.capton.purecomic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.appx.BDSplashAd;

//import com.baidu.appx.BDSplashAd;

public class SplashActivity extends AppCompatActivity {
   public BDSplashAd ad=null;
    private String TAG="BDSplashAd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        final ObjectAnimator animator=ObjectAnimator.ofFloat(findViewById(R.id.loadingIv),"alpha",1f,1f);
        animator.setDuration(1200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //展示开屏广告
               if (ad.isLoaded()) {
                    ad.showAd();
                }else {
                   ad.loadAd();
                }
            }
        });
           ad = new BDSplashAd(SplashActivity.this, getString(R.string.ad_apikey), getString(R.string.fullscreen_ad_id));
            ad.setAdListener(new BDSplashAd.SplashAdListener() {
                @Override
                public void onAdvertisementViewDidHide() {
                    Log.i(TAG, "onAdvertisementViewDidHide: ");
                    ad.destroy();
                    ad = null;
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onAdvertisementDataDidLoadSuccess() {
                    Log.i(TAG, "onAdvertisementDataDidLoadSuccess: ");
                }

                @Override
                public void onAdvertisementDataDidLoadFailure() {
                    Log.i(TAG, "onAdvertisementDataDidLoadFailure: ");
                    ad.destroy();
                    ad = null;
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onAdvertisementViewDidShow() {
                    Log.i(TAG, "onAdvertisementViewDidShow: ");
                }

                @Override
                public void onAdvertisementViewDidClick() {
                    Log.i(TAG, "onAdvertisementViewDidClick: ");
                }

                @Override
                public void onAdvertisementViewWillStartNewIntent() {
                    Log.i(TAG, "onAdvertisementViewWillStartNewIntent: ");
                    ad.destroy();
                    ad = null;
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            });
        animator.start();
    }


}
