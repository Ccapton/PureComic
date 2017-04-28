package com.capton.purecomic.DataModel.ImageList;

/**
 * Created by capton on 2017/4/18.
 */

public class image {
    /*
    *  "imageUrl" : "http://imgs.juheapi.com/comic_xin/vMTJ%2Bsre/8061/0-ODA2MTA=.jpg" ,
				 "id" : 1
    * */
    private String imageUrl;
    private int id;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
