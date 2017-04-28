package com.capton.purecomic.DataBase;

import android.content.Context;

/**
 * Created by capton on 2017/4/19.
 */

public class CollectionHelper extends DataBaseHelper {
    private static CollectionHelper mCollectionHelper;
    public CollectionHelper(Context context) {
        super(context);
    }
    public static CollectionHelper getInstance(Context context){
        if (mCollectionHelper==null){
            synchronized (DataBaseHelper.class){
                if (mCollectionHelper==null){
                    mCollectionHelper = new CollectionHelper(context);
                    if (mCollectionHelper.getDB()==null||!mCollectionHelper.getDB().isOpen()){
                        mCollectionHelper.open();
                    }
                }
            }
        }
        return mCollectionHelper;
    }
    @Override
    protected int getMDbVersion(Context context) {
        return 1;
    }

    @Override
    protected String getDbName(Context context) {
        return "UserCollection";
    }

    @Override
    protected String[] getDbCreateSql(Context context) {
        String[] a = new String[1];
        a[0] = "CREATE TABLE Collection (id INTEGER PRIMARY KEY AUTOINCREMENT,comic TEXT,picture TEXT,book TEXT,chapterResult TEXT,imageResult TEXT,chapterIndex INTEGER,pageIndex INTEGER)";
        return a;
    }

    @Override
    protected String[] getDbUpdateSql(Context context) {
        return new String[0];
    }
}
