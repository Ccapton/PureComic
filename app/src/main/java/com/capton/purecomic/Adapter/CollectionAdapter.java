package com.capton.purecomic.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.capton.purecomic.CollectionActivity;
import com.capton.purecomic.DataBase.CollectionHelper;
import com.capton.purecomic.DataModel.BookList.book;
import com.capton.purecomic.DataModel.ChapterList.Chapter;
import com.capton.purecomic.DataModel.ChapterList.ChapterList;
import com.capton.purecomic.DataModel.ChapterList.result;
import com.capton.purecomic.DataModel.CollectionComic;
import com.capton.purecomic.R;
import com.capton.purecomic.ReadingActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import chen.capton.optionlayout.OptionLayout;

/**
 * Created by capton on 2017/4/19.
 */

public class CollectionAdapter extends BaseAdapter implements OptionLayout.OptionClickListener{

    private ArrayList<CollectionComic> collectionComicList;
    private LayoutInflater inflater;
    private Context context;

    public CollectionAdapter(Context context, ArrayList<CollectionComic> collectionComicList) {
        this.collectionComicList = collectionComicList;
        this.inflater=LayoutInflater.from(context);
        this.context = context;
    }

    public ArrayList<CollectionComic> getCollectionComicList() {
        return collectionComicList;
    }

    public void setCollectionComicList(ArrayList<CollectionComic> collectionComicList) {
        this.collectionComicList = collectionComicList;
    }

    @Override
    public int getCount() {
        return collectionComicList.size();
    }

    @Override
    public Object getItem(int position) {
        return collectionComicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    HashMap<Integer,View> converViewMap=new HashMap<>();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(converViewMap.get(position)==null){
            convertView=inflater.inflate(R.layout.option_layout,parent,false);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            converViewMap.put(position,convertView);
        }else {
            convertView=converViewMap.get(position);
            viewHolder= (ViewHolder) convertView.getTag();
        }

        ((OptionLayout)convertView).setOptionClickListener(this); //强转为我们的OptionLaout,为适配器回调事件，例如点击事件，以实现我们的效果
        ((OptionLayout)convertView).setPosition(position);

        Glide.with(context).load(collectionComicList.get(position).getPicture()).override(60,150).placeholder(R.color.colorGrey).into(viewHolder.imageView);
        viewHolder.comicTv.setText(collectionComicList.get(position).getComic());

        String chapterResultString=collectionComicList.get(position).getChapterResult();
        String chapterImageString=collectionComicList.get(position).getImageResult();

        result mResult= JSON.parseObject(chapterResultString,result.class);
        ChapterList mChapterList=mResult.getChapterList();
        Chapter mChapter=mChapterList.get(collectionComicList.get(position).getChapterIndex());

        viewHolder.lastReadtv.setText("上次阅读："+mChapter.getName()+"第"+(collectionComicList.get(position).getPageIndex()+1)+"页");

        return convertView;
    }

    @Override
    public void leftOptionClick(View view, int i) {
        generateDate(i);
        CollectionHelper helper=new CollectionHelper(context);
        helper.delete("Collection",new String[]{"comic"},new String[]{comic});
        helper.insert("Collection",new String[]{"comic","picture","book","chapterResult", "imageResult", "chapterIndex", "pageIndex"},
                new Object[]{comic,
                        picture,
                        bookString,
                        chapterResultString,
                        imageResultString,
                        chapterIndex,
                        pageIndex});
        CollectionComic targetComic=collectionComicList.get(i);
        collectionComicList.remove(targetComic);
        collectionComicList.add(0,targetComic);
        notifyDataSetChanged();
    }

    @Override
    public void rightOptionClick(View view, int i) {
        generateDate(i);
        CollectionHelper helper=new CollectionHelper(context);
        helper.delete("Collection",new String[]{"comic"},new String[]{comic});
        collectionComicList.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public void itemClick(View view, int i) {
        generateDate(i);
        Intent intent = new Intent(context, ReadingActivity.class);
        intent.putExtra("chapter",mChapter);
        intent.putExtra("result", mResult);
        intent.putExtra("index", chapterIndex);
        book tempbook= JSON.parseObject(bookString,book.class);
        intent.putExtra("book", (Serializable) tempbook);
        intent.putExtra("page", pageIndex);
        intent.putExtra("position", i);

        intent.setAction("CollectionActivity");
        ((CollectionActivity)context).startActivityForResult(intent,0);
    }


    private int pageIndex;
    private int chapterIndex;
    private String chapterResultString;
    private String imageResultString;
    private String bookString;
    private String comic;
    private String picture;
    private result mResult;
    private Chapter mChapter;

    private void generateDate(int position){
        pageIndex= collectionComicList.get(position).getPageIndex();
        chapterIndex=collectionComicList.get(position).getChapterIndex();
        chapterResultString=collectionComicList.get(position).getChapterResult();
        imageResultString=collectionComicList.get(position).getChapterResult();
        bookString=collectionComicList.get(position).getBook();
        mResult=JSON.parseObject(chapterResultString,result.class);
        mChapter=mResult.getChapterList().get(chapterIndex);
        comic=collectionComicList.get(position).getComic();
        picture=collectionComicList.get(position).getPicture();
    }

    public class ViewHolder {
        ImageView imageView;
       TextView comicTv;
       TextView lastReadtv;

       public ViewHolder(View view) {
           imageView= (ImageView) view.findViewById(R.id.collection_comicIv);
           comicTv= (TextView) view.findViewById(R.id.collection_comicTv);
           lastReadtv= (TextView) view.findViewById(R.id.lastReadTv);
       }
   }

}
