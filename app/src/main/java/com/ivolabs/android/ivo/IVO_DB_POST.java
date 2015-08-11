package com.ivolabs.android.ivo;

/**
 * Created by Brennan on 7/28/15.
 */

import com.parse.ParseClassName;
import com.parse.ParseFile;
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

    public String getUserName() {
        return getString("userName");
    }

    public void setUserName(String value) {
        put("userName", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("geoLocation");
    }

    public void setLocation(ParseGeoPoint value) {
        put("geoLocation", value);
    }

    public void setVoteCount(int value) {
        put("votes", value);
    }

    public int getVoteCount() {
        return getInt("votes");
    }

    public void setCategory(String value) {
        put("category", value);
    }

    public ParseFile getPictureEntry() {
        return getParseFile("pictureEntry");
    }

    public void setPictureEntry(ParseFile file) {
        put("pictureEntry", file);
    }

    public String getCategory() {
        return getString("category");
    }
    public static ParseQuery<IVO_DB_POST> getQuery() {
        return ParseQuery.getQuery(IVO_DB_POST.class);
    }
}
