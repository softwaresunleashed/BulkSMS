package com.unleashed.android.helpers.usermanager;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sudhanshu on 12/11/17.
 */

@IgnoreExtraProperties
public class UserRecord {

    public String username;
    public String email;

    public UserRecord() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserRecord(String username, String email) {
        this.username = username;
        this.email = email;
    }



}
