package com.example.decrypt;

import android.app.Application;


/**
 * Created by jzg on 2015/12/22.
 */
public class AppContext extends Application {
    private static AppContext instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppContext getContext() {
        return instance;
    }
}

