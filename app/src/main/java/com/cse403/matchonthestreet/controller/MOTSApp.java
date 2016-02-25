package com.cse403.matchonthestreet.controller;

import android.app.Application;
import android.content.Context;

import com.cse403.matchonthestreet.models.Account;

/**
 * Created by Hao on 2/18/2016.
 */
public class MOTSApp extends Application {

    private static ViewController viewController = new ViewController();
    private static Context mContext;



    private Account myAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SportsIconFinder.initialize(this);
        System.out.println("MOTSApp created");
    }

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
