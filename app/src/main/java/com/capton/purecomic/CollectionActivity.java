package com.capton.purecomic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.capton.purecomic.Adapter.CollectionAdapter;
import com.capton.purecomic.DataBase.CollectionHelper;
import com.capton.purecomic.DataModel.BmobData.BmobCollection;
import com.capton.purecomic.DataModel.BmobData.BmobUtil;
import com.capton.purecomic.DataModel.BmobData.MyUser;
import com.capton.purecomic.DataModel.CollectionComic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;


public class CollectionActivity extends AppCompatActivity implements BmobUtil.OnBmobDataDoneListener{

    private ListView listView;
    private CollectionAdapter adapter;
     private CollectionHelper helper;
    private ArrayList<CollectionComic> mCollectionComicList;
    private Button updateDataBtn;
   private AlertDialog dialog,loadingDialog;
    private View loadingView;
   private ArrayList<BmobObject> bmobCollections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        helper=new CollectionHelper(this);

        listView= (ListView) findViewById(R.id.collectionListView);
        adapter=new CollectionAdapter(this,getCollectionComic());
        listView.setAdapter(adapter);

        Button exitBtn= (Button) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CollectionActivity.this,MainActivity.class);
                intent.putExtra("historyComic",historyComic);
                setResult(3,intent);
                finish();
            }
        });


        updateDataBtn= (Button) findViewById(R.id.updateDataBtn);
        updateDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BmobUser.getCurrentUser()==null){
                    Toast.makeText(CollectionActivity.this,"请登录后重试",Toast.LENGTH_SHORT).show();
                }else {
                    dialog = new AlertDialog.Builder(CollectionActivity.this)
                            .setTitle("同步").setMessage("上传、获取收藏？").setNegativeButton("上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (loadingView == null)
                                        loadingView = LayoutInflater.from(CollectionActivity.this).inflate(R.layout.layout_loading, null);
                                    if (loadingDialog == null)
                                        loadingDialog = new AlertDialog.Builder(CollectionActivity.this).setView(loadingView).create();
                                    loadingDialog.show();
                                    isDownloadData = false;
                                    BmobUtil.setOnBmobDataDoneListener(CollectionActivity.this);
                                    BmobUtil.queryByMyUser();
                                }
                            }).setPositiveButton("获取", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (loadingView == null)
                                        loadingView = LayoutInflater.from(CollectionActivity.this).inflate(R.layout.layout_loading, null);
                                    if (loadingDialog == null)
                                        loadingDialog = new AlertDialog.Builder(CollectionActivity.this).setView(loadingView).create();
                                    loadingDialog.show();
                                    isDownloadData = true;
                                    BmobUtil.setOnBmobDataDoneListener(CollectionActivity.this);
                                    BmobUtil.queryByMyUser();
                                }
                            }).create();
                    dialog.show();
                }
            }
        });
    }

    private ArrayList<CollectionComic> getCollectionComic(){
        mCollectionComicList=new ArrayList<>();

        List<Map> maps=  helper.queryListMap("select * from Collection",new String[0]);

        List<Map> tempMaps=new ArrayList<>();

        for (int i = maps.size()-1; i >=0; i--) {
            tempMaps.add(maps.get(i));
        }
            if(maps.size()!=0) {
                for (int i = 0; i < maps.size(); i++) {
                    String comic = (String) tempMaps.get(i).get("comic");
                    String picture = (String) tempMaps.get(i).get("picture");
                    String chapterResult = (String) tempMaps.get(i).get("chapterResult");
                    String imageResult = (String) tempMaps.get(i).get("imageResult");
                    int chapterIndex = (int) tempMaps.get(i).get("chapterIndex");
                    int pageIndex = (int) tempMaps.get(i).get("pageIndex");
                    String book =  (String) tempMaps.get(i).get("book");
                    CollectionComic collectionComic = new CollectionComic();
                    collectionComic.setBook(book);
                    collectionComic.setPicture(picture);
                    collectionComic.setComic(comic);
                    collectionComic.setChapterResult(chapterResult);
                    collectionComic.setImageResult(imageResult);
                    collectionComic.setChapterIndex(chapterIndex);
                    collectionComic.setPageIndex(pageIndex);
                    mCollectionComicList.add(collectionComic);
                }
            }
        return  mCollectionComicList;
    }
    private boolean saveCollectionComic(ArrayList<CollectionComic> collectionComicList){
        for (int i = 0; i <collectionComicList.size(); i++) {
            helper.insert("Collection",
                    new String[]{"comic","picture","chapterResult","imageResult","chapterIndex","pageIndex","book"},
                   new Object[]{collectionComicList.get(i).getComic(),
                              collectionComicList.get(i).getPicture(),
                              collectionComicList.get(i).getChapterResult(),
                           collectionComicList.get(i).getImageResult(),
                           collectionComicList.get(i).getChapterIndex(),
                           collectionComicList.get(i).getPageIndex(),
                           collectionComicList.get(i).getBook()});
        }
        loadingDialog.cancel();
        Toast.makeText(this,"操作成功",Toast.LENGTH_SHORT).show();
        return true;
    }
    private void clearCollectionComic(){
        helper.delete("Collection",new String[0],new String[0]);
    }

    private  int position;
    private CollectionComic historyComic;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if(resultCode==0){
                 historyComic= (CollectionComic) data.getSerializableExtra("CollectionComic");
                 position=data.getIntExtra("position",0);
                Log.i("collectionActivity", "position "+position);
                Log.i("collectionActivity", "comic "+historyComic.getComic());

                adapter.getCollectionComicList().set(position,historyComic);
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBmobDataDeleted(List<BatchResult> list) {
        bmobCollections=new ArrayList<BmobObject>();
        for (int i = 0; i <mCollectionComicList.size(); i++) {
            BmobCollection bmobCollection=new BmobCollection();
            bmobCollection.setCollectionComic(mCollectionComicList.get(i));
            bmobCollection.setMyUser(BmobUser.getCurrentUser(MyUser.class));
            bmobCollections.add(bmobCollection);
        }
        if(bmobCollections.size()==0){
            Toast.makeText(this,"已清空云端数据",Toast.LENGTH_SHORT).show();
        }else {
            BmobUtil.add(bmobCollections);
        }
    }


    private boolean isDownloadData=true;
    @Override
    public void onBmobDataAdded() {
        loadingDialog.cancel();
        Toast.makeText(this,"操作成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBmobDataQuery(List<BmobCollection> bmobObjectList) {
            List<BmobObject> tempBmobObjects = new ArrayList<>();
            for (int i = 0; i < bmobObjectList.size(); i++) {
                tempBmobObjects.add(bmobObjectList.get(i));
            }
            if (!isDownloadData) {
                Log.i("    ", "isDownloadData: " + isDownloadData);
                if (bmobObjectList.size() == 0) {
                    bmobCollections = new ArrayList<BmobObject>();
                    for (int i = 0; i < mCollectionComicList.size(); i++) {
                        BmobCollection bmobCollection = new BmobCollection();
                        bmobCollection.setCollectionComic(mCollectionComicList.get(i));
                        bmobCollection.setMyUser(BmobUser.getCurrentUser(MyUser.class));
                        bmobCollections.add(bmobCollection);
                    }
                    if(bmobCollections.size()==0){
                        loadingDialog.cancel();
                        Toast.makeText(this, "请先收藏一下漫画吧", Toast.LENGTH_SHORT).show();
                    }else {
                        BmobUtil.add(bmobCollections);
                    }
                } else {
                    BmobUtil.delete(tempBmobObjects);
                }
            } else {
                if (bmobObjectList.size() == 0) {
                    loadingDialog.cancel();
                    Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("    ", "isDownloadData: " + isDownloadData);
                    List<CollectionComic> tempCollectionComics = new ArrayList<>();
                    for (int i = 0; i < bmobObjectList.size(); i++) {
                        tempCollectionComics.add(bmobObjectList.get(i).getCollectionComic());
                    }
                    mCollectionComicList = (ArrayList<CollectionComic>) tempCollectionComics;
                    adapter.setCollectionComicList(mCollectionComicList);
                    adapter.notifyDataSetChanged();

                    clearCollectionComic();
                    List<CollectionComic> tempCollectionComics2 = new ArrayList<>();
                    for (int i = bmobObjectList.size() - 1; i >= 0; i--) {
                        tempCollectionComics2.add(bmobObjectList.get(i).getCollectionComic());
                    }
                    mCollectionComicList = (ArrayList<CollectionComic>) tempCollectionComics2;
                    saveCollectionComic(mCollectionComicList);
                }
            }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent();
            intent.putExtra("historyComic",historyComic);
            setResult(3,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}
