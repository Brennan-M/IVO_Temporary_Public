package com.ivolabs.android.ivo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Brennan on 7/28/15.
 */
public class IvoInitialization extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(IVO_DB_POST.class);
        Parse.initialize(this, "QWc3IKpFo2B5WLzfZsKD01tE8MFMh9AAkBGlBChQ", "4sWqGBTufwz72IOcKRfIaOpKJ4Lfo65sRFkTTium");
    }
}

