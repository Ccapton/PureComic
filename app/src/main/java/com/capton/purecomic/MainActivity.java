package com.capton.purecomic;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
//import com.baidu.appx.BDBannerAd;
import com.bumptech.glide.Glide;
import com.capton.purecomic.CustomView.CircleImageView;
import com.capton.purecomic.DataModel.BmobData.About;
import com.capton.purecomic.DataModel.BmobData.MyUser;
import com.capton.purecomic.DataModel.BmobData.Suggestion;
import com.capton.purecomic.DataModel.BmobData.UpdateForcast;
import com.capton.purecomic.DataModel.BookList.book;
import com.capton.purecomic.DataModel.ChapterList.Chapter;
import com.capton.purecomic.DataModel.ChapterList.result;
import com.capton.purecomic.DataModel.CollectionComic;
import com.capton.purecomic.Utils.DisplayUtil;
import com.capton.purecomic.Utils.NetworkState;
import com.capton.purecomic.Utils.PackageUtils;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import chen.capton.indicator.PagerIndicator;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import q.rorbin.badgeview.QBadgeView;


public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private PagerIndicator mPagerIndicator;
    private ComicFragment mComicFragment;
    private ComicFragment mComicFragment2;
    private ComicFragment mComicFragment3;
    private ComicFragment mComicFragment4;
    private ArrayList<ComicFragment> fragmentList;
    private ArrayList<String> typeList;

    private LinearLayout topbar,userLayout,optionLayout,collectionLayout,setting,exit,lastReadLayout,searchbarLayout,searchContentLayout;
    private DrawerLayout drawerlayout;
    private Button openSearchBtn,closeLoginBtn,closeSigninBtn,loginBtn,signinBtn,logoutBtn,closeSettingBtn,closeShowBtn,closeSearchbarBtn,searchBtn;
    private SwitchButton switchButton;

    private ImageView sexIv;
    private LinearLayout loginLayout,signinLayout,settingLayout,forgotPswLayout,userinfoShowLayout;
    private TextView usernameTv,usernameTv3,signinTv,confirmTv,confirmTvforgot,nickNameTv,forgotPswTv,
            editUserinfoTv,upgradeTv,suggestTv,aboutTv;
    private EditText userEtSignin,pswEtSigin,userEt,pswEt,confirmCodeEt,confirmCodeEtforgot,searchEt;
    private CircleImageView userIv,userIv2,userIv3;
    private MyUser mMyUser;
    private Handler handler;
    private boolean isInOneMinute=true;
    private boolean isCounting=true;
    private boolean isInOneMinute2=true;
    private boolean isCounting2=true;
    private int oneMinute=60;
    private int oneMinute2=60;
    private Boolean isForgotPsw=false;
    private Boolean isSearchBarShow=false;
    private SharedPreferences spf3;
    private ResultpageFragment mResultFragment;
    private RelativeLayout adLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spf=getSharedPreferences("LoginHistory",MODE_PRIVATE);
        spf2=getSharedPreferences("ReadHistory",MODE_PRIVATE);
        spf3=getSharedPreferences("ScreenToggle",MODE_PRIVATE);
        mMyUser= BmobUser.getCurrentUser(MyUser.class);
        handler=new Handler();
        setContentView(R.layout.activity_main);
        initView();
        if(NetworkState.isMobileConnected(this)&&!NetworkState.isWifiConnected(this)){
            Toast.makeText(this,"你正在使用移动流量呦",Toast.LENGTH_LONG).show();
        }
    }
private void toast(String content,int type){
    if(type==1)
      Toast.makeText(this,content,Toast.LENGTH_LONG).show();
    else
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
}
    private boolean isInputCorrect(int type){
        if(type==0) {
            if (userEtSignin.getText().toString().length() != 11) {
                toast("请输入11手机号", 0);
            } else {
                if (pswEtSigin.getText().toString().length() < 6) {
                    toast("请输入6位或以上的密码", 0);
                } else {
                    return true;
                }
            }
        }else {
            if (userEt.getText().toString().length() != 11) {
                toast("请输入11手机号", 0);
            } else {
                return true;
            }
        }
        return false;
    }

    private void initView() {
        topbar= (LinearLayout) findViewById(R.id.topbar);
        settingLayout= (LinearLayout) findViewById(R.id.settingLayout);

        mComicFragment=new ComicFragment( );
        mComicFragment2=new ComicFragment( );
        mComicFragment3=new ComicFragment( );
        mComicFragment4=new ComicFragment( );

        Bundle bundle=new Bundle();
        bundle.putString("url",getString(R.string.api_book)+"type="+"少年漫画"+"&key="+getString(R.string.api_key));
        bundle.putString("api",getString(R.string.api_book));
        bundle.putString("type","少年漫画");
        bundle.putInt("skip",0);
        bundle.putString("key",getString(R.string.api_key));
        mComicFragment.setArguments(bundle);

        Bundle bundle2=new Bundle();
        bundle2.putString("url",getString(R.string.api_book)+"type="+"青年漫画"+"&key="+getString(R.string.api_key));
        bundle2.putString("api",getString(R.string.api_book));
        bundle2.putString("type","青年漫画");
        bundle2.putInt("skip",0);
        bundle2.putString("key",getString(R.string.api_key));
        mComicFragment2.setArguments(bundle2);

        Bundle bundle3=new Bundle();
        bundle3.putString("url",getString(R.string.api_book)+"type="+"少女漫画"+"&key="+getString(R.string.api_key));
        bundle3.putString("api",getString(R.string.api_book));
        bundle3.putString("type","少女漫画");
        bundle3.putInt("skip",0);
        bundle3.putString("key",getString(R.string.api_key));
        mComicFragment3.setArguments(bundle3);

        Bundle bundle4=new Bundle();
        bundle4.putString("url",getString(R.string.api_book)+"type="+"耽美漫画"+"&key="+getString(R.string.api_key));
        bundle4.putString("api",getString(R.string.api_book));
        bundle4.putString("type","耽美漫画");
        bundle4.putInt("skip",0);
        bundle4.putString("key",getString(R.string.api_key));
        mComicFragment4.setArguments(bundle4);

        mViewPager= (ViewPager) findViewById(R.id.viewpager);
        mPagerIndicator= (PagerIndicator) findViewById(R.id.indicator);

        fragmentList=new ArrayList<>();
        fragmentList.add(mComicFragment);
        fragmentList.add(mComicFragment2);
        fragmentList.add(mComicFragment3);
        fragmentList.add(mComicFragment4);

         mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        typeList=new ArrayList<>();
        typeList.add("少年漫画");
        typeList.add("青年漫画");
        typeList.add("少女漫画");
        typeList.add("耽美漫画");

        mPagerIndicator.setTitleList(typeList);
        mPagerIndicator.setViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                topbar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                topbar.setVisibility(View.GONE);
            }
        });

        optionLayout= (LinearLayout) findViewById(R.id.optionLayout);
        collectionLayout= (LinearLayout) findViewById(R.id.collectionLayout);
        collectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.closeDrawer(GravityCompat.START);
                startActivityForResult(new Intent(MainActivity.this, CollectionActivity.class),3);
            }
        });
        setting= (LinearLayout) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionLayout.setVisibility(View.GONE);
            }
        });
        exit= (LinearLayout) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.get(MainActivity.this).clearMemory();
                finish();
            }
        });
        ( (ImageView)exit.getChildAt(0)).setImageResource(R.mipmap.back_g);

        drawerlayout= (DrawerLayout) findViewById(R.id.drawerlayout);
        userLayout= (LinearLayout) findViewById(R.id.userLayout);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerlayout.openDrawer(GravityCompat.START,true);
            }
        });

        closeSettingBtn= (Button) findViewById(R.id.closeSettingBtn);
        closeSettingBtn.setBackgroundResource(R.mipmap.back_g);
        closeSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionLayout.setVisibility(View.VISIBLE);
            }
        });

        openSearchBtn= (Button) findViewById(R.id.openSearchbarBtn);
        openSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearchBarShow=true;
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(searchbarLayout, "translationY",
                                -DisplayUtil.dip2px(MainActivity.this,50),0F);
                animator.setDuration(300);
                animator.start();
                ObjectAnimator animator2=
                        ObjectAnimator.ofFloat(searchContentLayout, "translationY",
                                DisplayUtil.dip2px(MainActivity.this,(DisplayUtil.getScreenHeightDp(MainActivity.this)-50)),0F);
                animator2.setDuration(300);
                animator2.start();
            }
        });

        loginLayout= (LinearLayout) findViewById(R.id.loginLayout);
        loginLayout.setY(DisplayUtil.getScreenHeightPx(this));

        usernameTv= (TextView) findViewById(R.id.usernameTv);
        usernameTv3= (TextView) findViewById(R.id.usernameTv3);

        sexIv= (ImageView) findViewById(R.id.sexIv);

        usernameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BmobUser.getCurrentUser()==null){
                drawerlayout.closeDrawer(GravityCompat.START);
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(loginLayout, "translationY",
                                DisplayUtil.getScreenHeightPx(MainActivity.this),DisplayUtil.dip2px(MainActivity.this,44));
                animator.setDuration(300);
                animator.start();
                }
            }
        });
        closeLoginBtn= (Button) findViewById(R.id.closeLoginBtn);
        closeLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(loginLayout, "translationY",
                                DisplayUtil.dip2px(MainActivity.this,44),DisplayUtil.getScreenHeightPx(MainActivity.this));
                animator.setDuration(300);
                animator.start();
            }
        });

        signinLayout= (LinearLayout) findViewById(R.id.signinLayout);
        signinLayout.setX(DisplayUtil.getScreenWidthPx(this));
        signinLayout.setY(DisplayUtil.dip2px(this,44));
        signinTv= (TextView) findViewById(R.id.signInTv);
        signinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(signinLayout, "translationX",
                                DisplayUtil.getScreenWidthPx(MainActivity.this),0F);
                animator.setDuration(300);
                animator.start();
            }
        });
        closeSigninBtn= (Button) findViewById(R.id.closeSigninBtn);
        closeSigninBtn.setBackgroundResource(R.mipmap.back_g);
        closeSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(signinLayout, "translationX",
                                0F,DisplayUtil.getScreenWidthPx(MainActivity.this));
                animator.setDuration(300);
                animator.start();
            }
        });
        userEtSignin= (EditText) findViewById(R.id.user_Signin);
        pswEtSigin= (EditText) findViewById(R.id.psw_SignIn);
        userEt= (EditText) findViewById(R.id.user);
        userEt.setText(spf.getString("username",""));
        pswEt= (EditText) findViewById(R.id.psw);

        loginBtn= (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!userEt.getText().toString().equals(""))&&(!pswEt.getText().toString().equals(""))){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    loginBtn.setText("登录中...");
                    if(forgotPswLayout.getVisibility()==View.GONE||confirmCodeEtforgot.getText().toString().equals("")){
                        isForgotPsw=false;
                    }else {
                        isForgotPsw=true;
                    }
                    if(!isForgotPsw) {
                        logIn(userEt.getText().toString(), pswEt.getText().toString());
                    }else {
                        BmobUser bmobUser=new BmobUser();
                        bmobUser.setUsername(userEt.getText().toString());
                        bmobUser.resetPasswordBySMSCode(confirmCodeEtforgot.getText().toString(), pswEt.getText().toString(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    logIn(userEt.getText().toString(), pswEt.getText().toString());
                                    toast("重置密码成功",0);
                                }else {
                                    if(e.getErrorCode()==9018){
                                        toast("请输入验证码",0);
                                    }
                                    if(e.getErrorCode()==207){
                                        toast("验证码错误",0);
                                    }
                                    loginBtn.setText("登录");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else {
                    if(userEt.getText().toString().equals("")){
                        toast("请输入手机号",0);
                    }else {
                        if(pswEt.getText().toString().equals("")){
                            toast("请输入密码",0);
                        }
                    }
                }
            }
        });

        confirmTv= (TextView) findViewById(R.id.comfirmCodeTv);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInOneMinute){
                if(isInputCorrect(0)) {
                    confirmTv.setText("已发送(60s)");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            while (isCounting) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (oneMinute != 0) {
                                    isInOneMinute=false;
                                    isCounting=true;
                                    oneMinute--;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            confirmTv.setText("已发送(" + oneMinute + "s)");
                                        }
                                    });
                                } else {
                                    oneMinute = 60;
                                    isInOneMinute= true;
                                    isCounting=false;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            confirmTv.setText("获取验证码");
                                        }
                                    });
                                }

                            }
                        }
                    }).start();
                    BmobSMS.requestSMSCode(userEtSignin.getText().toString(), getString(R.string.bmob_sms_pattern), new QueryListener<Integer>() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                toast("已发送验证码", 0);
                            } else {
                                if(e.getErrorCode()==10010){
                                    toast("此用户超过验证次数",0);
                                }
                                e.printStackTrace();
                            }
                        }
                    });
                }
                }else {
                    toast("稍后重试",0);
                }
            }
        });
        confirmTvforgot= (TextView) findViewById(R.id.comfirmCodeTvforgot);
        confirmTvforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInOneMinute2){
                    if(isInputCorrect(1)) {
                        confirmTvforgot.setText("已发送(60s)");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (isCounting2) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (oneMinute2 != 0) {
                                        oneMinute2--;
                                        isCounting2=true;
                                        isInOneMinute2=false;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                confirmTvforgot.setText("已发送(" + oneMinute2 + "s)");
                                            }
                                        });
                                    } else {
                                        oneMinute2 = 60;
                                        isCounting2=false;
                                        isInOneMinute2 = true;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                confirmTvforgot.setText("获取验证码");
                                            }
                                        });
                                    }

                                }
                            }
                        }).start();
                        BmobSMS.requestSMSCode(userEt.getText().toString(), getString(R.string.bmob_sms_pattern), new QueryListener<Integer>() {
                            @Override
                            public void done(Integer integer, BmobException e) {
                                if (e == null) {
                                    toast("已发送验证码", 0);
                                     System.out.println("手机号 "+userEt.getText().toString());
                                    BmobSMS.requestSMSCodeObservable(userEt.getText().toString(),getString(R.string.bmob_sms_pattern));
                                } else {
                                    if(e.getErrorCode()==10010){
                                        toast("此用户超过验证次数",0);
                                    }
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else {
                    toast("稍后重试",0);
                }
            }
        });
        confirmCodeEtforgot= (EditText) findViewById(R.id.comfirmCodeForgot);

        confirmCodeEt= (EditText) findViewById(R.id.comfirmCode);

        nickNameTv= (TextView) findViewById(R.id.nickName);
        userIv= (CircleImageView) findViewById(R.id.userIv);
        userIv2= (CircleImageView) findViewById(R.id.userIv2);
        userIv3= (CircleImageView) findViewById(R.id.userIv3);
        userIv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  openPicturePicker();
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(userinfoShowLayout, "translationY",
                                -DisplayUtil.getScreenHeightPx(MainActivity.this),0F);
                animator.setDuration(300);
                animator.start();
            }
        });

        forgotPswLayout= (LinearLayout) findViewById(R.id.forgotPswLayout);
        forgotPswTv= (TextView) findViewById(R.id.forgotPsw);
        forgotPswTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPswLayout.setVisibility(View.VISIBLE);
                forgotPswTv.setText("将使用当前输入的密码为新密码登录");
                toast("使用当前密码为新密码登录",0);
            }
        });
        signinBtn= (Button) findViewById(R.id.signInBtn);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                    if(isInputCorrect(0)){
                        if(confirmCodeEt.getText().toString().length()!=0){
                            BmobSMS.verifySmsCode(userEtSignin.getText().toString(), confirmCodeEt.getText().toString(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                 if(e==null){
                                     signUp(userEtSignin.getText().toString(),pswEtSigin.getText().toString());
                                 }else{
                                     if(e.getErrorCode()==207){
                                         toast("验证码错误",0);
                                     }
                                     e.printStackTrace();
                                 }
                                }
                            });
                        }else{
                            toast("请输入短信验证码",0);
                        }
                    }
            }
        });
        logoutBtn= (Button) findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setMessage("确定要注销此用户吗？").setTitle("注意")
                        .setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mMyUser!=null) {
                            BmobUser.logOut();
                            BmobUser currentUser = BmobUser.getCurrentUser();
                        }
                        afterLogout();
                    }
                }).create().show();
            }
        });

        editUserinfoTv= (TextView) findViewById(R.id.editUserinfoTv);
        editUserinfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EditUserInfoActivity.class);
                startActivityForResult(intent,2);
            }
        });

        userinfoShowLayout= (LinearLayout) findViewById(R.id.userinfoShowLayout);
        userinfoShowLayout.setY(-DisplayUtil.getScreenHeightPx(MainActivity.this));

        closeShowBtn= (Button) findViewById(R.id.closeShowBtn);
        closeShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(userinfoShowLayout, "translationY",
                                0F,-DisplayUtil.getScreenHeightPx(MainActivity.this));
                animator.setDuration(300);
                animator.start();
                userEtSignin.setText("");
                pswEtSigin.setText("");
                confirmCodeEt.setText("");
            }
        });

        historyComic= JSON.parseObject(spf2.getString("lastReadComic",""),CollectionComic.class);
        if(historyComic!=null){
            ((TextView) findViewById(R.id.lastReadTv)).setText(historyComic.getComic());
        }
        lastReadLayout=(LinearLayout)findViewById(R.id.lastReadLayout);
        lastReadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(historyComic==null) {
                    toast("没有记录",0);
                }else {
                    Intent intent = new Intent(MainActivity.this, ReadingActivity.class);
                    Log.i("lastReadLayout ", "onclick: "+historyComic.toString());
                    result chapResult = JSON.parseObject(historyComic.getChapterResult(), result.class);
                    Chapter chapter = chapResult.getChapterList().get(historyComic.getChapterIndex());
                    book tempBook = JSON.parseObject(historyComic.getBook(), book.class);
                    intent.putExtra("chapter", chapter);
                    intent.putExtra("result", chapResult);
                    intent.putExtra("index", historyComic.getChapterIndex());
                    intent.putExtra("page", historyComic.getPageIndex());
                    intent.putExtra("book", (Serializable) tempBook);
                    intent.setAction("MainActivity");
                    startActivityForResult(intent,3);
                }
            }
        });
        switchButton= (SwitchButton) findViewById(R.id.landscapeSwitchBtn);
        switchButton.setBackgroundColor(Color.WHITE);
        switchButton.setGravity(Gravity.CENTER);
        switchButton.setThumbColorRes(R.color.colorPrimary);
        switchButton.setChecked(spf3.getBoolean("isSensorOn",false));
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spf3.edit().putBoolean("isSensorOn",true).apply();
                }else {
                    spf3.edit().putBoolean("isSensorOn",false).apply();
                }
            }
        });
        searchbarLayout= (LinearLayout) findViewById(R.id.searchbarLayout);
        searchbarLayout.setY(-DisplayUtil.dip2px(this,50));
        searchContentLayout= (LinearLayout) findViewById(R.id.searchContentLayout);
        searchContentLayout.setY(DisplayUtil.dip2px(this,DisplayUtil.getScreenHeightDp(this)-50));
        closeSearchbarBtn= (Button) findViewById(R.id.closeSearchbarBtn);
        closeSearchbarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearchBarShow=false;
                ObjectAnimator animator=
                        ObjectAnimator.ofFloat(searchbarLayout, "translationY",
                                0F,-DisplayUtil.dip2px(MainActivity.this,50));
                animator.setDuration(300);
                animator.start();
                ObjectAnimator animator2=
                        ObjectAnimator.ofFloat(searchContentLayout, "translationY",
                                0F,DisplayUtil.dip2px(MainActivity.this,DisplayUtil.getScreenHeightDp(MainActivity.this)-50));
                animator2.setDuration(300);
                animator2.start();
            }
        });
        searchBtn= (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchEt.getText().toString().equals("")) {
                    if(mResultFragment==null) {
                        ResultpageFragment  resultFragment = new ResultpageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("api", getString(R.string.api_book));
                        bundle.putInt("skip", 0);
                        bundle.putString("name", searchEt.getText().toString());
                        bundle.putString("key", getString(R.string.api_key));
                        resultFragment.setArguments(bundle);

                        FragmentManager mamager = getSupportFragmentManager();
                        FragmentTransaction transaction = mamager.beginTransaction();
                        transaction.add(R.id.searchContentLayout, resultFragment);
                        transaction.commit();
                        mResultFragment=resultFragment;
                    } else {
                        ResultpageFragment  resultFragment = new ResultpageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("api", getString(R.string.api_book));
                        bundle.putInt("skip", 0);
                        bundle.putString("name", searchEt.getText().toString());
                        bundle.putString("key", getString(R.string.api_key));
                        resultFragment.setArguments(bundle);

                        FragmentManager mamager = getSupportFragmentManager();
                        FragmentTransaction transaction = mamager.beginTransaction();
                        transaction.hide(mResultFragment);
                        transaction.remove(mResultFragment);
                        transaction.add(R.id.searchContentLayout,resultFragment);
                        transaction.commit();
                        mResultFragment=resultFragment;
                    }
                }else {
                    toast("请输入关键词",0);
                }
            }
        });
        searchEt= (EditText) findViewById(R.id.searchEt);
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    if(!searchEt.getText().toString().equals("")) {
                        if(mResultFragment==null) {
                            ResultpageFragment  resultFragment = new ResultpageFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("api", getString(R.string.api_book));
                            bundle.putInt("skip", 0);
                            bundle.putString("name", searchEt.getText().toString());
                            bundle.putString("key", getString(R.string.api_key));
                            resultFragment.setArguments(bundle);

                            FragmentManager mamager = getSupportFragmentManager();
                            FragmentTransaction transaction = mamager.beginTransaction();
                            transaction.add(R.id.searchContentLayout, resultFragment);
                            transaction.commit();
                            mResultFragment=resultFragment;
                        } else {
                            ResultpageFragment  resultFragment = new ResultpageFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("api", getString(R.string.api_book));
                            bundle.putInt("skip", 0);
                            bundle.putString("name", searchEt.getText().toString());
                            bundle.putString("key", getString(R.string.api_key));
                            resultFragment.setArguments(bundle);

                            FragmentManager mamager = getSupportFragmentManager();
                            FragmentTransaction transaction = mamager.beginTransaction();
                            transaction.hide(mResultFragment);
                            transaction.remove(mResultFragment);
                            transaction.add(R.id.searchContentLayout,resultFragment);
                            transaction.commit();
                            mResultFragment=resultFragment;
                        }
                    }else {
                        toast("请输入关键词",0);
                    }
                    return false;
                }
                return false;

            }

        });

        upgradeTv= (TextView) findViewById(R.id.upgrade);

        BmobQuery<UpdateForcast> query=new BmobQuery<UpdateForcast>();
        query.getObject("dKM7111P", new QueryListener<UpdateForcast>() {
            @Override
            public void done(UpdateForcast updateForcast, BmobException e) {
                if(e==null){
                    forcast=updateForcast;
                    if(forcast!=null) {
                        if (forcast.isNewVersion()) {
                            if (!forcast.getVersion().equals(PackageUtils.getVersionName(MainActivity.this))) {
                                upgradeBadeView = new QBadgeView(MainActivity.this);
                                upgradeBadeView.bindTarget(upgradeTv).setBadgeText("新版本");
                            }
                        }
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });
        upgradeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upgradeBadeView!=null)
                upgradeBadeView.hide(true);
                if(forcast!=null&&!forcast.getVersion().equals(PackageUtils.getVersionName(MainActivity.this) )){
                    openUpgradeDiaglog(forcast, forcast.getCharacterList(), forcast.getUrl());
                }else {
                    final BmobQuery<UpdateForcast> query = new BmobQuery<UpdateForcast>();
                    query.getObject("bwWOFFFI", new QueryListener<UpdateForcast>() {
                        @Override
                        public void done(UpdateForcast updateForcast, BmobException e) {
                            if (e == null) {
                                forcast = updateForcast;
                                openUpgradeDiaglog(forcast, forcast.getCharacterList(), forcast.getUrl());
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        suggestTv= (TextView) findViewById(R.id.suggest);

        BmobQuery<Suggestion> query2=new BmobQuery<Suggestion>();
        query2.addWhereEqualTo("myUser", BmobUser.getCurrentUser());
        query2.findObjects(new FindListener<Suggestion>() {
            @Override
            public void done(List<Suggestion> list, BmobException e) {
                if(e==null){
                    if(list.size()!=0){
                        suggestion=list.get(0);
                        if(!suggestion.getResponse().equals("")) {
                            suggestBadeView = new QBadgeView(MainActivity.this);
                            suggestBadeView.bindTarget(suggestTv).setBadgeText("有回复");
                        }
                    }
                }else {
                  e.printStackTrace();
                }
            }
        });

        suggestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BmobUser.getCurrentUser()!=null) {
                    if(suggestion==null) {
                        BmobQuery<Suggestion> query = new BmobQuery<Suggestion>();
                        query.addWhereEqualTo("myUser", BmobUser.getCurrentUser());
                        query.findObjects(new FindListener<Suggestion>() {
                            @Override
                            public void done(List<Suggestion> list, BmobException e) {
                                if (e == null) {
                                    if(list.size()==0){
                                        sendSuggestion();
                                    }else {
                                        if (!("").equals(list.get(0).getResponse()))
                                            openSuggestDialog(list.get(0), true);
                                        else
                                            openSuggestDialog(list.get(0), false);
                                    }
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else {
                        if (!("").equals(suggestion.getResponse())) {
                            openSuggestDialog(suggestion, true);
                            suggestion=null;
                        }
                        else {
                            openSuggestDialog(suggestion, false);
                        }
                    }
                }else {
                    toast("请先登录",0);
                }
            }

        });
        aboutTv= (TextView) findViewById(R.id.about);
        aboutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<About> query=new BmobQuery<About>();
                query.getObject("CeK5iiiB", new QueryListener<About>() {
                    @Override
                    public void done(About about, BmobException e) {
                      if(e==null){
                          openAboutDialog(about);
                      }else {
                          e.printStackTrace();
                      }
                    }
                });

            }
        });

        findViewById(R.id.weiboLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("暂未开通",0);
            }
        });
        findViewById(R.id.wechatLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("暂未开通",0);
            }
        });
        if(mMyUser==null){
            logoutBtn.setVisibility(View.GONE);
            editUserinfoTv.setVisibility(View.GONE);
        }else {
            logoutBtn.setVisibility(View.VISIBLE);
            editUserinfoTv.setVisibility(View.VISIBLE);
            usernameTv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            nickNameTv.setText(mMyUser.getNick());
            usernameTv.setText(mMyUser.getNick());
            usernameTv3.setText(mMyUser.getNick());
            if(mMyUser.getSex().equals("male"))
                sexIv.setImageResource(R.mipmap.male);
            else
                sexIv.setImageResource(R.mipmap.female);
            if(mMyUser.getPicture()!=null) {
                Glide.with(this).load(mMyUser.getPicture()).into(userIv);
                Glide.with(this).load(mMyUser.getPicture()).into(userIv2);
                Glide.with(this).load(mMyUser.getPicture()).into(userIv3);
            }
        }

        adLayout= (RelativeLayout) findViewById(R.id.adLayout);


    }

    private String TAG="BDBannerAd";
   // private BDBannerAd bannerview;
    
    private void openSuggestDialog(Suggestion suggestion,Boolean isResponse) {
        if(isResponse) {
            new AlertDialog.Builder(this).setTitle("作者回复：").setMessage(suggestion.getResponse())
                    .setNegativeButton("取消", null).setPositiveButton("继续反馈", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendSuggestion();
                }
            }).create().show();
            if(suggestBadeView!=null)
            suggestBadeView.hide(true);
        }else {
            sendSuggestion();
        }
    }

    private void sendSuggestion(){
        View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_suggest,null);
        final EditText suggestEt= (EditText) view.findViewById(R.id.suggestEt);
        final Button suggestBtn= (Button) view.findViewById(R.id.suggestBtn);
        final AlertDialog alertDialog=new AlertDialog.Builder(MainActivity.this).setView(view).create();
        alertDialog.show();
        suggestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(suggestEt.getText().toString().equals("")){
                    toast("请输入留言再发送",0);
                }else {
                    BmobQuery<Suggestion> query = new BmobQuery<Suggestion>();
                    query.addWhereEqualTo("myUser", BmobUser.getCurrentUser(MyUser.class));
                    query.findObjects(new FindListener<Suggestion>() {
                        @Override
                        public void done(List<Suggestion> list, BmobException e) {
                            if (e == null) {
                                if (list.size() == 0) {
                                    Suggestion suggestion = new Suggestion();
                                    suggestion.setMyUser(BmobUser.getCurrentUser(MyUser.class));
                                    suggestion.setContent(suggestEt.getText().toString());
                                    suggestion.setResponse("");
                                    suggestion.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e==null){
                                                alertDialog.cancel();
                                                toast("反馈已发送",0);
                                            }else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }else {
                                    list.get(0).setContent(suggestEt.getText().toString());
                                    list.get(0).update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                alertDialog.cancel();
                                                toast("反馈已发送",0);
                                            }else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
    QBadgeView upgradeBadeView;
    QBadgeView suggestBadeView;
private Suggestion suggestion;
   private UpdateForcast forcast;
   private void openUpgradeDiaglog(UpdateForcast updateForcast, ArrayList<String> characterList, final String url) {
       if (!updateForcast.getTitle().equals("") && characterList != null) {
           if (characterList.size() != 0) {
               String message = "";
               for (int i = 0; i < characterList.size(); i++) {
                   message += (i+1)+"、"+characterList.get(i) + "\n";
               }
               if (updateForcast.getTitle().equals("新版本预测")) {
                   new AlertDialog.Builder(this).setTitle(updateForcast.getTitle()).setMessage(message)
                           .setNegativeButton("确定", null).create().show();
               }else if(updateForcast.getTitle().equals("版本升级")){
                   new AlertDialog.Builder(this).setTitle(updateForcast.getTitle()+" "+updateForcast.getVersion()).setMessage(message)
                           .setNegativeButton("取消", null).setPositiveButton("升级", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           Intent intent = new Intent();
                           intent.setAction("android.intent.action.VIEW");
                           Uri content_url = Uri.parse(url);
                           intent.setData(content_url);
                           startActivity(intent);
                       }
                   }).create().show();
               }
           }
       }
   }

    private void openAboutDialog(About about){
        View view= LayoutInflater.from(this).inflate(R.layout.layout_about,null);
        TextView aboutTv= (TextView) view.findViewById(R.id.aboutTv);
        aboutTv.setText(PackageUtils.getAppName(this)+" "+PackageUtils.getVersionName(this)+"\n"+"\n"+about.getContent());
        TextView copyRightsTv= (TextView) view.findViewById(R.id.copyrightsTv);
        copyRightsTv.setText("CopyRights @"+about.getAuthor()+" "+about.getE_mail());
        new AlertDialog.Builder(this).setView(view).setPositiveButton("确定",null).create().show();
    }


    private SharedPreferences spf2;
    private  CollectionComic historyComic;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2){
            mMyUser= (MyUser) data.getSerializableExtra("MyUser");
            afterLogin(mMyUser);
        }
        if(resultCode==3){
            if(data.getSerializableExtra("historyComic")!=null) {
                historyComic = (CollectionComic) data.getSerializableExtra("historyComic");
                ((TextView) findViewById(R.id.lastReadTv)).setText(historyComic.getComic());
            }
        }
    }
    private  String createNick(String username){
        int start=username.length()-3;
        int end=username.length();
        return "uid_"+username.substring(0,3)+username.substring(start,end);
    }

    private SharedPreferences spf;
    private void logIn(final String username, final String password){
          BmobQuery<MyUser> query=new BmobQuery<>();
        query.addWhereEqualTo("username",username);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e==null) {
                    if (list.size() == 0) {
                        BmobQuery<MyUser> query2=new BmobQuery<>();
                        query2.addWhereEqualTo("nick",username);
                       query2.findObjects(new FindListener<MyUser>() {
                           @Override
                           public void done(List<MyUser> list, BmobException e) {
                               if(e==null) {
                                   if(list.size()==0){
                                       loginBtn.setText("登录");
                                       forgotPswTv.setText("忘记密码");
                                       forgotPswLayout.setVisibility(View.GONE);
                                       confirmCodeEtforgot.setText("");
                                       pswEt.setText("");
                                       toast("该用户并未注册！",0);
                                   }else {
                                       list.get(0).setPassword(password);
                                       list.get(0).login(new SaveListener<MyUser>() {
                                           @Override
                                           public void done(MyUser myUser, BmobException e) {
                                               loginBtn.setText("登录");
                                               forgotPswTv.setText("忘记密码");
                                               forgotPswLayout.setVisibility(View.GONE);
                                               confirmCodeEtforgot.setText("");
                                               pswEt.setText("");
                                               if (e == null) {
                                                   afterLogin(myUser);
                                                   spf.edit().putString("username",username).apply();
                                                   Log.i("Bmob", " login by nick");
                                               } else {
                                                   if(e.getErrorCode()==101)
                                                       toast("用户名或密码错误",0);
                                                   e.printStackTrace();
                                               }
                                           }
                                       });
                                   }
                               }else {
                                   e.printStackTrace();
                               }
                           }
                       });
                    }else {
                        list.get(0).setPassword(password);
                        list.get(0).login(new SaveListener<MyUser>() {
                            @Override
                            public void done(MyUser myUser, BmobException e) {
                                loginBtn.setText("登录");
                                forgotPswTv.setText("忘记密码");
                                forgotPswLayout.setVisibility(View.GONE);
                                confirmCodeEtforgot.setText("");
                                pswEt.setText("");
                                if(e==null) {
                                    afterLogin(myUser);
                                    spf.edit().putString("username",username).apply();
                                    Log.i("Bmob", " login by username");
                                }else {
                                    if(e.getErrorCode()==101)
                                        toast("用户名或密码错误",0);
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void signUp(String username,String password) {
        BmobUser bmobuser=new BmobUser();
        bmobuser.setUsername(username);
        bmobuser.setPassword(password);
        bmobuser.setMobilePhoneNumber(username);
        bmobuser.setValue("nick",createNick(username));
        bmobuser.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if(e==null) {
                    toast("注册成功!",0);
                    myUser.setPassword(pswEtSigin.getText().toString());
                    myUser.login(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                               if(e==null){
                                   toast("登录成功!",0);
                                   afterSignin(myUser);
                               }else {
                                   e.printStackTrace();
                               }
                        }
                    });
                }else {
                     if(e.getErrorCode()==202){
                         toast("手机号"+userEtSignin.getText().toString()+"已经注册",0);
                     }
                    e.printStackTrace();
                }
            }
        });
    }

    private void afterLogin(MyUser myUser){
        ObjectAnimator animator2=
                ObjectAnimator.ofFloat(loginLayout, "translationY",
                        DisplayUtil.dip2px(MainActivity.this,44),DisplayUtil.getScreenHeightPx(MainActivity.this));
        animator2.setDuration(300);
        animator2.start();
        if(myUser.getSex().equals("男"))
            sexIv.setImageResource(R.mipmap.male);
        else
            sexIv.setImageResource(R.mipmap.female);
        nickNameTv.setText(myUser.getNick());
        usernameTv.setText(myUser.getNick());
        usernameTv3.setText(myUser.getNick());
        usernameTv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if(myUser.getPicture()!=null) {
            Glide.with(this).load(myUser.getPicture()).into(userIv);
            Glide.with(this).load(myUser.getPicture()).into(userIv2);
            Glide.with(this).load(myUser.getPicture()).into(userIv3);
        }
        mMyUser=myUser;
        logoutBtn.setVisibility(View.VISIBLE);
        editUserinfoTv.setVisibility(View.VISIBLE);
        userEt.clearFocus();
        userEtSignin.clearFocus();
        pswEt.clearFocus();
        pswEtSigin.clearFocus();
        confirmCodeEt.clearFocus();
        confirmCodeEtforgot.clearFocus();
    }

    private void afterSignin(MyUser myUser){
        ObjectAnimator animator=
                ObjectAnimator.ofFloat(signinLayout, "translationX",
                        0F,DisplayUtil.getScreenWidthPx(MainActivity.this));
        animator.setDuration(300);
        animator.start();
        afterLogin(myUser);
    }
   private void afterLogout(){
       logoutBtn.setVisibility(View.GONE);
       userIv.setImageResource(R.mipmap.image_default);
       userIv2.setImageResource(R.mipmap.image_default);
       userIv3.setImageResource(R.mipmap.image_default);
       usernameTv.setText("点击登录");
       usernameTv3.setText("未登录");
       usernameTv.setBackgroundResource(R.drawable.touming_btn);
       nickNameTv.setText("未登录");
     //  listView.setVisibility(View.VISIBLE);

       editUserinfoTv.setVisibility(View.GONE);
       ObjectAnimator animator=
               ObjectAnimator.ofFloat(userinfoShowLayout, "translationY",
                       0F,-DisplayUtil.getScreenHeightPx(MainActivity.this));
       animator.setDuration(300);
       animator.start();
   }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          if(isSearchBarShow){
            isSearchBarShow=false;
            ObjectAnimator animator=
                    ObjectAnimator.ofFloat(searchbarLayout, "translationY",
                            0F,-DisplayUtil.dip2px(MainActivity.this,50));
            animator.setDuration(300);
            animator.start();
            ObjectAnimator animator2=
                    ObjectAnimator.ofFloat(searchContentLayout, "translationY",
                            0F,DisplayUtil.dip2px(MainActivity.this,DisplayUtil.getScreenHeightDp(MainActivity.this)-50));
            animator2.setDuration(300);
            animator2.start();
          }else {
                exitBy2Click(); //调用双击退出函数
            }
        }
        Log.i("   ", "onKeyDown: "+keyCode);
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
             toast("再按一次退出程序",0);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            adLayout.removeAllViews();
         //   bannerview.destroy();
         //   bannerview=null;
            Glide.get(this).clearMemory();
            finish();
        }
    }
}
