package com.capton.purecomic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.baidu.appx.BDBannerAd;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capton.purecomic.Adapter.SearchComicAdapter;
import com.capton.purecomic.DataModel.BookList.Books;
import com.capton.purecomic.DataModel.BookList.book;
import com.capton.purecomic.DataModel.BookList.result;
import com.capton.purecomic.DataModel.ChapterList.ChapterList;
import com.capton.purecomic.DataModel.ChapterList.Chapters;
import com.capton.purecomic.Utils.NetworkState;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by capton on 2017/4/24.
 */

public class ResultpageFragment extends Fragment implements SearchComicAdapter.OnItemClickListener{
    private SearchComicAdapter comicAdapter;
    private RecyclerView recyclerView;
    private MaterialRefreshLayout refreshLayout;
    private ArrayList<book> bookArrayList;

    private String  key="";
    private String  api="";
    private String  name="";
    private String url="";
    private int skip=0;
    public  int  total=0;
    public  int load=0;
    private boolean isLoadMore;
    private String TAG="ResultpageFragment";

    public  TextView totalTv,loadedNumTv;
    private OkHttpClient okHttpClient;
    private RelativeLayout adLayout;
    private BDBannerAd bannerview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        okHttpClient=new OkHttpClient();
        View view=inflater.inflate(R.layout.fragment_result_page,container,false);

        key=getArguments().getString("key");
        api=getArguments().getString("api");
        name=getArguments().getString("name");
        skip=getArguments().getInt("skip");

          totalTv= (TextView) view.findViewById(R.id.totalTv);
          loadedNumTv= (TextView) view.findViewById(R.id.loadedNumTv);

            refreshLayout= (MaterialRefreshLayout) view.findViewById(R.id.resultLayout);
            refreshLayout.setProgressColors(new int[]{getResources().getColor(R.color.colorPrimary)});
            refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    if(NetworkState.isNetWorkAvailable(getContext())) {
                            skip += 20;
                            url = api + "name=" + name + "&type=&skip=" + skip + "&finish="+ "&key=" + key;
                           isLoadMore = false;
                           new GetDataTask().execute(url);
                    }else {
                        Toast.makeText(getContext(),"请检查网络",Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefreshing();
                    }
                }
                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    super.onRefreshLoadMore(materialRefreshLayout);
                    if(NetworkState.isNetWorkAvailable(getContext())) {
                        skip += 20;
                        url = api + "name=" + name + "&type=&skip=" + skip + "&finish=" + "&key=" + key;
                        isLoadMore = true;
                        new GetDataTask().execute(url);
                    }else {
                        Toast.makeText(getContext(),"请检查网络",Toast.LENGTH_SHORT).show();
                        refreshLayout.finishRefreshing();
                    }
                }

            });


             recyclerView = (RecyclerView) view.findViewById(R.id.resultRecyclerView);

             recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

             if (NetworkState.isNetWorkAvailable(getContext())) {
                 View loadingView=null;
                 loadingView = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
                 if (loadingDialog == null)
                     loadingDialog = new AlertDialog.Builder(getContext()).setView(loadingView).create();
                 loadingDialog.show();
                 url = api + "name=" + name + "&type=&skip=" + "&finish="  + "&key=" + key;
                 new GetDataTask().execute(url);
             } else {
                 Toast.makeText(getContext(), "网络未连接", Toast.LENGTH_SHORT).show();
             }
        adLayout= (RelativeLayout) view.findViewById(R.id.adLayout);
          //创建并展示广告
        bannerview = new BDBannerAd(getActivity(),getString(R.string.ad_apikey),getString(R.string.banner_ad_id));
        bannerview.setAdSize(BDBannerAd.SIZE_FULL_FLEXIBLE);

        bannerview.setAdListener(new BDBannerAd.BannerAdListener() {
            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                Log.i(TAG, "onAdvertisementDataDidLoadSuccess: ");
            }

            @Override
            public void onAdvertisementDataDidLoadFailure() {
                Log.i(TAG, "onAdvertisementDataDidLoadFailure: ");
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
            }
        }) ;
        adLayout.addView(bannerview);
        return view;
    }
    private AlertDialog loadingDialog;
    private AlertDialog alertDialog;
    private ListView chapterListView;
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
                        intent.putExtra("book",mBook);
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



    class GetDataTask extends AsyncTask<String ,Void ,String> {
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
                if(!s.equals("")) {
                    Books books = JSON.parseObject(s, Books.class);
                    if (books != null) {
                        result mResult = books.getResult();
                        if(mResult==null){
                            Log.i("response", "onResponse: "+s);
                        }else {
                            total = mResult.getTotal();
                            totalTv.setText("总计："+"\n"+total);
                            if (mResult.getTotal() == 0) {
                                refreshLayout.finishRefresh();
                                refreshLayout.finishRefreshLoadMore();
                                if(loadingDialog!=null)
                                    loadingDialog.cancel();
                                Toast.makeText(getContext(), "没有结果了", Toast.LENGTH_SHORT).show();
                            } else {
                                bookArrayList = mResult.getBookList();
                                if (bookArrayList != null) {
                                    if (comicAdapter == null) {
                                        comicAdapter = new SearchComicAdapter(getContext(), bookArrayList);
                                        comicAdapter.setOnItemClickListener(ResultpageFragment.this);
                                        load=comicAdapter.getBookList().size();
                                        recyclerView.setAdapter(comicAdapter);
                                        loadedNumTv.setText("已加载："+"\n"+load);
                                        loadingDialog.cancel();
                                    } else {
                                        load=comicAdapter.getBookList().size();
                                        if (isLoadMore) {
                                            comicAdapter.getBookList().addAll(bookArrayList);
                                            comicAdapter.notifyDataSetChanged();
                                            recyclerView.smoothScrollToPosition(load+bookArrayList.size()/6);
                                        }
                                        else {
                                            comicAdapter.getBookList().addAll(0, bookArrayList);
                                            comicAdapter.notifyDataSetChanged();
                                        }
                                        loadedNumTv.setText("已加载："+"\n"+ load);


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
                                intent.putExtra("book", mBook);
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
    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, ArrayList<book> bookList, int position) {
        openBookInfoDialog(bookList.get(position));
        mBook=bookList.get(position);
    }

    @Override
    public void onDetach() {
        adLayout.removeAllViews();
        bannerview.destroy();
        bannerview=null;
        super.onDetach();
    }
}
