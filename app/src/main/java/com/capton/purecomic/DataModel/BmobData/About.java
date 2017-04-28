package com.capton.purecomic.DataModel.BmobData;

import cn.bmob.v3.BmobObject;

/**
 * Created by capton on 2017/4/24.
 */

public class About extends BmobObject {
    private String appName;
    private String version;
    private String content;
    private String e_mail="437499914@qq.com";
    private String author="Capton";

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
