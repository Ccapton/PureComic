package com.capton.purecomic.Network;

import android.os.AsyncTask;

/**
 * Created by capton on 2017/4/25.
 */

public class CaptonHttp {
    public static int GET=0;
    public static int POST=1;
    private int Method;
     public String url;
   private CaptonHttp captonHttp;
    public CaptonHttp(int Method) {
        this.Method=Method;
    }

    public CaptonHttp  load(String url){
        captonHttp=this;
        this.url=url;
        return captonHttp;
    }

    public  void requst(int Method,ResponseListener listener){
        responseListener=listener;
        if(Method==GET){

        }else {

        }

    }

    private  ResponseListener responseListener;
   public interface  ResponseListener {
        void onResponseSuccess(String response);
        void onResponseFailed(String reponse, Exception e);
    }

    class MyAsynckTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

}
