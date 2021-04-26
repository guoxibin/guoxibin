package com.example.imagebrower.util;

import android.app.Application;
import android.content.Context;

/**
 * 全局获取context
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
