package com.pearson.lt.mobileplatform.android.core.tasks;

public interface TaskCallback<T> {

    public void onResponse(T response);

    public void onException(Exception e);

}
