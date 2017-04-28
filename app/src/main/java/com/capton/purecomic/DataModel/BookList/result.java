package com.capton.purecomic.DataModel.BookList;

/**
 * Created by capton on 2017/4/17.
 */

public class result {
    private int total;
    private int limit;
    private bookList mBookList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public bookList getBookList() {
        return mBookList;
    }

    public void setBookList(bookList mBookList) {
        this.mBookList = mBookList;
    }
}
