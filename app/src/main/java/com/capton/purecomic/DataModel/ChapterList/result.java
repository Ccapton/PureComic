package com.capton.purecomic.DataModel.ChapterList;



import java.io.Serializable;

/**
 * Created by capton on 2017/4/18.
 */
public class result implements Serializable{

    private Long id;

    private int total;
    private int limit;
    private String comicName;
    private ChapterList ChapterList;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public ChapterList getChapterList() {
        return ChapterList;
    }

    public void setChapterList(ChapterList mChapterList) {
        this.ChapterList = mChapterList;
    }

    @Override
    public String toString() {
        return "result{" +
                "total=" + total +
                ", limit=" + limit +
                ", comicName='" + comicName + '\'' +
                ", mChapterList=" + ChapterList +
                '}';
    }
}
