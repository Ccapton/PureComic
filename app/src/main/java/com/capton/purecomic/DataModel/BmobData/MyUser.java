package com.capton.purecomic.DataModel.BmobData;

import cn.bmob.v3.BmobUser;

/**
 * Created by capton on 2017/4/20.
 */

public class MyUser extends BmobUser {
    private String nick;
    private String picture;
    private String introduction;
    private String sex="male";

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
