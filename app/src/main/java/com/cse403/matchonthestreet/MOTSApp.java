package com.cse403.matchonthestreet;

import android.app.Application;

import java.util.List;
import java.util.Set;

/**
 * Created by Hao on 2/18/2016.
 */
public class MOTSApp extends Application {

    private ViewController viewController = new ViewController();

    public ViewController getViewController() {
        return viewController;
    }


}
