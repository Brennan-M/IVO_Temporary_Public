package com.ivolabs.android.ivo;

/**
 * Created by Brennan on 7/28/15.
 */

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Data model for a post to our IVO Database.
 */
@ParseClassName("IVO_DB")
public class IVO_DB_POST extends ParseObject {

    public String getTextEntry() {
        return getString("textEntry");
    }

    public void setTextEntry(String value) {
        put("textEntry", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public static ParseQuery<IVO_DB_POST> getQuery() {
        return ParseQuery.getQuery(IVO_DB_POST.class);
    }
}
