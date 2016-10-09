package com.unleashed.android.helpers.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Utils.ToastUtil;
import com.unleashed.android.helpers.activities.BaseActivity;
import com.unleashed.android.helpers.constants.Constants;
import com.unleashed.android.helpers.logger.Logger;
import com.unleashed.android.helpers.trackers.TrackerEvents;
import com.unleashed.android.helpers.trackers.Trackers;
import com.unleashed.android.helpers.usermanager.UserNameManager;

import java.util.Arrays;
import java.util.List;


public class FacebookLoginActivity extends BaseActivity {

    public static final int REQUEST_CODE = Constants.FACEBOOK_REQUEST_CODE;
    public static final String FB_TOKEN = "fb_token";
    public static final String FB_USERNAME = "fb_name";
    public static final String FB_EMAIL = "fb_email";
    public static final String FB_USERPHONE = "fb_phone";
    public static final String FB_USERPHONE_CONFIRMED = "fb_phone_confirmed";
    public static final String FB_LOGIN_ERROR = "fb_login_error";
    public static final String SHOWING_SNACK_BAR = "showing_snack_bar";

    private static final List<String> FB_PERMISSIONS = Arrays.asList("user_friends", "email", "public_profile");
    private static final String TAG = FacebookLoginActivity.class.getSimpleName();
    private static final int LOADER_FB_SESSION = 2;
    private static final String ARGS_LAUNCH_SOURCE = "launchsource";

    private String mUserName;
    private String mEmail;
    private String mPhone;
    private String mError;
    private boolean mPhoneConfirmed;
    private String mLaunchSource;
    private boolean mIsShowingSnackBar;

    private ProgressDialog progressDialog;
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    // Firebase related handlers
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " FacebookLoginActivity::onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " FacebookLoginActivity::onAuthStateChanged:signed_out");
            }
        }
    };


    public static void callFacebookLogout(Context context) {
        if (LoginManager.getInstance() != null)
            LoginManager.getInstance().logOut();

        // Also call firebase signout here
        FirebaseAuthentication.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First things first, Initialize FB SDK before we begin any of the other transactions.
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_fb_login);

        // Get Firebase Auth handler from Firebase single instance
         mAuth = FirebaseAuthentication.getInstance().getAuth();

        initFacebookButton();
        performFacebookLogin();

    }

    public static final void startFacebookLoginActivityResult(Fragment fragment, String source) {
        Intent intent = new Intent(fragment.getActivity(), FacebookLoginActivity.class);
        intent.putExtra(ARGS_LAUNCH_SOURCE, source);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    public static final void startFacebookLoginActivityResult(Fragment fragment, String source, boolean showSnackBar) {
        Intent intent = new Intent(fragment.getActivity(), FacebookLoginActivity.class);
        intent.putExtra(ARGS_LAUNCH_SOURCE, source);
        intent.putExtra(FacebookLoginActivity.SHOWING_SNACK_BAR, showSnackBar);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Register for Firebase User Session Callbacks
        FirebaseAuthentication.addAuthStateListener(mAuthListener);
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Un-Register for Firebase User Session Callbacks
        if (mAuthListener != null) {
            FirebaseAuthentication.removeAuthStateListener(mAuthListener);
//            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void performFacebookLogin() {
        if (!UserNameManager.isFacebookAccount()) {
            //showProgress();
            mLoginButton.performClick();
        } else {
            ToastUtil.show(this, R.string.already_facebook_loggedin);
            setResultAndFinish();
        }

        Trackers.trackEvent(Trackers.EVENT_FB_CONNECT_TAP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
            finish();
    }

    private void initFacebookButton() {

        mCallbackManager = CallbackManager.Factory.create();

        mLoginButton = (LoginButton) findViewById(R.id.btnFacebookLogin);
        mLoginButton.setReadPermissions(FB_PERMISSIONS);

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " facebook:onSuccess():" + loginResult);
                Trackers.trackEvent(TrackerEvents.EVENT_FB_LOGIN_SUCCESS);

                handleFacebookAccessToken(loginResult.getAccessToken());
                //hideProgress();

            }

            @Override
            public void onCancel() {
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " facebook:onCancel()");
                Trackers.trackEvent(TrackerEvents.EVENT_FB_LOGIN_DISMISS);

//                hideProgress();
                showToast(getResources().getString(R.string.login_cancel));
                setResultAndFinish();

            }

            @Override
            public void onError(FacebookException exception) {
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " facebook:onError():" + exception.toString());
                Trackers.trackEvent(TrackerEvents.EVENT_FB_LOGIN_FAILURE);

//                hideProgress();
                showToast(getResources().getString(R.string.login_error));
                setResultAndFinish();
            }
        });

    }

    private OnCompleteListener<AuthResult> credentialFacebookLoginOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Logger.push(Logger.LogType.LOG_DEBUG, TAG + " signInWithCredential:onComplete:" + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                Logger.push(Logger.LogType.LOG_WARNING, TAG + " signInWithCredential : " +  task.getException());
                Toast.makeText(FacebookLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void handleFacebookAccessToken(AccessToken token) {
        Logger.push(Logger.LogType.LOG_DEBUG, TAG + " handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuthentication.signInWithCredential(this, credential, credentialFacebookLoginOnCompleteListener);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, credentialFacebookLoginOnCompleteListener);
    }

//    private void loginWithFacebook() {
//        //getSupportLoaderManager().initLoader(LOADER_FB_SESSION, null, loginCallback);
//    }

//    private AsyncTaskLoader createFacebookSessionLoginLoader() {
//        String token = AccessToken.getCurrentAccessToken().getToken();
//        return new FacebookSessionLoginLoader(this, token, FacebookLoginWidgetFragment.MY_ACCOUNT_LAUNCH.equals(mLaunchSource) ? 1 : 0);
//    }
//
//    LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>> loginCallback =
//            new LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>>() {
//
//                @Override
//                public Loader<TaskResponse<LoginResponse>> onCreateLoader(int id, Bundle args) {
//                    Loader loader = null;
//                    showProgress();
//                    switch (id) {
//                        case LOADER_FB_SESSION:
//                            loader = createFacebookSessionLoginLoader();
//                            break;
//                    }
//                    return loader;
//                }
//
//                @Override
//                public void onLoadFinished(Loader<TaskResponse<LoginResponse>> loader, TaskResponse<LoginResponse> data) {
//
//                    hideProgress();
//                    if (data != null && data.data != null) {
//                        if (LoginResponse.ERROR.equals(data.data.getStatus())) {
//                            handleLoginError(data.data);
//                        } else {
//                            switch (loader.getId()) {
//                                case LOADER_FB_SESSION:
//                                    Apptimize.track(FacebookLoginActivity.FB_LOGIN_SUCCESS);
//                                    parseLoginResponse(data);
//                                    getSupportLoaderManager().destroyLoader(LOADER_FB_SESSION);
//                                    break;
//                            }
//                        }
//                    } else {
//                        ToastUtil.show(FacebookLoginActivity.this,R.string.something_not_right);
//                        trackMixPanelEvent(MixpanelEventName.EVENT_FB_LOGIN_ERROR, mLaunchSource);
//                        callFacebookLogout(FacebookLoginActivity.this);
//                    }
//                    setResultAndFinish();
//                }
//
//                @Override
//                public void onLoaderReset(Loader<TaskResponse<LoginResponse>> loader) {
//                }
//            };
//
//    private void parseLoginResponse(TaskResponse<LoginResponse> data) {
//        if (LoginResponse.OK.equals(data.data.getStatus())) {
//
//            Toast.makeText(FacebookLoginActivity.this, getResources().getString(R.string.logging_success), Toast.LENGTH_SHORT).show();
//            mUserName = data.data.getName();
//            mEmail = data.data.getEmail();
//            mPhone = data.data.getPhone();
//            mPhoneConfirmed = data.data.getPhoneConfirmed();
//
//            UserNameManager.setUserId(data.data.getUserId());
//
//            if (!TextUtils.isEmpty(mUserName))
//                UserNameManager.setUsername(mUserName);
//
//            if (!TextUtils.isEmpty(mEmail))
//                UserNameManager.setUserEmail(mEmail);
//
//            if (!TextUtils.isEmpty(mPhone))
//                UserNameManager.setUserPhone(mPhone);
//
//            BottomBarManager.setBottomBarState(data.data.getUserMetaData(), this);
//
//            HashMap<String, String> params = preparePageParams(true);
//            Trackers.trackView(this, Trackers.VIEW_FB_LOGIN, params);
//            Trackers.trackEvent(this, Trackers.EVENT_FB_LOGIN_SUCCESS, null, params);
//
//            setLoginSuperProperty();
//            trackMixPanelEvent(MixpanelEventName.EVENT_LOGIN_SUCCESS, mLaunchSource);
//
//
//            MAT.trackEvent(this, MAT.LOGIN);
//            Config.isLogged = true;
//        } else {
//            trackMixPanelEvent(MixpanelEventName.EVENT_FB_LOGIN_ERROR, mLaunchSource);
//        }
//    }

//    private void setLoginSuperProperty() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put(MixpanelPropertyName.Super.EVER_LOGGED_IN, "true");
//        Mixpanel.setSuperPropertyOnce(this, map);
//    }

//    private void trackMixPanelEvent(String event, String source) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put(MixpanelPropertyName.SOURCE, source);
//        Mixpanel.track(this, event, map);
//    }


//    private void handleLoginError(LoginResponse data) {
//
//        callFacebookLogout(FacebookLoginActivity.this);
//        trackFacebookLoginFailure();
//
//        Map<String, Object> formErrors = data.getFormErrors();
//        if (formErrors != null && formErrors.size() > 0) {
//            Set<String> keyset = formErrors.keySet();
//
//            Iterator<String> it = keyset.iterator();
//            while (it.hasNext()) {
//                String key = it.next();
//                if ("permission".equals(key)) {
//                    showToast(formErrors.get(key).toString());
//                } else {
//                    showToast(formErrors.get(key).toString());
//                }
//            }
//        }
//        trackMixPanelEvent(MixpanelEventName.EVENT_FB_LOGIN_ERROR, mLaunchSource);
//    }



    private void showProgress() {
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.logging_progress), true, false);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !isFinishing()) {
            progressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        mError = message;
        if (!mIsShowingSnackBar)
            Toast.makeText(FacebookLoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    private void setResultAndFinish() {
        Intent result = new Intent();
        if (AccessToken.getCurrentAccessToken() != null)
            result.putExtra(FB_TOKEN, AccessToken.getCurrentAccessToken().getToken());

        result.putExtra(FB_LOGIN_ERROR, mError);
        result.putExtra(FB_USERNAME, mUserName);
        result.putExtra(FB_EMAIL, mEmail);
        result.putExtra(FB_USERPHONE, mPhone);
        result.putExtra(FB_USERPHONE_CONFIRMED, mPhoneConfirmed);
        setResult(RESULT_OK, result);
        finish();
    }



}
