package com.capton.purecomic.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capton.purecomic.CustomView.ScaleView;
import com.capton.purecomic.DataModel.ImageList.image;
import com.capton.purecomic.R;

import java.util.ArrayList;

/**
 * Created by capton on 2017/4/18.
 */

public class ComicPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<View> viewList;
    private ArrayList<image> imageArrayList;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<View> getViewList() {
        return viewList;
    }

    public void setViewList(ArrayList<View> viewList) {
        this.viewList = viewList;
    }

    public ArrayList<image> getImageArrayList() {
        return imageArrayList;
    }

    public void setImageArrayList(ArrayList<image> imageArrayList) {
        this.imageArrayList = imageArrayList;
    }

    public ComicPagerAdapter(Context context, ArrayList<View> viewList, ArrayList<image> imageArrayList) {
        this.context=context;
        this.viewList=viewList;
        this.imageArrayList=imageArrayList;
     }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=viewList.get(position);
        final LinearLayout loadingLayout= (LinearLayout) view.findViewById(R.id.loadingLayout);
        ScaleView scaleView= (ScaleView) view.findViewById(R.id.scaleView);
        Glide.with(context).load(imageArrayList.get(position).getImageUrl()).diskCacheStrategy( DiskCacheStrategy.RESULT )
                .skipMemoryCache( true ).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                loadingLayout.setVisibility(View.GONE);
                Glide.clear(loadingLayout.getChildAt(0));
                return false;
            }
        }).into(scaleView);
        container.addView(view);
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Glide.clear(viewList.get(position));
        container.removeView(viewList.get(position));
    }


}
