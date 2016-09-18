package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.networkops.Connectivity;
import com.unleashed.android.helpers.trackers.Trackers;


public class SocialLoginFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = SocialLoginFragment.class.getSimpleName();
    private static final String LAUNCH_SOURCE = "launch_source";
    private static final int FACEBOOK_LOGIN_RETRY = 1;
    private static final int GOOGLE_LOGIN_RETRY = 2;
//    private static final int RC_SIGN_IN = 123;
    private static final int LOADER_GOOGLE_LOGIN = 1;
//    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog progressDialog;
    private String mLaunchSource = "";

    private Button fbBtn;
    private Button googleLoginBtn;
    private Button loginBtn;
    private Button phoneRegisterBtn;
    private TextView tv_tnc;
    private Snackbar mInfoSnackbar;
    private LinearLayout parentContainer;

    private SocialLoginListener mSocialLoginListener;


//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;


    public static SocialLoginFragment newInstance(String source) {
        SocialLoginFragment fragment = new SocialLoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LAUNCH_SOURCE, source);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social_login, container, false);

        parentContainer = (LinearLayout) view.findViewById(R.id.parent_container);
        fbBtn = (Button) view.findViewById(R.id.btn_fb);
        googleLoginBtn = (Button) view.findViewById(R.id.btn_google);
        loginBtn = (Button) view.findViewById(R.id.btn_legacy_login);
        phoneRegisterBtn = (Button) view.findViewById(R.id.btn_phone_register);
        tv_tnc = (TextView) view.findViewById(R.id.tv_tnc);

        fbBtn.setOnClickListener(this);
        googleLoginBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        phoneRegisterBtn.setOnClickListener(this);
        tv_tnc.setOnClickListener(this);

        //buildGoogleLoginClient();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null)
            mLaunchSource = getArguments().getString(LAUNCH_SOURCE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        getActivity().supportInvalidateOptionsMenu();

//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Logger.push(Logger.LogType.LOG_DEBUG, TAG + "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Logger.push(Logger.LogType.LOG_DEBUG, TAG +"onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof SocialLoginListener)
            mSocialLoginListener = (SocialLoginListener) getParentFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fb:
                performFacebookLogin();
                break;
            case R.id.btn_google:
                performGoogleLogin();
                break;
            case R.id.btn_legacy_login:
                startLoginActivity();
                break;
            case R.id.btn_phone_register:
                startRegisterActivity();
                break;
            case R.id.tv_tnc:
                //displayWeb("rules", getString(R.string.term_of_use));
                break;
        }
    }

//    private static boolean isGoogleLoginClientInitialized = false;
//    private void buildGoogleLoginClient() {
//
//        if(isGoogleLoginClientInitialized == true)
//            return;
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestServerAuthCode(getString(R.string.GOOGLEPLUS_WEBCLIENT_ID), false)
//                .requestIdToken(getString(R.string.GOOGLEPLUS_WEBCLIENT_ID))
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        isGoogleLoginClientInitialized = true;
//    }

    public void googleSignIn() {

        Intent intent = new Intent(getActivity(), GooglePlusLoginActivity.class);
        startActivityForResult(intent, GooglePlusLoginActivity.REQUEST_CODE);

        Trackers.trackEvent(Trackers.EVENT_GP_CONNECT_TAP);


        //buildGoogleLoginClient();

//        showProgress();
//
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    public void googleSignOut() {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//
//                    }
//                });
//    }

    private void performGoogleLogin() {
        if (Connectivity.isConnected(getActivity())) {
            googleSignIn();
        } else {
            showSnackBar(getString(R.string.no_internet_new), GOOGLE_LOGIN_RETRY);
        }
    }

//    private void handleSignInResult(GoogleSignInResult result) {
//        if (result.isSuccess()) {
//            GoogleSignInAccount acct = result.getSignInAccount();
//            firebaseAuthWithGoogle(acct);
//            String authcode = acct.getServerAuthCode();
//            showProgress();
//            //startgoogleLoginLoader(authcode);
//
//            setResultandFinish();
//        } else {
//            //ToastUtil.show(this, R.string.login_error);
//        }
//
//    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Logger.push(Logger.LogType.LOG_DEBUG, TAG + " firebaseAuthWithGoogle:" + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Logger.push(Logger.LogType.LOG_DEBUG, TAG + " signInWithCredential:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Logger.push(Logger.LogType.LOG_WARNING, TAG + " signInWithCredential " + task.getException());
//                            // TODO: Toast.makeText(GoogleSignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void showProgress() {
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.logging_progress), true, true);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !getActivity().isFinishing()) {
            progressDialog.dismiss();
        }
    }

//    private void startgoogleLoginLoader(String token) {
//        Bundle bundle = new Bundle();
//        bundle.putString("token", token);
//        getLoaderManager().restartLoader(LOADER_GOOGLE_LOGIN, bundle, googleLoginCallback);
//    }

//    LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>> googleLoginCallback =
//            new LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>>() {
//
//                @Override
//                public Loader<TaskResponse<LoginResponse>> onCreateLoader(int i, Bundle bundle) {
//                    String token = bundle.getString("token");
//                    return new GoogleLoginLoader(getActivity(), token);
//                }
//
//                @Override
//                public void onLoadFinished(Loader<TaskResponse<LoginResponse>> loader, TaskResponse<LoginResponse> data) {
//                    hideProgress();
//                    if (data != null) {
//                        BottomBarManager.setBottomBarState(data.data.getUserMetaData(), getContext());
//                        handleGoogleLoginResponse(data.data);
//                    } else {
//                        googleSignOut();
//                        showSnackBar(getString(R.string.no_internet_new), GOOGLE_LOGIN_RETRY);
//                    }
//                    getLoaderManager().destroyLoader(loader.getId());
//
//                }
//
//                @Override
//                public void onLoaderReset(Loader<TaskResponse<LoginResponse>> taskResponseLoader) {
//
//                }
//            };
//
//    private void handleGoogleLoginResponse(LoginResponse data) {
//        if (data != null) {
//            if (data.isSucceeded()) {
//                UserNameManager.setUserData(data);
//                setResultandFinish();
//                trackMixPanelEvent(MixpanelEventName.EVENT_LOGIN_SUCCESS);
//            } else {
//                handleGoogleLoginError(data);
//            }
//        } else {
//            googleSignOut();
//            showSnackBar(getString(R.string.no_internet_new), GOOGLE_LOGIN_RETRY);
//        }
//    }

    private void setResultandFinish() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }



//    private void handleGoogleLoginError(LoginResponse data) {
//        //Trackers.trackEvent(getActivity(), Trackers.EVENT_GP_LOGIN_ERROR);
//        googleSignOut();
//        Map<String, Object> formErrors = data.getFormErrors();
//        if (formErrors != null && formErrors.size() > 0) {
//            Set<String> keyset = formErrors.keySet();
//            Iterator<String> it = keyset.iterator();
//
//            while (it.hasNext()) {
//                String key = it.next();
//                ToastUtil.show(getActivity(), formErrors.get(key).toString());
//                break;
//            }
//        }
//    }

    private void performFacebookLogin() {
        if (Connectivity.isConnected(getActivity()))
            startFacebookLogin();
        else
            showSnackBar(getString(R.string.no_internet_new), FACEBOOK_LOGIN_RETRY);
    }
//
//    private void displayWeb(String url, String title) {
//        String displayUrl = Config.getBaseUrl() + url;
//        Logger.push(Logger.LogType.LOG_DEBUG, TAG, "Displaying url: " + displayUrl);
//        WebViewActivity.startWebViewActivity(getActivity(), displayUrl, title);
//    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().supportInvalidateOptionsMenu();
    }

    private void startLoginActivity() {
        SignInTabActivity.startSignInActivityForResult(this);
        //Trackers.trackEvent(getActivity(), Trackers.EVENT_LOGIN_NOW_TAP);
    }

    private void startRegisterActivity() {
        //RegisterActivity.startActivityForResult(this, mLaunchSource);
        //Trackers.trackEvent(getActivity(), Trackers.EVENT_MOBILE_REGISTER_TAP);
    }

    private void startFacebookLogin() {
        Intent intent = new Intent(getActivity(), FacebookLoginActivity.class);
        startActivityForResult(intent, FacebookLoginActivity.REQUEST_CODE);

        Trackers.trackEvent(Trackers.EVENT_FB_CONNECT_TAP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == FacebookLoginActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            if (data != null && data.hasExtra(FacebookLoginActivity.FB_TOKEN)) {
//                if (!TextUtils.isEmpty(FacebookLoginActivity.FB_TOKEN))
//                    informLoginSuccess();
//            } else {
//                showSnackBar(getString(R.string.something_not_right), FACEBOOK_LOGIN_RETRY);
//            }
//        } else if (requestCode == RegisterActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            informLoginSuccess();
//        } else if (requestCode == SignInTabActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            informLoginSuccess();
//        }
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//            hideProgress();
//        }
    }

    private void informLoginSuccess() {

        if (getParentFragment() instanceof SocialLoginListener) {
            mSocialLoginListener = (SocialLoginListener) getParentFragment();
            mSocialLoginListener.onSocialLoginSuccess();
        }

        if (getActivity() instanceof SocialLoginListener) {
            mSocialLoginListener = (SocialLoginListener) getActivity();
            mSocialLoginListener.onSocialLoginSuccess();
        }

    }

    private void showSnackBar(String message, final int retryAction) {

        mInfoSnackbar = Snackbar.make(parentContainer, message, Snackbar.LENGTH_INDEFINITE);

        mInfoSnackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (retryAction) {
                    case FACEBOOK_LOGIN_RETRY:
                        performFacebookLogin();
                        break;
                    case GOOGLE_LOGIN_RETRY:
                        performGoogleLogin();
                        break;
                }
            }

        });

        mInfoSnackbar.setActionTextColor(getResources().getColor(R.color.material_amber_900));
        View sbView = mInfoSnackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        mInfoSnackbar.show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public interface SocialLoginListener {
        void onSocialLoginSuccess();
    }
}
