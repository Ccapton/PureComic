package com.capton.purecomic.DataModel.ChapterList;


import java.io.Serializable;

/**
 * Created by capton on 2017/4/18.
 */

public class Chapter implements Serializable{

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
