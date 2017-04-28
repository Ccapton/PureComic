package com.capton.purecomic;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

/**
 * Created by capton on 2017/4/22.
 */

public class ReadingFragment extends Fragment {


    private image mImage;

    public image getmImage() {
        return mImage;
    }

    public void setmImage(image mImage) {
        this.mImage = mImage;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.layout_comic_page,container,false);
        final LinearLayout loadingLayout= (LinearLayout) view.findViewById(R.id.loadingLayout);
        ScaleView scaleView= (ScaleView) view.findViewById(R.id.scaleView);
        Glide.with(getActivity()).load(mImage.getImageUrl()).diskCacheStrategy( DiskCacheStrategy.RESULT )
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
        return view;
    }

}
