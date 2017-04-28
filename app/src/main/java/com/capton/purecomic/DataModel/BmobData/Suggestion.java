package com.capton.purecomic.DataModel.BmobData;

import cn.bmob.v3.BmobObject;

/**
 * Created by capton on 2017/4/24.
 */

public class Suggestion extends BmobObject {
    private String content;
    private MyUser myUser;
    private String response;
    private boolean userReaded;

    public boolean isUserReaded() {
        return userReaded;
    }

    public void setUserReaded(boolean userReaded) {
        this.userReaded = userReaded;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
