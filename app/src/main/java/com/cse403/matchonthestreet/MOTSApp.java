package com.cse403.matchonthestreet;

import android.app.Application;
import android.content.Context;

import java.util.List;
import java.util.Set;

/**
 * Created by Hao on 2/18/2016.
 */
public class MOTSApp extends Application {

    private static ViewController viewController = new ViewController();
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

    public ViewController getViewController() {
        return viewController;
    }


}
