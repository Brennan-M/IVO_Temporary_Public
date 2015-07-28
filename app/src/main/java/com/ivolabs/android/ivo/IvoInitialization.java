package com.ivolabs.android.ivo;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Brennan on 7/28/15.
 */
public class IvoInitialization extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "QWc3IKpFo2B5WLzfZsKD01tE8MFMh9AAkBGlBChQ", "4sWqGBTufwz72IOcKRfIaOpKJ4Lfo65sRFkTTium");
    }
}

