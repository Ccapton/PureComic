package com.capton.purecomic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capton.purecomic.CustomView.CircleImageView;
import com.capton.purecomic.DataModel.BmobData.MyUser;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button backBtn,mEditNickBtn,mEditIntroBtn;
    private TextView titleTv,nickTv,sexTv,introTv;
    private RelativeLayout mPictureLayout;
    private CircleImageView userCiv;
    private RelativeLayout mSexLayout;
    private RelativeLayout mNickLayout;
    private RelativeLayout mIntroductionLayout;
    private LinearLayout mMenuLayout;
    private LinearLayout mEditNickLayout;
    private LinearLayout mEditIntroLayout;

    private EditText mEditNickEt,mEditIntroEt;



    private MyUser myUser;
    private boolean isEditting=false;
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spf=getSharedPreferences("UserInfo",MODE_PRIVATE);
        myUser= BmobUser.getCurrentUser(MyUser.class);
        setContentView(R.layout.layout_edit_userinfo);

        initView();
        loadUserInfo();
    }

    private void initView() {
        backBtn= (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        mEditNickBtn= (Button) findViewById(R.id.editNickBtn);
        mEditNickBtn.setOnClickListener(this);
        mEditIntroBtn= (Button) findViewById(R.id.editIntroBtn);
        mEditIntroBtn.setOnClickListener(this);

        titleTv= (TextView) findViewById(R.id.title);
        titleTv.setText("编辑资料");
        nickTv= (TextView) findViewById(R.id.nickTv);
        sexTv= (TextView) findViewById(R.id.sexTv);
        introTv= (TextView) findViewById(R.id.indroductionTv);


        userCiv= (CircleImageView) findViewById(R.id.userCiv);
        Glide.with(this).load(myUser.getPicture()).into(userCiv);

        mPictureLayout= (RelativeLayout) findViewById(R.id.pictureLayout);
        mPictureLayout.setOnClickListener(this);
        mSexLayout= (RelativeLayout) findViewById(R.id.sexLayout);
        mSexLayout.setOnClickListener(this);
        mNickLayout= (RelativeLayout) findViewById(R.id.nickLayout);
        mNickLayout.setOnClickListener(this);
        mIntroductionLayout= (RelativeLayout) findViewById(R.id.introductionLayout);
        mIntroductionLayout.setOnClickListener(this);

        mMenuLayout= (LinearLayout) findViewById(R.id.menuLayout);
        mEditNickLayout= (LinearLayout) findViewById(R.id.editNickLayout);
        mEditIntroLayout= (LinearLayout) findViewById(R.id.editIntroLayout);
        mEditIntroLayout.setOnClickListener(this);

        mEditNickEt= (EditText) findViewById(R.id.editNickEt);
        mEditIntroEt= (EditText) findViewById(R.id.editIntroEt);

    }
    private void loadUserInfo() {

        if (myUser != null) {
                nickTv.setText(myUser.getNick());
                Glide.with(this).load(myUser.getPicture()).into(userCiv);
            if(myUser.getSex().equals("male"))
               sexTv.setText("男");
            else
                sexTv.setText("女");
            ((TextView)findViewById(R.id.phoneTv)).setText(myUser.getUsername());
            introTv.setText(myUser.getIntroduction());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent();
            intent.putExtra("MyUser",myUser);
            setResult(2,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:

                if(!isEditting) {
                    Intent intent=new Intent();
                    intent.putExtra("MyUser",myUser);
                    setResult(2,intent);
                    finish();
                }
                else {
                    titleTv.setText("编辑资料");
                    mMenuLayout.setVisibility(View.VISIBLE);
                    mEditNickLayout.setVisibility(View.GONE);
                    isEditting=false;
                }

                break;
            case R.id.pictureLayout:
                openPicturePicker();
                break;
            case R.id.nickLayout:
                isEditting=true;
                mMenuLayout.setVisibility(View.GONE);
                mEditIntroLayout.setVisibility(View.GONE);
                mEditNickLayout.setVisibility(View.VISIBLE);
                mEditNickEt.setText(myUser.getNick());
                titleTv.setText("更改昵称");
                break;
            case R.id.sexLayout:
                new AlertDialog.Builder(this).setTitle("选择性别").setMessage("安能辨别阁下是雄雌？").setPositiveButton("女", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sexTv.setText("女");
                        myUser.setSex("女");
                        myUser.update(myUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    toast("性别修改成功",0);
                                }else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).setNegativeButton("男", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sexTv.setText("男");
                        myUser.setSex("男");
                        myUser.update(myUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    toast("性别修改成功",0);
                                }else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).show();
                break;
            case R.id.introductionLayout:

                isEditting=true;
                mMenuLayout.setVisibility(View.GONE);
                mEditNickLayout.setVisibility(View.GONE);
                mEditIntroLayout.setVisibility(View.VISIBLE);
           //     mEditIntroEt.setText(myUser.getIntroduction());
                titleTv.setText("更改签名");
                break;
            case R.id.editNickBtn:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                final String nick=mEditNickEt.getText().toString();
                if(nick.equals("")){
                    Toast.makeText(EditUserInfoActivity.this,"昵称不能为空",Toast.LENGTH_LONG).show();
                }else {
                    BmobQuery<MyUser> query=new BmobQuery<>();
                    query.addWhereEqualTo("nick",nick);
                    query.findObjects(new FindListener<MyUser>() {
                        @Override
                        public void done(List<MyUser> list, BmobException e) {
                             if(e==null){
                                 if(list.size()==0){
                                     myUser.setNick(nick);
                                     myUser.update(myUser.getObjectId(), new UpdateListener() {
                                         @Override
                                         public void done(BmobException e) {
                                             if(e==null){
                                                 mMenuLayout.setVisibility(View.VISIBLE);
                                                 mEditNickLayout.setVisibility(View.GONE);
                                                 titleTv.setText("编辑资料");
                                                 nickTv.setText(nick);
                                                 Toast.makeText(EditUserInfoActivity.this,"昵称更改成功",Toast.LENGTH_LONG).show();
                                             }else {
                                                 e.printStackTrace();
                                             }
                                         }
                                     });
                                 }else {
                                     toast("有人取了同样的名字-_-!",0);
                                 }
                             }else {
                                 e.printStackTrace();
                             }
                        }
                    });


                }
                break;
            case R.id.editIntroBtn:
                InputMethodManager imm2= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm2 != null) {
                    imm2.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                final String introduction=mEditIntroEt.getText().toString();
                if(introduction.equals("")){
                    Toast.makeText(EditUserInfoActivity.this,"签名不能为空",Toast.LENGTH_LONG).show();
                }else {
                    if(introduction.length()>135){
                        toast("字数超过限制了",0);
                    }else {
                        myUser.setIntroduction(introduction);
                        myUser.update(myUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    mMenuLayout.setVisibility(View.VISIBLE);
                                    mEditIntroLayout.setVisibility(View.GONE);
                                    titleTv.setText("编辑资料");
                                    introTv.setText(introduction);
                                    Toast.makeText(EditUserInfoActivity.this, "签名更改成功", Toast.LENGTH_LONG).show();
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                break;
        }
    }

    private String picture="";

    private int REQUEST_CODE=1;
    private void openPicturePicker(){
        // 自定义图片加载器
        ImageLoader loader = new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
                Glide.with(context).load(path).into(imageView);
            }
        };
        // 自由配置选项
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // “确定”按钮背景色
                .btnBgColor(getResources().getColor(R.color.colorPrimary))
                // “确定”按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(getResources().getColor(R.color.colorPrimary))
                // 返回图标ResId
                .backResId(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material)
                // 标题
                .title("选择图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(getResources().getColor(R.color.colorPrimary))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(true)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(1)
                .build();
        // 跳转到图片选择器
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            for (String path : pathList) {
                Glide.with(this).load(path).into(userCiv);
                final BmobFile file=new BmobFile(new File(path));
                file.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                     if(e==null){
                        picture=file.getFileUrl();
                         myUser.setPicture(picture);
                         myUser.update(myUser.getObjectId(), new UpdateListener() {
                             @Override
                             public void done(BmobException e) {
                                 if(e==null){

                                 }else {
                                     e.printStackTrace();
                                 }
                             }
                         });
                     }else {
                         e.printStackTrace();
                     }
                    }
                });
            }
        }
    }
    private void toast(String content,int type){
        if(type==1)
            Toast.makeText(this,content,Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }
}
