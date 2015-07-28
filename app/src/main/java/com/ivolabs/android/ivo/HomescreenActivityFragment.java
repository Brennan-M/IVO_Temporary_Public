package com.ivolabs.android.ivo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomescreenActivityFragment extends Fragment {

    public HomescreenActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }
}
