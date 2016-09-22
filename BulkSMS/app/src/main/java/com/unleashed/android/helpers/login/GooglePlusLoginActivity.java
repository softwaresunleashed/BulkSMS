package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Utils.ToastUtil;
import com.unleashed.android.helpers.activities.BaseActivity;
import com.unleashed.android.helpers.constants.Constants;
import com.unleashed.android.helpers.logger.Logger;
import com.unleashed.android.helpers.trackers.Trackers;
import com.unleashed.android.helpers.usermanager.UserNameManager;




public class GooglePlusLoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = GooglePlusLoginActivity.class.getSimpleName();

    public static final int REQUEST_CODE = Constants.GOOGLEPLUS_REQUEST_CODE;
//    private static final int RC_SIGN_IN = 123;

    private static boolean isGoogleLoginClientInitialized = false;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    // Firebase related handlers
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildGoogleLoginClient();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Logger.push(Logger.LogType.LOG_DEBUG, TAG + " onAuthStateChanged:signed_in: " + user.getUid());
                } else {
                    // User is signed out
                    Logger.push(Logger.LogType.LOG_DEBUG, TAG + " onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        performGooglePlusLogin();
    }

//    public static final void startFacebookLoginActivityResult(Fragment fragment, String source) {
//        Intent intent = new Intent(fragment.getActivity(), GooglePlusLoginActivity.class);
//        intent.putExtra(ARGS_LAUNCH_SOURCE, source);
//        fragment.startActivityForResult(intent, REQUEST_CODE);
//    }
//
//    public static final void startFacebookLoginActivityResult(Fragment fragment, String source, boolean showSnackBar) {
//        Intent intent = new Intent(fragment.getActivity(), GooglePlusLoginActivity.class);
//        intent.putExtra(ARGS_LAUNCH_SOURCE, source);
//        intent.putExtra(GooglePlusLoginActivity.SHOWING_SNACK_BAR, showSnackBar);
//        fragment.startActivityForResult(intent, REQUEST_CODE);
//    }



    @Override
    protected void onStart() {
        super.onStart();

        // Register for Firebase User Session Callbacks
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Un-Register for Firebase User Session Callbacks
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public static void callGooglePlusLogout(Context context) {

        // TODO: Do we need to call google logout sequence here???

        FirebaseAuth.getInstance().signOut();
    }


    public void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private void performGooglePlusLogin() {

        showProgress();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE);

        Trackers.trackEvent(Trackers.EVENT_GP_CONNECT_TAP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED)
            finish();

        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            hideProgress();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            String authcode = acct.getServerAuthCode();
            //showProgress();
            //startgoogleLoginLoader(authcode);

            setResultAndFinish();
        } else {
            ToastUtil.show(this, R.string.login_error);
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Logger.push(Logger.LogType.LOG_DEBUG, TAG + " firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Logger.push(Logger.LogType.LOG_DEBUG, TAG + " signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Logger.push(Logger.LogType.LOG_WARNING, TAG + " signInWithCredential " + task.getException());
                            Toast.makeText(GooglePlusLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setResultAndFinish() {
        this.setResult(Activity.RESULT_OK);
        this.finish();
    }

    private void buildGoogleLoginClient() {

        if(isGoogleLoginClientInitialized == true)
            return;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode(getString(R.string.GOOGLEPLUS_WEBCLIENT_ID), false)
                .requestIdToken(getString(R.string.GOOGLEPLUS_WEBCLIENT_ID))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        isGoogleLoginClientInitialized = true;
    }


    private void showProgress() {
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.logging_progress), true, false);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !isFinishing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.push(Logger.LogType.LOG_DEBUG, TAG + "onConnectionFailed()...result = " + connectionResult.getErrorMessage());
    }

//    private void showToast(String message) {
//        mError = message;
//        if (!mIsShowingSnackBar)
//            Toast.makeText(GooglePlusLoginActivity.this, message, Toast.LENGTH_SHORT).show();
//    }


//    private void setResultAndFinish() {
//        Intent result = new Intent();
//        if (AccessToken.getCurrentAccessToken() != null)
//            result.putExtra(FB_TOKEN, AccessToken.getCurrentAccessToken().getToken());
//
//        result.putExtra(FB_LOGIN_ERROR, mError);
//        result.putExtra(FB_USERNAME, mUserName);
//        result.putExtra(FB_EMAIL, mEmail);
//        result.putExtra(FB_USERPHONE, mPhone);
//        result.putExtra(FB_USERPHONE_CONFIRMED, mPhoneConfirmed);
//        setResult(RESULT_OK, result);
//        finish();
//    }



}
