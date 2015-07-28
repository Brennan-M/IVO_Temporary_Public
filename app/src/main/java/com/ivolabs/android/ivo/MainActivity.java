package com.ivolabs.android.ivo;

/**
 * Created by Brennan on 7/27/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * This is the main activity which runs upon starting. If the user is logged in,
 * they will be directed to the HomescreenActivity. Otherwise they will be directed
 * to the LoginActivity or the SignupActivity.
 */
public class MainActivity extends Activity {

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, HomescreenActivity.class));
        } else {
            startActivity(new Intent(this, FrontScreenActivity.class));
        }
    }

}