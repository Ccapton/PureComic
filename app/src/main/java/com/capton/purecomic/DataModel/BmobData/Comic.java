package com.capton.purecomic.DataModel.BmobData;

import cn.bmob.v3.BmobObject;

/**
 * Created by capton on 2017/4/20.
 */

public class Comic extends BmobObject {
    private String comicName;
    private String picture;
    private int readTimes=0;
    private int collectTimes=0;

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(int readTimes) {
        this.readTimes = readTimes;
    }

    public int getCollectTimes() {
        return collectTimes;
    }

    public void setCollectTimes(int collectTimes) {
        this.collectTimes = collectTimes;
    }
}
