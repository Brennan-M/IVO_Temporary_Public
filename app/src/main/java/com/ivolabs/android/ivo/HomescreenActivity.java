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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;


public class HomescreenActivity extends ActionBarActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // This variable holds our request to connect to Location Services
    private LocationRequest locationRequest;
    // This variable holds our current instance of the location client
    private GoogleApiClient locationClient;

    private Location lastLocation;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


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

        final TextView welcomeTextView = (TextView) findViewById(R.id.welcomeUser);

        /* Button to start our IvoPost Activity which needs our location */
        Button startIvoPostActivityButton = (Button) findViewById(R.id.post_to_Ivo_button);
        startIvoPostActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Location locationToPass = (currentLocation == null) ? lastLocation : currentLocation;
                if (locationToPass == null) {
                    Toast.makeText(HomescreenActivity.this,
                            "Please enable location services on your device.", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(HomescreenActivity.this, IvoPostActivity.class);
                intent.putExtra("location", locationToPass);
                startActivity(intent);
            }
        });

        /* Button to start our IvoFeed Activity which needs our location */
        Button startIvoFeedActivityButton = (Button) findViewById(R.id.get_Ivo_feed_button);
        startIvoFeedActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Location locationToPass = (currentLocation == null) ? lastLocation : currentLocation;
                if (locationToPass == null) {
                    Toast.makeText(HomescreenActivity.this,
                            "Your location cannot be accessed at the moment...", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(HomescreenActivity.this, IvoFeedActivity.class);
                intent.putExtra("location", locationToPass);
                startActivity(intent);
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        welcomeTextView.setText("Welcome " + currentUser.getUsername());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);

        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(HomescreenActivity.this, SettingsActivity.class));
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
        // Update the display
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
}
