package com.capton.purecomic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
//import com.baidu.appx.BDNativeAd;
import com.baidu.appx.BDNativeAd;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capton.purecomic.Adapter.ComicAdapter;
import com.capton.purecomic.DataModel.BookList.Books;
import com.capton.purecomic.DataModel.BookList.book;
import com.capton.purecomic.DataModel.BookList.result;
import com.capton.purecomic.DataModel.ChapterList.ChapterList;
import com.capton.purecomic.DataModel.ChapterList.Chapters;
import com.capton.purecomic.Utils.NetworkState;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class ComicFragment extends Fragment implements ComicAdapter.OnItemClickListener{

    private String url="";
    private String key="";
    private String api="";
    private String type="";
    private int skip=0;
    private int top=0;
    private int bottom=0;
    private int middle=0;
    private String lastDataString="";


    private ArrayList<book> bookArrayList;

    private ComicAdapter comicAdapter;
    private RecyclerView recyclerView;
    private MaterialRefreshLayout refreshLayout;

    private SharedPreferences spf;
   private OkHttpClient okHttpClient;

    private BDNativeAd nativeAd;

    public ComicFragment( ) {
    }

    @Override
    public void onStop() {
        spf.edit().putInt("lastSkip_"+type,middle).apply();
        spf.edit().putString("lastData_"+type,lastDataString).apply();
        super.onStop();
    }

  private boolean once=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        okHttpClient=new OkHttpClient();
        nativeAd = new BDNativeAd(getActivity(), getString(R.string.ad_apikey), getString(R.string.native_ad_id));
        nativeAd.loadAd();
        if(spf==null)
        spf=getContext().getSharedPreferences("history",Context.MODE_PRIVATE);
        total=spf.getInt("total_"+type,1000);

        url=getArguments().getString("url");
        key=getArguments().getString("key");
        api=getArguments().getString("api");
        type=getArguments().getString("type");

        if(spf.getInt("lastSkip_"+type,0)==0)
            middle=top=bottom=skip=getArguments().getInt("skip");
        else
            middle=top=bottom=skip=spf.getInt("lastSkip_"+type,0);

        if(!spf.getString("lastData_"+type,"").equals("")){
            lastDataString=spf.getString("lastData_"+type,"");
        }

        View view=inflater.inflate(R.layout.fragment_comic, container, false);

        if(refreshLayout==null){
            refreshLayout= (MaterialRefreshLayout) view.findViewById(R.id.swipeRefreshlayout);
            refreshLayout.setProgressColors(new int[]{getResources().getColor(R.color.colorPrimary)});
            refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    if(NetworkState.isNetWorkAvailable(getContext())) {
                        if(top!=0) {
                            top = top - 20;
                            middle = top;
                            if(top==0){
                                url= api + "type=" + type+"&key=" +key;
                            }else {
                                url=api + "type=" + type + "&skip=" + top + "&key=" + key;
                            }
                            new GetDataTask().execute(url);
                            isLoadMore = false;
                        }else {
                            Toast.makeText(getContext(),"这是最前面了",Toast.LENGTH_SHORT).show();
                            refreshLayout.finishRefreshing();
                        }
                    }else {
                        Toast.makeText(getContext(),"请检查网络",Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefreshing();
                    }
                }
                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    super.onRefreshLoadMore(materialRefreshLayout);
                        if(bottom<total) {
                            bottom = bottom + 20;
                            middle = bottom;
                            if (NetworkState.isNetWorkAvailable(getContext())) {
                                if(bottom==0){
                                    url= api + "type=" + type+"&key=" +key;
                                }else {
                                    url=api + "type=" + type + "&skip=" + bottom + "&key=" + key;
                                }
                               new GetDataTask().execute(url);
                                isLoadMore = true;
                            } else {
                                refreshLayout.finishRefreshLoadMore();
                                Toast.makeText(getContext(), "请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getContext(), "这是最后面了", Toast.LENGTH_SHORT).show();
                            refreshLayout.finishRefreshLoadMore();
                        }
                }

            });

        }

        if(recyclerView==null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            if(lastDataString.equals("")) {
                if(NetworkState.isNetWorkAvailable(getContext())) {
                    if(middle==0){
                        url= api + "type=" + type+"&key=" +key;
                    }else {
                        url=api + "type=" + type + "&skip=" + middle + "&key=" + key;
                    }
                 //   getData(url);
                    new GetDataTask().execute(url);
                }else {
                    Toast.makeText(getContext(),"网络未连接",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                 Books books= JSON.parseObject(lastDataString,Books.class);
                if(books==null){
                    Toast.makeText(getContext(),"数据解析错误，请刷新 code1",Toast.LENGTH_LONG).show();
                }else {
                    result mResult = books.getResult();
                    if(mResult==null){
                        Toast.makeText(getContext(),"数据解析错误，请刷新 code2 "+type,Toast.LENGTH_LONG).show();
                    }else {
                        bookArrayList = mResult.getBookList();
                        if(bookArrayList==null){
                            Toast.makeText(getContext(),"数据解析错误，请刷新 code3",Toast.LENGTH_LONG).show();
                        }else {
                            if (comicAdapter == null)
                                comicAdapter = new ComicAdapter(getContext(), bookArrayList);
                            comicAdapter.setOnItemClickListener(ComicFragment.this);
                            recyclerView.setAdapter(comicAdapter);
                        }
                    }
                }
            }
        }
        return view;
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, ArrayList<book> bookList, int position) {
        if(bookList!=null) {
            openBookInfoDialog(bookList.get(position));
            mBook = bookList.get(position);
        }else {
                BDNativeAd.AdInfo adInfo = ((ComicAdapter.FooterViewHolder) viewHolder).getAdInfo();
                adInfo.didClick();
                Toast.makeText(getContext(), "正在后台下载应用", Toast.LENGTH_SHORT).show();
        }
    }
    private AlertDialog alertDialog;
    private  ListView chapterListView;
    private Button readBtn;
    private boolean isChapterShow=false;
    private book mBook;

    private int tempIndex=0;
    private boolean isLoadingChapter=true;
    Handler handler=new Handler();

    private void openBookInfoDialog(final book mbook){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        final View view=LayoutInflater.from(getContext()).inflate(R.layout.layout_bookinfo_diglog,null);
        ImageView bookIv= (ImageView) view.findViewById(R.id.bookIv);
        TextView nameTv= (TextView) view.findViewById(R.id.name);
        TextView typeTv= (TextView) view.findViewById(R.id.type);
        TextView areaTv= (TextView) view.findViewById(R.id.area);
        TextView desTv= (TextView) view.findViewById(R.id.des);
        TextView finishTv= (TextView) view.findViewById(R.id.finish);
        TextView lastUpdateTv= (TextView) view.findViewById(R.id.lastUpdate);

        Glide.with(getContext()).load(mbook.getCoverImg()).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                ((RelativeLayout)((LinearLayout)((LinearLayout)view).getChildAt(0)).getChildAt(0)).getChildAt(0).destroyDrawingCache();
                return false;
            }
        }).into(bookIv);
        nameTv.setText("《"+mbook.getName()+"》");
        typeTv.setText("类型："+mbook.getType());
        areaTv.setText("区域："+mbook.getArea());
        if(mbook.getDes().length()>30) {
            desTv.setText(mbook.getDes().substring(0, 30)+"...");
        }else {
            desTv.setText(mbook.getDes());
        }
        if(mbook.isFinish())
            finishTv.setText("连载状态：已完结");
        else
            finishTv.setText("连载状态：连载中");
        String date=String.valueOf(mbook.getLastUpdate()).substring(0,4)+"/"+
                String.valueOf(mbook.getLastUpdate()).substring(4,6)+"/"+
                String.valueOf(mbook.getLastUpdate()).substring(6,8);
        lastUpdateTv.setText("上次更新："+date);

        readBtn= (Button) view.findViewById(R.id.readBtn);
        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkState.isNetWorkAvailable(getContext())) {
                    if (!isChapterShow){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (isLoadingChapter) {
                                    try {
                                        Thread.sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            String temp[] = new String[]{"正在获取.", "正在获取..","正在获取..."};
                                            readBtn.setText(temp[tempIndex]);
                                            tempIndex++;
                                            if (tempIndex == 3) {
                                                tempIndex = 0;
                                            }
                                        }
                                    });
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        readBtn.setText("开始阅读");
                                    }
                                });
                            }
                        }).start();
                        url=getString(R.string.api_chapter)+"comicName="+mbook.getName()+"&skip="+String.valueOf(0)+"&key="+getString(R.string.api_key);
                        new GetChapterDataTask().execute(url);
                    }
                    else {
                        isLoadingChapter=true;
                        Intent intent = new Intent(getContext(), ReadingActivity.class);
                        intent.putExtra("chapter", chapterArrayList.get(0));
                        intent.putExtra("result", mResult);
                        intent.putExtra("index", 0);
                        intent.putExtra("book", (Serializable) mBook);
                        intent.setAction("MainActivity");
                        startActivityForResult(intent,3);

                    }
                }else {
                    Toast.makeText(getContext(),"网络未连接",Toast.LENGTH_SHORT).show();
                }
            }
        });
        chapterListView= (ListView) view.findViewById(R.id.chapterListView);
        builder.setView(view);
        alertDialog=builder.create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                isChapterShow=false;
                isLoadingChapter=false;
            }
        });
        alertDialog.show();;
    }


    private ChapterList chapterArrayList;
    private com.capton.purecomic.DataModel.ChapterList.result mResult;
    private  ArrayList<String> chapterStringList;

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
                Toast.makeText(getContext(), "抱歉，无数据", Toast.LENGTH_SHORT).show();
            } else {
                lastDataString=s;
                if(!s.equals("")) {
                    Books books = JSON.parseObject(s, Books.class);
                    if (books != null) {
                        result mResult = books.getResult();
                        total=mResult.getTotal();
                        if (mResult != null) {
                            bookArrayList = mResult.getBookList();
                            if(middle%120==0) {
                                ArrayList<book> adBookList = new  ArrayList<book>();
                               if (nativeAd.isLoaded()) {
                                    Log.i(TAG, "onPostExecute: "+"nativeAd is loaded");
                                    ArrayList<BDNativeAd.AdInfo> adArray = nativeAd.getAdInfos();
                                    //... 自定义展示UI，其中BDNativeAd.AdInfo里的
                                    // didShow() 和 didClick()
                                    // 需要相应的UI响应逻辑中触发调用。
                                    if(adArray!=null) {
                                        if (adArray.size() != 0) {
                                            Log.i(TAG, "onPostExecute: adArray size "+adArray.size());
                                            for (int i = 0; i < adArray.size(); i++) {
                                                book adbook=new book();
                                                adbook.setViewType(book.VIEWTYPE_AD);
                                                adbook.setAdInfo(adArray.get(i));
                                                adBookList.add(adbook);
                                            }
                                            bookArrayList.addAll(0,adBookList);
                                        }
                                    }
                                }
                            }
                            if(bookArrayList!=null) {
                                if (comicAdapter == null) {
                                    comicAdapter = new ComicAdapter(getContext(), bookArrayList);
                                    comicAdapter.setOnItemClickListener(ComicFragment.this);
                                    recyclerView.setAdapter(comicAdapter);
                                } else {
                                    if (isLoadMore)
                                        comicAdapter.getBookList().addAll(bookArrayList);
                                    else
                                        comicAdapter.getBookList().addAll(0, bookArrayList);
                                    comicAdapter.notifyDataSetChanged();
                                }
                                if (refreshLayout != null) {
                                    refreshLayout.finishRefreshing();
                                    refreshLayout.finishRefreshLoadMore();
                                }
                            }
                        }
                    }
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
                Toast.makeText(getContext(), "抱歉，无数据", Toast.LENGTH_SHORT).show();
            } else {
                final Chapters chapters = JSON.parseObject(s, Chapters.class);
                if (chapters == null) {
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                } else {
                    mResult = chapters.getResult();
                    if (mResult == null) {
                        Toast.makeText(getContext(), "数据解析出错", Toast.LENGTH_SHORT).show();
                    } else {
                        chapterArrayList = mResult.getChapterList();
                        chapterStringList = new ArrayList<>();
                        for (int i = 0; i < chapterArrayList.size(); i++) {
                            chapterStringList.add(chapterArrayList.get(i).getName());
                        }
                        chapterListView.setVisibility(View.VISIBLE);
                        chapterListView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, chapterStringList));
                        chapterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), ReadingActivity.class);
                                intent.putExtra("chapter", chapterArrayList.get(position));
                                intent.putExtra("result", mResult);
                                intent.putExtra("index", position);
                                intent.putExtra("book", (Serializable) mBook);
                                intent.setAction("MainActivity");
                                startActivityForResult(intent,3);
                            }
                        });
                        isChapterShow = true;
                        isLoadingChapter=false;
                    }
                }
            }
        }
    }

    private boolean isLoadMore=true;
    private int total=100;

    @Override
    public void onDestroy() {
        for (int i = 0; i <recyclerView.getChildCount()-1; i++) {
            ((ImageView)((LinearLayout)recyclerView.getChildAt(i)).getChildAt(0)).destroyDrawingCache();
        }
        super.onDestroy();
    }


}
