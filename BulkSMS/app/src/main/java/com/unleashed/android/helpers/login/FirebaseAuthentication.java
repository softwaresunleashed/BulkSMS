package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.unleashed.android.helpers.logger.Logger;

/**
 * Created by Sudhanshu on 28-09-2016.
 */
public class FirebaseAuthentication implements  AuthStateListener{
    private static final String TAG = FirebaseAuthentication.class.getSimpleName();

    private static FirebaseAuthentication firebaseAuthInstance = new FirebaseAuthentication();

    private FirebaseAuth mAuth = null;
    private FirebaseUser mUser = null;


    private FirebaseAuthentication() {
    }

    public static FirebaseAuthentication getInstance() {
        setAuth();
        return firebaseAuthInstance;
    }

    public static void addAuthStateListener(@NonNull final AuthStateListener listener){
        firebaseAuthInstance.getAuth().addAuthStateListener(listener);
    }

    public static void removeAuthStateListener(@NonNull final AuthStateListener listener){
        firebaseAuthInstance.getAuth().addAuthStateListener(listener);
    }

    public static FirebaseAuth getAuth() {
        return firebaseAuthInstance.mAuth;
    }

    private static void setAuth() {
        if(firebaseAuthInstance.mAuth == null){
            firebaseAuthInstance.mAuth = FirebaseAuth.getInstance();
        }

    }

    // Email Login
    @NonNull
    public static Task<AuthResult> signInWithEmailAndPassword(Context activityContext, @NonNull String email, @NonNull String password, OnCompleteListener<AuthResult> onCompleteListener) {
        return firebaseAuthInstance.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) activityContext, onCompleteListener);
    }

    // Email User Creation
    @NonNull
    public static Task<AuthResult> createUserWithEmailAndPassword(Context activityContext, @NonNull String email, @NonNull String password, OnCompleteListener<AuthResult> onCompleteListener) {
        return firebaseAuthInstance.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) activityContext, onCompleteListener);
    }

    // Login with credentials
    @NonNull
    public static Task<AuthResult> signInWithCredential(Context activityContext, @NonNull AuthCredential credential, @NonNull OnCompleteListener<AuthResult> onCompleteListener) {
        return firebaseAuthInstance.mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) activityContext, onCompleteListener);
    }

    public static void signOut(){
        // TODO: Implement logout
        firebaseAuthInstance.mAuth.signOut();
    }

    public static FirebaseUser getUser() {
        return firebaseAuthInstance.mUser;
    }

    public static void setUser(FirebaseUser firebaseUser) {
        firebaseAuthInstance.mUser = firebaseUser;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            setUser(user);
            Logger.push(Logger.LogType.LOG_DEBUG, TAG + " FirebaseAuthentication::onAuthStateChanged:signed_in:" + user.getUid());
        } else {
            // User is signed out
            setUser(null);
            Logger.push(Logger.LogType.LOG_DEBUG, TAG + " FirebaseAuthentication::onAuthStateChanged:signed_out");
        }
    }
}
