package com.ivolabs.android.ivo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;


public class IvoFeedActivity extends ActionBarActivity {

    Location currentLocation;

    private ParseQueryAdapter<IVO_DB_POST> IvoFeedQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivo_feed);

        Intent intent = getIntent();
        currentLocation = intent.getParcelableExtra("location");

        ParseQueryAdapter.QueryFactory<IVO_DB_POST> factory =
                new ParseQueryAdapter.QueryFactory<IVO_DB_POST>() {
                    public ParseQuery<IVO_DB_POST> create() {
                        if (currentLocation == null) {
                            Toast.makeText(IvoFeedActivity.this,"current location is null", Toast.LENGTH_LONG).show();
                            return null;
                        }

                        ParseQuery<IVO_DB_POST> query = IVO_DB_POST.getQuery();
                        query.include("user");
                        query.orderByDescending("createdAt");
                        query.whereWithinKilometers("location", geoPointFromLocation(currentLocation), 500);
                        query.setLimit(20);
                        return query;
                    }
                };

        IvoFeedQueryAdapter = new ParseQueryAdapter<IVO_DB_POST>(this, factory) {
            @Override
            public View getItemView(IVO_DB_POST post, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.ivo_post_item, null);
                    Toast.makeText(IvoFeedActivity.this,"Inflating a view...", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(IvoFeedActivity.this,"Setting up stuff...", Toast.LENGTH_LONG).show();
                TextView contentView = (TextView) view.findViewById(R.id.content_view);
                TextView usernameView = (TextView) view.findViewById(R.id.username_view);
                contentView.setText(post.getTextEntry());
                usernameView.setText(post.getUser().getUsername());
                return view;
            }
        };

        // Disable automatic loading when the adapter is attached to a view.
        IvoFeedQueryAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        IvoFeedQueryAdapter.setPaginationEnabled(false);

        // Attach the query adapter to the view
        ListView postsListView = (ListView) findViewById(R.id.ivo_feed_list);
        postsListView.setAdapter(IvoFeedQueryAdapter);

        doListQuery();
    }

    private void doListQuery() {

        if (currentLocation != null) {
            Toast.makeText(this,"Doing Query on list!", Toast.LENGTH_LONG).show();
            IvoFeedQueryAdapter.loadObjects();
            Toast.makeText(this,"objects should be loaded", Toast.LENGTH_LONG).show();
        }
    }

    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(this,"Being Resume...", Toast.LENGTH_LONG).show();
        doListQuery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ivo_feed, menu);
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
}
