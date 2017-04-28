package com.capton.purecomic;

import android.app.Application;
import android.util.Log;

//import com.baidu.appx.BaiduAppX;

import cn.bmob.v3.Bmob;
//import cn.jpush.android.api.JPushInterface;


/**
 * Created by capton on 2017/4/18.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,getString(R.string.bmob_appid));

       // Log.i("BaiduAppX", "BaiduAppX version"+  BaiduAppX.version());
    }

}
