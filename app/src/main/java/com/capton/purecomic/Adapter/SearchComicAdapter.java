package com.capton.purecomic.Adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capton.purecomic.DataModel.BookList.book;
import com.capton.purecomic.R;
import com.capton.purecomic.Utils.DisplayUtil;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

/**
 * Created by capton on 2017/4/23.
 */

public class SearchComicAdapter extends RecyclerView.Adapter {

    private ArrayList<book> bookList;
    private LayoutInflater inflater;
    private Context context;



    public SearchComicAdapter(Context context, ArrayList<book> bookList ) {
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.bookList=bookList;
    }


    public ArrayList<book> getBookList() {
        return bookList;
    }

    public void setBookList(ArrayList<book> bookList) {
        this.bookList = bookList;
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==VIEWTYPE_NORMAL) {
            View convertView = inflater.inflate(R.layout.layout_comic, parent, false);
            return new  ComicViewHolder(convertView);
        }else {
            View  convertView = inflater.inflate(R.layout.layout_comic_footer, parent, false);

            return new  FooterViewHolder(convertView);
        }

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(bookList.get(position).getViewType()==VIEWTYPE_NORMAL) {
            ((SearchComicAdapter.ComicViewHolder) holder).textView.setText(bookList.get(position).getName());
            Glide.with(context).load(bookList.get(position).getCoverImg()).fitCenter().override(200, 280).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .skipMemoryCache(true).placeholder(R.color.colorGrey).into(((SearchComicAdapter.ComicViewHolder) holder).imageView);
            ((SearchComicAdapter.ComicViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder, bookList, position);
                }
            });
            ((SearchComicAdapter.ComicViewHolder) holder).imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ObjectAnimator translateX = ObjectAnimator.ofFloat(v.getParent(), "translationX", 0f, DisplayUtil.dip2px(context, 1));
                        ObjectAnimator translateY = ObjectAnimator.ofFloat(v.getParent(), "translationY", 0f, DisplayUtil.dip2px(context, 1));
                        AnimatorSet set = new AnimatorSet();
                        set.setDuration(100);
                        set.play(translateX).with(translateY);
                        set.start();
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        ObjectAnimator translateX = ObjectAnimator.ofFloat(v.getParent(), "translationX", DisplayUtil.dip2px(context, 1), 0f);
                        ObjectAnimator translateY = ObjectAnimator.ofFloat(v.getParent(), "translationY", DisplayUtil.dip2px(context, 1), 0f);
                        AnimatorSet set = new AnimatorSet();
                        set.setDuration(100);
                        set.play(translateX).with(translateY);
                        set.start();

                    }
                    return false;
                }
            });
        }else {
       //     Glide.with(context).load(bookList.get(position).getAwesomeDiyAdInfo().getImageUrl("720*1280")).into(((SearchComicAdapter.FooterViewHolder)holder).adIv);

        }

    }
    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public final int VIEWTYPE_NORMAL=0;
    public final int VIEWTYPE_FOOTER=1;
    @Override
    public int getItemViewType(int position) {
        return bookList.get(position).getViewType();
    }

    public class ComicViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView imageView;
        public ComicViewHolder(View itemView) {
            super(itemView);
            textView= (TextView) itemView.findViewById(R.id.comicTv);
            imageView= (ImageView) itemView.findViewById(R.id.comicIv);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        private SpinKitView spinKitView;
        private ImageView adIv;
        public FooterViewHolder(View itemView) {
            super(itemView);
            spinKitView= (SpinKitView) itemView.findViewById(R.id.spin_kit);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView.ViewHolder viewHolder, ArrayList<book> bookList, int position);
    }

    public OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

}
