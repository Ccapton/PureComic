package com.capton.purecomic.DataModel.BmobData;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by capton on 2017/4/24.
 */

public class UpdateForcast extends BmobObject {
     private String title;
    private String url;
    private ArrayList<String> characterList;
    private boolean isNewVersion=false;
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getCharacterList() {
        return characterList;
    }

    public void setCharacterList(ArrayList<String> characterList) {
        this.characterList = characterList;
    }

    public boolean isNewVersion() {
        return isNewVersion;
    }

    public void setNewVersion(boolean newVersion) {
        isNewVersion = newVersion;
    }
}
