package com.unleashed.android.helpers.dbhelper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unleashed.android.helpers.usermanager.UserRecord;

/**
 * Created by sudhanshu on 12/11/17.
 */

public class FirebaseDatabaseWrapper {



    private DatabaseReference mDatabase;

    public static final String TAG = FirebaseDatabaseWrapper.class.getSimpleName();

    public FirebaseDatabaseWrapper() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }





    public void writeNewUser(String userId, String name, String email) {
        UserRecord user = new UserRecord(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    public void updateExistingUser(String userId, String name){

        mDatabase.child("users").child(userId).child("username").setValue(name);
    }

}
