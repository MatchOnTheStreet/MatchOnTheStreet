package com.cse403.matchonthestreet.controller;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.cse403.matchonthestreet.models.Account;

/**
 * Created by Hao on 2/18/2016.
 */
public class MOTSApp extends Application {

    private static Point SCREEN_RES = new Point();

    private static ViewController viewController = new ViewController();
    private static Context mContext;
    private Account myAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        viewController.setContext(this);
        SportsIconFinder.initialize(this);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getSize(SCREEN_RES);
        System.out.println("MOTSApp created");
    }

    public static Point getScreenRes() { return SCREEN_RES; }

    public static Context getContext(){
        return mContext;
    }

    public ViewController getViewController() {
        return viewController;
    }

    public Account getMyAccount() {
        return myAccount;
    }

    public void setMyAccount(Account myAccount) {
        this.myAccount = myAccount;
    }
}
