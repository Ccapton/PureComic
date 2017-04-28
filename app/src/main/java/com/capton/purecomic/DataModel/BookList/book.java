package com.capton.purecomic.DataModel.BookList;


//import com.baidu.appx.BDNativeAd;

import java.io.Serializable;

/**
 * Created by capton on 2017/4/17.
 */

public class book   implements Serializable {
    private String name;
    private String type;
    private String area;
    private String des;
    private boolean finish;
    private int lastUpdate;
    private String coverImg;
    public static final int VIEWTYPE_NORMAL=0;
    public static final int VIEWTYPE_AD=1;
    public int viewType=VIEWTYPE_NORMAL;

  // private  BDNativeAd.AdInfo adInfo;

 //   public BDNativeAd.AdInfo getAdInfo() {
 //       return adInfo;
 //   }

  //  public void setAdInfo(BDNativeAd.AdInfo adInfo) {
  //      this.adInfo = adInfo;
  //  }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    @Override
    public String toString() {
        return "book{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", area='" + area + '\'' +
                ", des='" + des + '\'' +
                ", finish=" + finish +
                ", lastUpdate=" + lastUpdate +
                ", coverImg='" + coverImg + '\'' +
                '}';
    }
}
