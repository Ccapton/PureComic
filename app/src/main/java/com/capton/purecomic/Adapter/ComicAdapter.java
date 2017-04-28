package com.capton.purecomic.Adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//import com.baidu.appx.BDNativeAd;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capton.purecomic.DataModel.BookList.book;
import com.capton.purecomic.R;
import com.capton.purecomic.Utils.DisplayUtil;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

/**
 * Created by capton on 2017/4/17.
 */

public class ComicAdapter extends RecyclerView.Adapter {

     private ArrayList<book> bookList;
    private LayoutInflater inflater;
    private Context context;



    public ComicAdapter(Context context, ArrayList<book> bookList) {
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
            return new ComicViewHolder(convertView);
        }else {
            View  convertView = inflater.inflate(R.layout.layout_comic_footer, parent, false);
            return new FooterViewHolder(convertView);
        }

    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (bookList.get(position).getViewType() == VIEWTYPE_NORMAL) {
            ((ComicViewHolder) holder).textView.setText(bookList.get(position).getName());
            Glide.with(context).load(bookList.get(position).getCoverImg()).fitCenter().override(200, 280).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .skipMemoryCache(true).placeholder(R.color.colorGrey).into(((ComicViewHolder) holder).imageView);
            ((ComicViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder, bookList, position);
                }
            });
            ((ComicViewHolder) holder).imageView.setOnTouchListener(new View.OnTouchListener() {
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
        } else {
           /* final BDNativeAd.AdInfo adInfo=bookList.get(position).getAdInfo();
            if (adInfo != null) {
                String title = adInfo.getTitle();
                String description = adInfo.getDescription();
                String fileSize =adInfo.getFileSize();
                String iconUrl = adInfo.getIconUrl();
               // String imageUrl = adInfo.getImageUrl();
                int imageHeight = adInfo.getImageHeight();
                int imageWidth = adInfo.getImageWidth();

                if(imageWidth==0){
                    imageWidth=DisplayUtil.dip2px(context,100);
                }
                if(imageHeight==0){
                    imageHeight=DisplayUtil.dip2px(context,100);
                }

                ((FooterViewHolder) holder).titleTv.setText(title);
                ((FooterViewHolder) holder).descriptionTv.setText(description);

                Glide.with(context).load(iconUrl).override(imageWidth,imageHeight).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ((FooterViewHolder) holder).spinKitView.setVisibility(View.GONE);
                        return false;
                    }
                }).into(((FooterViewHolder) holder).iconIv);

                adInfo.didShow();

                ((FooterViewHolder) holder).iconIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String filesize="";
                        if(!adInfo.getFileSize().equals("null")){
                            filesize=adInfo.getFileSize();
                        }
                        new AlertDialog.Builder(context)
                                .setMessage("确定要下载"+adInfo.getTitle()+" "+filesize+"?").setTitle("下载应用")
                                .setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((FooterViewHolder) holder).setAdInfo(adInfo);
                                onItemClickListener.onItemClick(holder,null,2000);
                            }
                        }).create().show();
                    }
                });
            }*/
        }
    }
    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public final int VIEWTYPE_NORMAL=0;
    public final int VIEWTYPE_AD=1;
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

   public class FooterViewHolder extends RecyclerView.ViewHolder{
        private SpinKitView spinKitView;
        TextView titleTv;
        TextView fileSizeTv;
        TextView descriptionTv;
        ImageView iconIv;
       /* BDNativeAd.AdInfo adInfo;

        public BDNativeAd.AdInfo getAdInfo() {
            return adInfo;
        }

        public void setAdInfo(BDNativeAd.AdInfo adInfo) {
            this.adInfo = adInfo;
        }*/

        public FooterViewHolder(View itemView) {
            super(itemView);
            spinKitView= (SpinKitView) itemView.findViewById(R.id.spin_kit);
             titleTv= (TextView) itemView.findViewById(R.id.titleTv);
             descriptionTv= (TextView) itemView.findViewById(R.id.descriptionTv);
             iconIv= (ImageView) itemView.findViewById(R.id.iconIv);
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
