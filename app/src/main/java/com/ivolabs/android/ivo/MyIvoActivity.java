package com.ivolabs.android.ivo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class MyIvoActivity extends ActionBarActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // This variable holds our request to connect to Location Services
    private LocationRequest locationRequest;
    // This variable holds our current instance of the location client
    private GoogleApiClient locationClient;

    private Location lastLocation;
    private Location currentLocation;

    private EditText ivoPostText;

    private static final float DISTANCE_TO_SEARCH_IN_FEET = 250.0f;
    private static final float METERS_PER_FEET = 0.3048f;
    private static final int METERS_PER_KILOMETER = 1000;

    private ParseQueryAdapter<IVO_DB_POST> IvoFeedQueryAdapter;

    final ParseUser user = ParseUser.getCurrentUser();

    String category;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myivo);


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(1000);

        // Create a new location client
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        spinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.categories_array, R.layout.alttextview);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CategoryOnItemSelectedListener());

        /* This is the ParseQueryAdapter which prepares us to retrieve results from our database */
        ParseQueryAdapter.QueryFactory<IVO_DB_POST> factory =
                new ParseQueryAdapter.QueryFactory<IVO_DB_POST>() {
                    public ParseQuery<IVO_DB_POST> create() {
                        if (currentLocation == null) {
                            return null;
                        }

                        ParseQuery<IVO_DB_POST> query = IVO_DB_POST.getQuery();
                        query.include("user");
                        query.orderByDescending("votes");
                        query.whereWithinKilometers("geoLocation", geoPointFromLocation(currentLocation), DISTANCE_TO_SEARCH_IN_FEET * METERS_PER_FEET/METERS_PER_KILOMETER);
                        query.whereEqualTo("category", spinner.getSelectedItem().toString()); //toString or not?
                        return query;
                    }
                };


        IvoFeedQueryAdapter = new ParseQueryAdapter<IVO_DB_POST>(this, factory) {
            @Override
            public View getItemView(final IVO_DB_POST post, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.ivo_post_item, null);
                }


                TextView contentView = (TextView) view.findViewById(R.id.content_view);
                TextView usernameView = (TextView) view.findViewById(R.id.username_view);
                final Button upvote = (Button) view.findViewById(R.id.like_button);

                ParseRelation relation = user.getRelation("LikedIvoPosts");
                ParseQuery query = relation.getQuery();
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject element : list) {
                            if (element.getObjectId().equals(post.getObjectId())) {

//                                upvote.setBackgroundColor(0x8deeee);
                                upvote.setText(String.valueOf(post.getVoteCount()));
                                // Display like count
                                return;
                            }
                        }

//                        upvote.setBackgroundColor(0xc0d9d9);
                        upvote.setText(String.valueOf(post.getVoteCount()));

                        upvote.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                // Increment liked counter
                                post.setVoteCount(post.getVoteCount() + 1);

                                // Add post to users' liked posts
                                ParseRelation postsLiked = user.getRelation("LikedIvoPosts");
                                postsLiked.add(post);

                                // Add user to posts' liked by users
                                ParseRelation likedByUsers = post.getRelation("LikedByIvoUsers");
                                likedByUsers.add(user);


                                post.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            user.saveInBackground();
                                            upvote.setOnClickListener(null);
                                            upvote.setText(String.valueOf(post.getVoteCount()));
                                            upvote.setBackgroundColor(0xc0d9d9);

                                            //upvote.setBackgroundColor(Color.GREEN);
                                        } else {
                                            Log.d("IVOTAG", "Failure: " + e);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });




                contentView.setText(post.getTextEntry());
                usernameView.setText(post.getUser().getUsername());
                return view;
            }
        };


        IvoFeedQueryAdapter.setAutoload(false);
        IvoFeedQueryAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView postsListView = (ListView) findViewById(R.id.ivo_feed_list);
        postsListView.setAdapter(IvoFeedQueryAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);

        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MyIvoActivity.this, SettingsActivity.class));
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Once our location client is connected via onStart(), get our location.
     */
    @Override
    public void onConnected(Bundle bundle) {
        currentLocation = getLocation();
        startPeriodicUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Ivo", "GoogleApiClient connection has been suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 9000);
            } catch (IntentSender.SendIntentException e) {
            }
        } else {
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onStop() {

        stopPeriodicUpdates();
        super.onStop();
    }

    @Override
    public void onStart() {

        super.onStart();
        locationClient.connect();
    }

    private void doListQuery() {

        if (currentLocation != null) {
            IvoFeedQueryAdapter.loadObjects();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        doListQuery();
    }

    private void startPeriodicUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
    }

    private void stopPeriodicUpdates() {
        locationClient.disconnect();
    }

    /*
     * Get the current location of our mobile user
     */
    private Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private boolean servicesConnected() {
        int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == response) {
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(response, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), "Ivo");
            }
            return false;
        }
    }

    /*
     * Helper method to get the Parse GEO point representation of a location
     */
    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    /*
     * Compares our current location to an old location, update old location if it is
     * actually different from our current location.
     */
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            return;
        }
        lastLocation = location;
        doListQuery();
    }

    /*
     * Show a dialog returned by Google Play services for the connection error code
     */
    private void showErrorDialog(int errorCode) {
        // Get the error dialog from Google Play services
        Dialog errorDialog =
                GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                        9000);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), "Ivo");
        }
    }

    private void post () {

        if (ivoPostText.getText().toString().trim().length() <= 0) {
            return;
        }

        IVO_DB_POST newIvoPost = new IVO_DB_POST();
        newIvoPost.setUser(ParseUser.getCurrentUser());
        String text = ivoPostText.getText().toString().trim();
        newIvoPost.setTextEntry(text);
        newIvoPost.setUserName(ParseUser.getCurrentUser().getUsername());
        newIvoPost.setVoteCount(0);
        newIvoPost.setCategory(spinner.getSelectedItem().toString());
        Log.d("IVOTAG", spinner.getSelectedItem().toString());

        Intent intent = this.getIntent();

        newIvoPost.setLocation(geoPointFromLocation(currentLocation));

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        newIvoPost.setACL(acl);

        newIvoPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ivoPostText.getText().clear();
                doListQuery();
            }
        });
    }

    public class CategoryOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            doListQuery();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
