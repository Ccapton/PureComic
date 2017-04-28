package com.capton.purecomic.DataModel.BmobData;

import com.capton.purecomic.DataModel.CollectionComic;

import cn.bmob.v3.BmobObject;

/**
 * Created by capton on 2017/4/20.
 */

public class BmobCollection extends BmobObject {
    private CollectionComic collectionComic;
    private MyUser myUser;

    public CollectionComic getCollectionComic() {
        return collectionComic;
    }

    public void setCollectionComic(CollectionComic collectionComic) {
        this.collectionComic = collectionComic;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }
}
