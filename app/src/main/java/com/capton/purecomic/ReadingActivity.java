package com.capton.purecomic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.capton.purecomic.Adapter.*;
import com.capton.purecomic.DataBase.CollectionHelper;
import com.capton.purecomic.DataModel.BmobData.BmobUtil;
import com.capton.purecomic.DataModel.BmobData.Comic;
import com.capton.purecomic.DataModel.BookList.book;
import com.capton.purecomic.DataModel.ChapterList.Chapter;
import com.capton.purecomic.DataModel.ChapterList.ChapterList;
import com.capton.purecomic.DataModel.ChapterList.Chapters;
import com.capton.purecomic.DataModel.ChapterList.result;
import com.capton.purecomic.DataModel.CollectionComic;
import com.capton.purecomic.DataModel.ImageList.chapterContent;
import com.capton.purecomic.DataModel.ImageList.image;
import com.capton.purecomic.Utils.DisplayUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;


public class ReadingActivity extends AppCompatActivity implements View.OnClickListener{

    private Chapter mChapter;
    private result mResult;
    private book mBook;
    private String bookString;
    private int index;
    private ViewPager bookVp;
    private ReadingFragmentAdapter pagerAdapter;
    private LinearLayout actionBar,tag,lastChapterLayout,nextChapterlayout,chapterListLayout,hideActionBarLayout,collectLayout;
    private ImageView collectIv;
    private RelativeLayout comicInfoBar;
    private TextView chapterTv;
    private TextView comicTv;
    private TextView collectTv;

    private String url;
    private String api_chapterContent="";
    private String name="";
    private String key="";
    private  int id=0;
    private int  skip=0;
    private  int page=0;
    private int position=0;
    private SharedPreferences   spf2,spf3;
    private OkHttpClient okHttpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpClient=new OkHttpClient();
        spf2=getSharedPreferences("ReadHistory",MODE_PRIVATE);
        spf3=getSharedPreferences("ScreenToggle",MODE_PRIVATE);

        api_chapterContent=getString(R.string.api_chapterContent);
        key=getString(R.string.api_key);

        mChapter= (Chapter) getIntent().getSerializableExtra("chapter");
        mResult= (result) getIntent().getSerializableExtra("result");
        mBook= (book) getIntent().getSerializableExtra("book");

        bookString= JSON.toJSONString(mBook);
        chapterArrayList=mResult.getChapterList();
        index=getIntent().getIntExtra("index",0);
        page=getIntent().getIntExtra("page",0);
        position=getIntent().getIntExtra("position",0);

        name=mResult.getComicName();
         id=mChapter.getId();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if(!spf3.getBoolean("isSensorOn",false)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_reading);

         initView();
        url=api_chapterContent+"comicName="+name+"&id="+String.valueOf(id)+"&key="+key;
        new GetDataTask().execute(url);
        checkIsComicCollected();

        Comic comic=new Comic();
        comic.setPicture(mBook.getCoverImg());
        comic.setComicName(mBook.getName());
        comic.setCollectTimes(1);
        comic.setReadTimes(1);
        BmobUtil.add(this,comic,BmobUtil.ACTION_READ);
    }
    class GetDataTask extends AsyncTask<String ,Void ,String>{
        @Override
        protected String doInBackground(String... params) {
            okhttp3.Request request= new okhttp3.Request.Builder().url(params[0]).get().build();
            try {
                okhttp3.Response response=okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    return  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "erro";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("erro")) {
                Toast.makeText(ReadingActivity.this, "抱歉，无数据", Toast.LENGTH_SHORT).show();
            } else {
                chapterContent content = JSON.parseObject(s, chapterContent.class);
                imageResult = content.getResult();

                imageArrayList = imageResult.getImageList();
                if (imageArrayList == null) {
                    Toast.makeText(ReadingActivity.this, "抱歉，无数据", Toast.LENGTH_SHORT).show();
                } else {
                    fragmentArrayList = new ArrayList<Fragment>();
                    for (int i = 0; i < imageArrayList.size(); i++) {
                        ReadingFragment readingFragment=new ReadingFragment();
                        readingFragment.setmImage(imageArrayList.get(i));
                        fragmentArrayList.add(readingFragment);
                    }
                    if (pagerAdapter == null) {
                        pagerAdapter =new ReadingFragmentAdapter(getSupportFragmentManager(),fragmentArrayList);
                        bookVp.setAdapter(pagerAdapter);
                    } else {
                        pagerAdapter.setFragmentArrayList(fragmentArrayList);
                        pagerAdapter.notifyDataSetChanged();
                    }
                    mChapter =  mResult.getChapterList().get(index);
                    chapterTv.setText(mChapter.getName() + "  " + 1 + "/" + imageArrayList.size());
                    bookVp.setCurrentItem(page);
                    page=0;
                }
            }
        }
    }
    class GetChapterDataTask extends AsyncTask<String ,Void ,String> {

        @Override
        protected String doInBackground(String... params) {
            okhttp3.Request request= new okhttp3.Request.Builder().url(params[0]).get().build();
            try {
                okhttp3.Response response=okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    return  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "erro";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("erro")) {
                Toast.makeText(ReadingActivity.this, "抱歉，无数据", Toast.LENGTH_SHORT).show();
            } else {
                final Chapters chapters= JSON.parseObject(s,Chapters.class);
                mResult=chapters.getResult();
                chapterArrayList=mResult.getChapterList();
                chapterStringList=new ArrayList<>();
                chapterIdList=new ArrayList<>();
                for (int i = 0; i <chapterArrayList.size(); i++) {
                    chapterStringList.add(chapterArrayList.get(i).getName());
                    chapterIdList.add(chapterArrayList.get(i).getId());
                }
                String tempUrl=api_chapterContent+"comicName="+name+"&id="+String.valueOf(chapterIdList.get(0))+"&key="+key;
              //  getData(tempUrl);
                new GetDataTask().execute(tempUrl);
            }
        }
    }

    private void initView() {
        bookVp= (ViewPager) findViewById(R.id.bookVp);
        bookVp.setOffscreenPageLimit(2);
        bookVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPageIndex=position;
                chapterTv.setText(mChapter.getName() + "  " + (position + 1) + "/" + imageArrayList.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        actionBar= (LinearLayout) findViewById(R.id.actionBar);
        chapterTv= (TextView) findViewById(R.id.chapter);
        comicTv= (TextView) findViewById(R.id.comic);
        comicTv.setText("《"+name+"》");
        comicInfoBar= (RelativeLayout) findViewById(R.id.comicInfo);
        tag= (LinearLayout) findViewById(R.id.tag);
        tag.setVisibility(View.INVISIBLE);
        tag.setOnClickListener(this);
        lastChapterLayout= (LinearLayout) findViewById(R.id.lastChapter);
        lastChapterLayout.setOnClickListener(this);
        nextChapterlayout= (LinearLayout) findViewById(R.id.nextChapter);
        nextChapterlayout.setOnClickListener(this);
        chapterListLayout= (LinearLayout) findViewById(R.id.chapterList);
        chapterListLayout.setOnClickListener(this);
        hideActionBarLayout= (LinearLayout) findViewById(R.id.hideLayout);
        hideActionBarLayout.setOnClickListener(this);
        collectLayout= (LinearLayout) findViewById(R.id.collect);
        collectIv= (ImageView) collectLayout.getChildAt(0);
        collectTv= (TextView) collectLayout.getChildAt(1);
        collectLayout.setOnClickListener(this);
    }


    private ArrayList<image> imageArrayList;

   // private ArrayList<View> viewList;
    private ArrayList<Fragment> fragmentArrayList;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lastChapter:
                    index--;
                    if(index>=0) {
                        mChapter = mResult.getChapterList().get(index);
                        id = mChapter.getId();
                        url=api_chapterContent+"comicName="+mResult.getComicName()+"&id="+String.valueOf(id)+"&key="+key;
                        new GetDataTask().execute(url);
                    }
                    else {
                        if(mResult.getTotal()>mResult.getLimit()){
                            if(skip>0){
                                skip-=20;
                                index=mResult.getChapterList().size() - 1;

                                url=getString(R.string.api_chapter)+"comicName="+name+"&skip="+String.valueOf(skip)+"&key="+key;
                                new GetChapterDataTask().execute(url);


                            }else if(skip==0){
                                Toast.makeText(ReadingActivity.this, "前面没有了 -_-!", Toast.LENGTH_LONG).show();
                                index = 0;
                            }
                        }else {
                            Toast.makeText(ReadingActivity.this, "前面没有了 -_-!", Toast.LENGTH_LONG).show();
                            index = 0;
                        }
                    }
                break;
            case R.id.nextChapter:
                    index++;
                    if(index<mResult.getChapterList().size()) {
                        mChapter = mResult.getChapterList().get(index);
                        id = mChapter.getId();
                        url=api_chapterContent+"comicName="+mResult.getComicName()+"&id="+String.valueOf(id)+"&key="+key;
                        new GetDataTask().execute(url);
                    }
                    else {
                        if(mResult.getTotal()>mResult.getLimit()){
                            skip+=20;
                            if(skip>mResult.getTotal()){
                                Toast.makeText(ReadingActivity.this, "后面没有了 -_-!", Toast.LENGTH_LONG).show();
                                index = mResult.getChapterList().size() - 1;
                            }else {
                                index = 0;
                                url=getString(R.string.api_chapter)+"comicName="+name+"&skip="+String.valueOf(skip)+"&key="+key;
                                new GetChapterDataTask().execute(url);
                            }
                        }else {
                            Toast.makeText(ReadingActivity.this, "后面没有了 -_-!", Toast.LENGTH_LONG).show();
                            index = mResult.getChapterList().size() - 1;
                        }
                    }
                break;
            case R.id.chapterList:
                openChapterListDialog((ChapterList) chapterArrayList);
                break;
            case R.id.hideLayout:
                tag.setVisibility(View.VISIBLE);
                hideMenuAnimation(30);
                hideComicInfoAnimation(36);
                break;
            case R.id.tag:
                tag.setVisibility(View.INVISIBLE);
                showMenuAnimation(30);
                showComicInfoAnimation(36);
                break;
            case R.id.collect:
                collectComic();
                break;
        }
    }

    private void hideMenuAnimation(int height_dp) {
        ObjectAnimator animator=ObjectAnimator.ofFloat(actionBar,"translationY",0f,(float)DisplayUtil.dip2px(this,height_dp));
        animator.setDuration(300);
        animator.start();
    }
    private void showMenuAnimation(int height_dp) {
        ObjectAnimator animator=ObjectAnimator.ofFloat(actionBar,"translationY",(float)DisplayUtil.dip2px(this,height_dp),0f);
        animator.setDuration(300);
        animator.start();
    }
    private void hideComicInfoAnimation(int height_dp) {
        ObjectAnimator animator=ObjectAnimator.ofFloat(comicInfoBar,"translationY",0f,-(float)DisplayUtil.dip2px(this,height_dp));
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                comicInfoBar.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
            }
        });
        animator.start();

    }
    private void showComicInfoAnimation(int height_dp) {
        ObjectAnimator animator=ObjectAnimator.ofFloat(comicInfoBar,"translationY",-(float)DisplayUtil.dip2px(this,height_dp),0f);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                comicInfoBar.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        animator.start();
    }
    private AlertDialog alertDialog;
    private ListView listView;
    private ChapterListAdapter adapter;
    private Button closeDialogBtn;
    private TextView comicNameTv;
    private void openChapterListDialog(ChapterList mChapterList){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=LayoutInflater.from(this).inflate(R.layout.layout_chapter_list,null);
        listView= (ListView) view.findViewById(R.id.chapterListView);
        closeDialogBtn= (Button) view.findViewById(R.id.closeDialogBtn);
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        comicNameTv= (TextView) view.findViewById(R.id.comicName);
        comicNameTv.setText("《"+name+"》");
        adapter=new ChapterListAdapter(this,mChapterList,index);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index=position;
                alertDialog.cancel();
                url=api_chapterContent+"comicName="+mResult.getComicName()+"&id="+adapter.getChapterList().get(position).getId()+""+"&key="+key;
                new GetDataTask().execute(url);
            }
        });

        alertDialog=builder.setView(view).create();
        alertDialog.show();


    }

    private ArrayList<String> chapterStringList;
    private ArrayList<Integer> chapterIdList;
    private ArrayList<Chapter> chapterArrayList;
    private com.capton.purecomic.DataModel.ImageList.result imageResult;
    private int currentPageIndex;

    @Override
    protected void onStop() {
        saveHistory();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if("CollectionActivity".equals(getIntent().getAction())){
         if(keyCode==KeyEvent.KEYCODE_BACK){

             Intent intent=new Intent();

             String chapterResultString=JSON.toJSONString(mResult);
             String imageResultString=JSON.toJSONString(imageResult);

             CollectionComic collectionComic=new CollectionComic();
             collectionComic.setPageIndex(currentPageIndex);
             collectionComic.setChapterIndex(index);
             collectionComic.setChapterResult(chapterResultString);
             collectionComic.setImageResult(imageResultString);
             collectionComic.setBook(bookString);
             collectionComic.setComic(name);
             collectionComic.setPicture(mBook.getCoverImg());

             intent.putExtra("CollectionComic",collectionComic);
             intent.putExtra("position",position);
             Log.i("position", "onKeyDown: position "+position);
             setResult(0,intent);
             finish();
         }
        }else if("MainActivity".equals(getIntent().getAction())){
            Intent intent=new Intent();
            collectionComic=new CollectionComic();
            collectionComic.setChapterIndex(index);
            collectionComic.setBook(bookString);
            collectionComic.setPageIndex(currentPageIndex);
            collectionComic.setPicture(mBook.getCoverImg());
            collectionComic.setComic(mBook.getName());
            String chapterResultString=JSON.toJSONString(mResult);
            String imageResultString=JSON.toJSONString(imageResult);
            collectionComic.setChapterResult(chapterResultString);
            collectionComic.setImageResult(imageResultString);
            intent.putExtra("historyComic",collectionComic);
            setResult(3,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void checkIsComicCollected(){
        CollectionHelper helper=new CollectionHelper(this);
        HashMap<String,Object> map= (HashMap<String, Object>) helper.queryItemMap("select * from Collection where comic=?",new String[]{name});
        if(map!=null) {
            if(name.equals(map.get("comic"))) {
                collectIv.setImageResource(R.mipmap.liked);
                collectTv.setText("已收藏");
            }
        }
    }

    private void collectComic(){
        String chapterResultString=JSON.toJSONString(mResult);
        String imageResultString=JSON.toJSONString(imageResult);
        String bookString=JSON.toJSONString(mBook);
        CollectionHelper helper=new CollectionHelper(this);

        HashMap<String,Object> map= (HashMap<String, Object>) helper.queryItemMap("select * from Collection where comic=?",new String[]{name});

        if(map!=null) {
            if(!name.equals(map.get("comic"))) {
                System.out.println("insert");
                helper.insert("Collection",
                        new String[]{"comic","picture","book","chapterResult", "imageResult", "chapterIndex", "pageIndex"},
                        new Object[]{name, mBook.getCoverImg(),bookString, chapterResultString, imageResultString, index, currentPageIndex});
                collectIv.setImageResource(R.mipmap.liked);
                collectTv.setText("已收藏");
                Comic comic=new Comic();
                comic.setPicture(mBook.getCoverImg());
                comic.setComicName(mBook.getName());
                comic.setCollectTimes(1);
                BmobUtil.add(this,comic,BmobUtil.ACTION_COLLECT);
            }else {
                System.out.println("delete");
                helper.delete("Collection",new String[]{"comic"},new String[]{name});
                collectIv.setImageResource(R.mipmap.like);
                collectTv.setText("收藏");
                Comic comic=new Comic();
                comic.setPicture(mBook.getCoverImg());
                comic.setComicName(mBook.getName());
                comic.setCollectTimes(1);
                BmobUtil.add(this,comic,BmobUtil.ACTION_COLLECT_CANCLE);
            }
        }

    }

    private CollectionComic collectionComic;
    private void saveHistory(){
        CollectionHelper helper=new CollectionHelper(this);
       String chapterResultString=JSON.toJSONString(mResult);
       String imageResultString=JSON.toJSONString(imageResult);
       helper.update("Collection",
               new String[]{"chapterResult", "imageResult", "chapterIndex", "pageIndex"},
               new Object[]{chapterResultString, imageResultString, index, currentPageIndex},
               new String[]{"comic"},new String[]{name} );

        collectionComic=new CollectionComic();
        collectionComic.setChapterIndex(index);
        collectionComic.setBook(bookString);
        collectionComic.setPageIndex(currentPageIndex);
        collectionComic.setPicture(mBook.getCoverImg());
        collectionComic.setComic(mBook.getName());
        collectionComic.setChapterResult(chapterResultString);
        collectionComic.setImageResult(imageResultString);
        spf2.edit().putString("lastReadComic",JSON.toJSONString(collectionComic)).apply();
    }

    @Override
    protected void onDestroy() {

        for (int i = 0; i < bookVp.getChildCount(); i++) {
            Glide.clear(bookVp.getChildAt(i));
        }
        Glide.get(this).clearMemory();

        mChapter=null;mResult=null;
        mBook=null;bookString=null;bookVp=null;pagerAdapter=null;
        actionBar=null;tag=null;lastChapterLayout=null;nextChapterlayout=null;
        chapterListLayout=null;hideActionBarLayout=null;collectLayout=null;
        collectIv=null;comicInfoBar=null;chapterTv=null;comicTv=null;collectTv=null;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
