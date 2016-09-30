package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.constants.Constants;
import com.unleashed.android.helpers.logger.Logger;
import com.unleashed.android.helpers.networkops.Connectivity;
import com.unleashed.android.helpers.trackers.Trackers;




public class EmailSignInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = EmailSignInFragment.class.getSimpleName();

    public static final int REQUEST_CODE = Constants.EMAILLOGIN_REQUEST_CODE;

    private static final int LOADER_EMAIL_LOGIN = 1;
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;

    private TextView tv_forgot_password;
    private TextView tv_register_new_user;
    private TextView tv_login;
    private Snackbar mInfoSnackbar;
    private FrameLayout parentLayout;

    private String mLaunchSource;
    private ProgressDialog progressDialog;

    // Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " EmailSignIn::onAuthStateChanged:signed_in: " + user.getUid());
            } else {
                // User is signed out
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " EmailSignIn::onAuthStateChanged:signed_out");
            }
            // [START_EXCLUDE]
            //updateUI(user);
            // [END_EXCLUDE]
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuthentication.addAuthStateListener(mAuthListener);
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
            FirebaseAuthentication.removeAuthStateListener(mAuthListener);
        }
    }

    public static Fragment newInstance() {
        Fragment emailSignInFragment = new EmailSignInFragment();
        return emailSignInFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sudhanshu ORIG
        //View view = inflater.inflate(R.layout.fragment_email_signin, container, false);
        View view = inflater.inflate(R.layout.fragment_email_signin, null, false);
        parentLayout = (FrameLayout) view.findViewById(R.id.parent_container);

        edtEmail = (TextInputEditText) view.findViewById(R.id.edt_email);
        edtEmail.setHint(getString(R.string.email));

        edtPassword = (TextInputEditText) view.findViewById(R.id.edt_password);
        edtPassword.setHint(getString(R.string.password));

        tv_forgot_password = (TextView) view.findViewById(R.id.tv_forgot_password);
        tv_forgot_password.setOnClickListener(this);

        tv_register_new_user = (TextView) view.findViewById(R.id.tv_register_newuser);
        tv_register_new_user.setOnClickListener(this);

        tv_login = (TextView) view.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);


        // Get Firebase Auth handler from Firebase single instance
        mAuth = FirebaseAuthentication.getInstance().getAuth();

        // Set Title of Login via Email
        this.getActivity().setTitle(getResources().getString(R.string.title_activity_emaillogin));

        return view;
    }

    public void resetViews() {
        if (edtPassword != null) {
            edtPassword.clearFocus();
            //edtPassword.hideError();
        }

        if (edtEmail != null) {
            edtEmail.clearFocus();
            //edtEmail.hideError();
        }

        hideSnackBar();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser)
            resetViews();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_forgot_password:
                startForgotPasswordActivity();
                break;
            case R.id.tv_login:
                startLogin();
                break;
            case R.id.tv_register_newuser:
                registerUser();
                break;
        }

    }

    private void startLogin() {
        hideKeyboard();
        if (Connectivity.isConnected(getActivity()))
            performEmailLogin();
        else
            showSnackbarMessage(getString(R.string.no_internet_new), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startLogin();
                }
            });
    }

    private void registerUser() {
        hideKeyboard();
        if (Connectivity.isConnected(getActivity()))
            registerNewUser();
        else
            showSnackbarMessage(getString(R.string.no_internet_new), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();
                }
            });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && edtPassword != null)
            imm.hideSoftInputFromWindow(edtPassword.getWindowToken(), 0);
    }

    private void hideSnackBar() {
        if (mInfoSnackbar != null && mInfoSnackbar.isShown())
            mInfoSnackbar.dismiss();
    }

    private void startForgotPasswordActivity() {
        //ForgotPasswordActivity.startActivityForResult(getActivity(), mLaunchSource);
        //Trackers.trackEvent(getActivity(), Trackers.EVENT_FORGOT_PASSWORD_TAP);
    }


    private OnCompleteListener<AuthResult> emailLoginOnCompleteListener  = new OnCompleteListener<AuthResult>(){

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Logger.push(Logger.LogType.LOG_DEBUG, TAG + " mAuth.signInWithEmailAndPassword():onComplete: " + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + "signInWithEmailAndPassword():failed exception" + task.getException().toString());
                Toast.makeText(SUApplication.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                showSnackbarMessage(task.getException().getMessage(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissSnackbarMessage();
                    }
                });
            } else {
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + "signInWithEmailAndPassword():success => " + task.getResult().toString());
                Toast.makeText(SUApplication.getContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                setResultandFinish();
            }

            hideProgress();
        }
    };

    private void performEmailLogout(){
        // Call Firebase specific logout api
        FirebaseAuthentication.signOut();
    }

    private void performEmailLogin() {

        String email = edtEmail.getText().toString().trim();
        String passwd = edtPassword.getText().toString().trim();
        boolean emailIsValid = validateEmailAddress(email);
        boolean passwordIsValid = validatePassword(passwd);

        if (emailIsValid && passwordIsValid) {
            Trackers.trackEvent(Trackers.EVENT_LOGIN_EMAIL_SUBMIT);
            Bundle b = new Bundle();
            b.putString(EMAIL, email);
            b.putString(PASSWORD, passwd);

            showProgress();

            // THIS IS THE SIGNIN METHOD
            FirebaseAuthentication.signInWithEmailAndPassword(this.getActivity(), email, passwd, emailLoginOnCompleteListener);
//            mAuth.signInWithEmailAndPassword(email, passwd)
//                    .addOnCompleteListener(this.getActivity(), emailLoginOnCompleteListener);

        } else {
            Logger.push(Logger.LogType.LOG_DEBUG, TAG + "email or password validation failed");
            Toast.makeText(SUApplication.getContext(), R.string.uname_passwd_error, Toast.LENGTH_SHORT).show();
        }

    }


    private OnCompleteListener<AuthResult> registerNewUserOnCompleteListener  = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Logger.push(Logger.LogType.LOG_DEBUG, TAG + " Entered mAuth.createUserWithEmailAndPassword():onComplete = " + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + "createUserWithEmailAndPassword():failed exception => " + task.getException().toString());
                Toast.makeText(SUApplication.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                showSnackbarMessage(task.getException().getMessage(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissSnackbarMessage();
                    }
                });
            } else {
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + "createUserWithEmailAndPassword():success => " + task.getResult().toString());
                Toast.makeText(SUApplication.getContext(), R.string.new_user_registration_success, Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        }
    };

        private void registerNewUser() {
        String email = edtEmail.getText().toString().trim();
        String passwd = edtPassword.getText().toString().trim();
        boolean emailIsValid = validateEmailAddress(email);
        boolean passwordIsValid = validatePassword(passwd);

        if (emailIsValid && passwordIsValid) {
            Trackers.trackEvent(Trackers.EVENT_LOGIN_EMAIL_SUBMIT);
            Bundle b = new Bundle();
            b.putString(EMAIL, email);
            b.putString(PASSWORD, passwd);

            showProgress();

            // THIS IS THE REGISTER METHOD
            FirebaseAuthentication.createUserWithEmailAndPassword(this.getActivity(), email, passwd, registerNewUserOnCompleteListener);
//            mAuth.createUserWithEmailAndPassword(email, passwd)
//                    .addOnCompleteListener(this.getActivity(), registerNewUserOnCompleteListener);
        }
    }

    private boolean validatePassword(String passwd) {
        // TODO : Validate Email Id and Password length . Minimum password length = 6 as per Firebase SDK
        if(passwd.length() == 0)
            return false;

        return true;
    }

    private boolean validateEmailAddress(String email) {
        // TODO : Validate Email Id and Password length . Minimum password length = 6 as per Firebase SDK
        if(email.length() == 0)
            return false;

        return true;
    }

    private void showProgress() {
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.please_wait), true, true);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !getActivity().isFinishing()) {
            progressDialog.dismiss();
        }
    }

    private void showSnackbarMessage(String message, View.OnClickListener _onClickListener) {
        if (parentLayout != null) {
            mInfoSnackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_INDEFINITE);
            mInfoSnackbar.setActionTextColor(getResources().getColor(R.color.orange));
            mInfoSnackbar.setAction(R.string.retry, _onClickListener);
            View sbView = mInfoSnackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            mInfoSnackbar.show();
        }
    }

    private void dismissSnackbarMessage() {
        if(mInfoSnackbar != null){
            mInfoSnackbar.dismiss();
        }
    }

//    private AsyncTaskLoader createLoginLoader(Bundle args) {
//        String email = null, password = null;
//
//        if (args != null) {
//            email = args.getString(EMAIL);
//            password = args.getString(PASSWORD);
//        }
//        return new LoginLoader(getActivity(), email, password);
//    }

//    private void handleLoginResult(TaskResponse<LoginResponse> data) {
//        if (data.data != null) {
//            if (LoginResponse.OK.equals(data.data.getStatus())) {
//                BottomBarManager.setBottomBarState(data.data.getUserMetaData(), getActivity());
//                ToastUtil.show(getActivity(), R.string.login_success_message);
//                UserNameManager.setUserId(data.data.getUserId());
//                UserNameManager.setUsername(data.data.getName());
//                UserNameManager.setUserPhone(data.data.getPhone());
//                UserNameManager.setAlreadyFacebookLogin(data.data.isFacebookUser());
//                setLoginSuperProperty();
////                trackMixPanelEvent(MixpanelEventName.EVENT_LOGIN_SUCCESS);
//                setResultandFinish();
//            } else {
//                handleLoginError(data.data);
//            }
//        } else {
//            showSnackbarMessage(getString(R.string.no_internet_new));
//        }
//    }



    private void setResultandFinish() {
        getActivity().setResult(Activity.RESULT_OK, null);
        getActivity().finish();
    }

//    private void handleLoginError(LoginResponse data) {
//        Map<String, Object> formErrors = data.getFormErrors();
//        if (formErrors != null && formErrors.size() > 0) {
//            Set<String> keyset = formErrors.keySet();
//            String error_type = null;
//            Iterator<String> it = keyset.iterator();
//            while (it.hasNext()) {
//                String key = it.next();
//                if ("password".equals(key)) {
//                    error_type = formErrors.get(key).toString();
//                    edtPassword.showError(error_type);
//                } else if ("email_phone".equals(key) || "login".equals(key)) {
//                    error_type = formErrors.get(key).toString();
//                    edtEmail.showError(error_type);
//                } else if ("permission".equals(key)) {
//                    error_type = formErrors.get(key).toString();
//                    showSnackbarMessage(error_type);
//                } else {
//                    error_type = formErrors.get(key).toString();
//                    showSnackbarMessage(error_type);
//                    break;
//                }
//            }
//        }
//        //Trackers.trackEvent(getActivity(), Trackers.EVENT_EMAIL_LOGIN_ERROR);
//    }

//    LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>> loginCallback =
//            new LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>>() {
//
//                @Override
//                public Loader<TaskResponse<LoginResponse>> onCreateLoader(int id, Bundle args) {
//
//                    showProgress();
//
//                    Loader loader = null;
//
//                    switch (id) {
//                        case LOADER_EMAIL_LOGIN:
//                            loader = createLoginLoader(args);
//                            break;
//                    }
//                    return loader;
//                }
//
//                @Override
//                public void onLoadFinished(Loader<TaskResponse<LoginResponse>> loader, TaskResponse<LoginResponse> data) {
//
//                    if (data.error != null) {
//                        showSnackbarMessage(getString(R.string.no_internet_new));
//                    } else {
//                        switch (loader.getId()) {
//                            case LOADER_EMAIL_LOGIN:
//                                handleLoginResult(data);
//                                break;
//                        }
//                    }
//                    getLoaderManager().destroyLoader(loader.getId());
//                    hideProgress();
//                }
//
//                @Override
//                public void onLoaderReset(Loader<TaskResponse<LoginResponse>> loader) {
//                }
//            };


}
