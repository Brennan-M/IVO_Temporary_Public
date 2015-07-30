package com.ivolabs.android.ivo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;


/**
 * A placeholder fragment containing a simple view.
 */
public class IvoPostActivityFragment extends Fragment {

    private ParseGeoPoint geoCoordinates;

    public IvoPostActivityFragment() {
    }

    private EditText ivoPostText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ivo_post, container, false);


        final Button postButton = (Button) view.findViewById(R.id.submit_ivopost_button);
        ivoPostText = (EditText) view.findViewById(R.id.postTextEntry);

        postButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                post();
            }
        });




        return view;
    }


    private void post () {

        IVO_DB_POST newIvoPost = new IVO_DB_POST();
        newIvoPost.setUser(ParseUser.getCurrentUser());
        String text = ivoPostText.getText().toString().trim();
        newIvoPost.setTextEntry(text);
        newIvoPost.setUserName(ParseUser.getCurrentUser().getUsername());

        /*
        Intent intent = getActivity().getIntent();
        Location location = intent.getParcelableExtra("location");
        geoCoordinates = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        newIvoPost.setLocation(geoCoordinates);
        */

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        newIvoPost.setACL(acl);

        newIvoPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                IvoPostActivityFragment.this.getActivity().finish();
            }
        });
    }
}
