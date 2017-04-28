package com.capton.purecomic.DataModel.BmobData;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by capton on 2017/4/20.
 */

public class BmobUtil {

    public static BmobUtil mBmobUtil;
    public static MyUser mMyUser;

    public static BmobUtil login(String username,String password){
        if(mBmobUtil==null)
            mBmobUtil=new BmobUtil();

        mMyUser= BmobUser.getCurrentUser(MyUser.class);
        if(mMyUser==null){
            BmobUser bmobUser=new BmobUser();
            bmobUser.setUsername(username);
            bmobUser.setPassword(password);
            bmobUser.login(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser myUser, BmobException e) {
                    mMyUser=myUser;
                }
            });
        }
        return mBmobUtil;
    }
    public static void logOut(){
        BmobUser.logOut();
    }


    /*
    * 普通的添加数据方法
    * */
    public static void add(Object object){
        if(object!=null){
            ((BmobObject)object).save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        // s 为objectId
                    }else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 保存objectId的添加数据方法
     * */
    private static SharedPreferences spf;
    public static final int ACTION_COLLECT_CANCLE=-1;
    public static final int ACTION_COLLECT=0;
    public static final int ACTION_READ=1;
    public static void add(Context context, final Comic mComic, final int action){
        if(mMyUser==null)
            mMyUser= BmobUser.getCurrentUser(MyUser.class);

        if(spf==null)
         spf=context.getSharedPreferences("ComicObjectId",Context.MODE_PRIVATE);
        String comicObjectId=spf.getString(mComic.getComicName(),"");
        BmobQuery<Comic> query=new BmobQuery<>();
        if(comicObjectId.equals("")){
            mComic.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        // s 为objectId
                        spf.edit().putString(mComic.getComicName(),s).apply();
                    }else {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            query.getObject(comicObjectId, new QueryListener<Comic>() {
                @Override
                public void done(Comic comic, BmobException e) {
                    if (e == null) {
                        if (comic != null) {
                            update(comic, action);
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    /*
    *  批量添加收藏的漫画
    * */
    public static void add(final List<BmobObject> bmobCollections){
        if(mMyUser==null)
            mMyUser= BmobUser.getCurrentUser(MyUser.class);

        new BmobBatch().insertBatch(bmobCollections).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                             if(onBmobDataDoneListener!=null){
                                 onBmobDataDoneListener.onBmobDataAdded();
                             }
                            Log.i("","第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                        }else{
                            Log.i("","第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    public interface OnBmobDataDoneListener{
        void onBmobDataDeleted(List<BatchResult> list);
        void onBmobDataAdded();
        void onBmobDataQuery(List<BmobCollection> bmobObjectList);
    }
    public static OnBmobDataDoneListener onBmobDataDoneListener;
    public static void setOnBmobDataDoneListener(OnBmobDataDoneListener Listener){
        onBmobDataDoneListener=Listener;
    }


    public static void delete(final List<BmobObject> bmobCollections){
          if(mMyUser==null)
              mMyUser= BmobUser.getCurrentUser(MyUser.class);

        new BmobBatch().deleteBatch(bmobCollections).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if(e==null){
                    //返回结果的results和上面提交的顺序是一样的，请一一对应
                    for(int i=0;i<list.size();i++){
                        BatchResult result= list.get(i);
                        if(result.isSuccess()){//只有批量添加才返回objectId
                            Log.i("","第"+i+"个成功："+result.getObjectId()+","+result.getUpdatedAt());
                        }else{
                            BmobException error= result.getError();
                            Log.i("","第"+i+"个失败："+error.getErrorCode()+","+error.getMessage());
                        }
                    }
                    if(onBmobDataDoneListener!=null){
                        onBmobDataDoneListener.onBmobDataDeleted(list);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private static void update(Comic comic,int action){
           switch (action){
               case ACTION_COLLECT:
                   comic.increment("collectTimes");
                   comic.update(spf.getString(comic.getComicName(), ""), new UpdateListener() {
                       @Override
                       public void done(BmobException e) {
                           if(e==null){

                           }else {
                               e.printStackTrace();
                           }
                       }
                   });
                   break;
               case ACTION_READ:
                   comic.increment("readTimes");
                   comic.update(spf.getString(comic.getComicName(), ""), new UpdateListener() {
                       @Override
                       public void done(BmobException e) {
                           if(e==null){

                           }else {
                               e.printStackTrace();
                           }
                       }
                   });
                   break;
                case ACTION_COLLECT_CANCLE:
                    comic.increment("collectTimes",-1);
                   comic.update(spf.getString(comic.getComicName(), ""), new UpdateListener() {
                       @Override
                       public void done(BmobException e) {
                           if(e==null){

                           }else {
                               e.printStackTrace();
                           }
                       }
                   });
                   break;
           }
    }

    public static void queryByMyUser(){
        if(mMyUser==null)
            mMyUser= BmobUser.getCurrentUser(MyUser.class);
            BmobQuery<BmobCollection> query=new BmobQuery<>();
            query.addWhereEqualTo("myUser",mMyUser);
            query.setLimit(500);
            query.findObjects(new FindListener<BmobCollection>() {
                @Override
                public void done(List<BmobCollection> list, BmobException e) {
                    if(e==null) {
                        if (onBmobDataDoneListener != null) {
                            onBmobDataDoneListener.onBmobDataQuery(list);
                        }
                    }else {
                        e.printStackTrace();
                    }
                }
            });

    }
}
