package com.gmail.jorgegilcavazos.ballislife.features.data;

/**
 * Created by jorgegil on 12/29/16.
 */

public class AsyncLoaderResult<T> {
    private Exception exception;
    private T data;


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
