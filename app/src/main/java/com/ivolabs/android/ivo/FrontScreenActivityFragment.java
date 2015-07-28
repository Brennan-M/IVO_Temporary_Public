package com.ivolabs.android.ivo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;


/**
 * A placeholder fragment containing a simple view.
 */
public class FrontScreenActivityFragment extends Fragment {

    public FrontScreenActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_front_screen, container, false);

        // Log in button click handler
        Button loginButton = (Button) view.findViewById(R.id.start_login);
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(FrontScreenActivityFragment.this.getActivity(), LoginActivity.class));
            }
        });

        // Sign up button click handler
        Button signupButton = (Button) view.findViewById(R.id.start_signup);
        signupButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(FrontScreenActivityFragment.this.getActivity(), SignupActivity.class));
            }
        });

        return view;
    }
}
