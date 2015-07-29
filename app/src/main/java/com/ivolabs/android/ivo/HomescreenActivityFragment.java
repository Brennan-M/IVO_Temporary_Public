package com.ivolabs.android.ivo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomescreenActivityFragment extends Fragment {

    public HomescreenActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        final TextView welcomeTextView = (TextView) view.findViewById(R.id.welcomeUser);

        Button startIvoPostActivityButton = (Button) view.findViewById(R.id.post_to_Ivo_button);
        startIvoPostActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(HomescreenActivityFragment.this.getActivity(), IvoPostActivity.class);
                startActivity(intent);
            }
        });

        Button startIvoFeedActivityButton = (Button) view.findViewById(R.id.get_Ivo_feed_button);
        startIvoFeedActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(HomescreenActivityFragment.this.getActivity(), IvoFeedActivity.class);
                startActivity(intent);
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        welcomeTextView.setText("Welcome " + currentUser.getUsername());

        return view;
    }
}
