package com.capton.purecomic.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.capton.purecomic.DataModel.ChapterList.ChapterList;
import com.capton.purecomic.R;

/**
 * Created by capton on 2017/4/18.
 */

public class ChapterListAdapter extends BaseAdapter {

    private ChapterList mChapterList;
    private Context context;
    private LayoutInflater inflater;
    private int currentIndex;

    public ChapterListAdapter(Context context, ChapterList mChapterList, int currentIndex) {
        this.mChapterList = mChapterList;
        this.context = context;
        this.currentIndex=currentIndex;
        inflater=LayoutInflater.from(context);
    }

    public ChapterList getChapterList() {
        return mChapterList;
    }

    public void setChapterList(ChapterList mChapterList) {
        this.mChapterList = mChapterList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return mChapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler viewHoler=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.chapter_item,parent,false);
            viewHoler=new ViewHoler(convertView);
            convertView.setTag(viewHoler);
        }else {
            viewHoler= (ViewHoler) convertView.getTag();
        }

        viewHoler.chapterTv.setText(mChapterList.get(position).getName());
        if(position==currentIndex) {
            viewHoler.ball.setBackgroundResource(R.drawable.ball);
        }else {
            viewHoler.ball.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }

    class ViewHoler {
       public TextView chapterTv;
       public TextView ball;
        public ViewHoler(View view) {
            chapterTv= (TextView) view.findViewById(R.id.chapterTv);
            ball= (TextView) view.findViewById(R.id.ball);
        }
    }
}
