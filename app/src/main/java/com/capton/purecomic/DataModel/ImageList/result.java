package com.capton.purecomic.DataModel.ImageList;

/**
 * Created by capton on 2017/4/18.
 */

public class result {
    /*
    *  "comicName" : "寄生兽" ,
		 "chapterId" : 8061 ,
		 "imageList" : [
    * */
    private String comicName;
    private int chapterId;
    private imageList mImageList;

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public imageList getImageList() {
        return mImageList;
    }

    public void setImageList(imageList mImageList) {
        this.mImageList = mImageList;
    }
}
