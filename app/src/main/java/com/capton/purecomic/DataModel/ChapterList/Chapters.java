package com.capton.purecomic.DataModel.ChapterList;


/**
 * Created by capton on 2017/4/18.
 */
public class Chapters {
    /*
    *
	 "error_code" : 200 ,
	 "reason" : "请求成功！" ,
	 "result"
    * */
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
