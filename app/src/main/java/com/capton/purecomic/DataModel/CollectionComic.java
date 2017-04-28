package com.capton.purecomic.DataModel;

import java.io.Serializable;


/**
 * Created by capton on 2017/4/19.
 */

public class CollectionComic implements Serializable {
    private String comic ;
    private String picture;
    private String book;
    private String chapterResult;
    private String imageResult;
    private int chapterIndex;
    private int pageIndex;

    public String getComic () {
        return comic;
    }

    public void setComic (String comic) {
        this.comic = comic;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getChapterResult() {
        return chapterResult;
    }

    public void setChapterResult(String chapterResult) {
        this.chapterResult = chapterResult;
    }

    public String getImageResult() {
        return imageResult;
    }

    public void setImageResult(String imageResult) {
        this.imageResult = imageResult;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public String toString() {
        return "CollectionComic{" +
                "comic='" + comic + '\'' +
                ", picture='" + picture + '\'' +
                ", book='" + book + '\'' +
                ", chapterResult='" + chapterResult + '\'' +
                ", imageResult='" + imageResult + '\'' +
                ", chapterIndex=" + chapterIndex +
                ", pageIndex=" + pageIndex +
                '}';
    }
}
