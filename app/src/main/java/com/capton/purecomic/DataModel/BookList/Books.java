package com.capton.purecomic.DataModel.BookList;

/**
 * Created by capton on 2017/4/17.
 */

/*
*  "error_code" : 200 ,
	 "reason" : "请求成功！" ,
	 "result" : {
		 "total" : 15767 ,
		 "limit" : 20 ,
		 "bookList" : [
			 {
				 "name" : "灵神考试" ,
				 "type" : "少年漫画" ,
				 "area" : "国漫" ,
				 "des" : "" ,
				 "finish" : false ,
				 "lastUpdate" : 20150603 ,
				 "coverImg" : "http://imgs.juheapi.com/comic_xin/5559b86938f275fd560ad613.jpg"
			} ,
*
* */
public class Books {
    private int error_code;
    private String reason;
    private result mResult;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public result getResult() {
        return mResult;
    }

    public void setResult(result mResult) {
        this.mResult = mResult;
    }
}
