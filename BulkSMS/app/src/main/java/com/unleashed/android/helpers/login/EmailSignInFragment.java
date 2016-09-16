package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
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
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Connectivity;
import com.unleashed.android.helpers.Logger;
import com.unleashed.android.helpers.trackers.Trackers;
import com.unleashed.android.helpers.widgets.inputs.InputTextEdit;

import static com.unleashed.android.helpers.login.SocialLoginFragment.TAG;


public class EmailSignInFragment extends Fragment implements View.OnClickListener {

    private static final int LOADER_EMAIL_LOGIN = 1;
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private InputTextEdit edtEmail;
    private InputTextEdit edtPassword;

    private TextView tv_forgot_password;
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
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Logger.push(Logger.LogType.LOG_DEBUG, TAG + " onAuthStateChanged:signed_out");
            }
            // [START_EXCLUDE]
            //updateUI(user);
            // [END_EXCLUDE]
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
        edtEmail = (InputTextEdit) view.findViewById(R.id.edt_email);
        edtPassword = (InputTextEdit) view.findViewById(R.id.edt_password);
        tv_forgot_password = (TextView) view.findViewById(R.id.tv_forgot_password);
        tv_login = (TextView) view.findViewById(R.id.tv_login);

        //edtEmail.setValidator(PostadValidators.getEmailValidator(getActivity()));
        edtEmail.setInputType(InputTextEdit.InputMethod.EMAIL);
        edtEmail.setHint(getString(R.string.email));

        edtPassword.setInputType(InputTextEdit.InputMethod.EMAIL);
        edtPassword.getView().setTransformationMethod(new PasswordTransformationMethod());
        //edtPassword.setValidator(PostadValidators.getPasswordValidator(getActivity()));
        edtPassword.setHint(getString(R.string.password));
        edtPassword.setIsPasswordUI(true);

        tv_forgot_password.setOnClickListener(this);
        tv_login.setOnClickListener(this);

        // Get Firebase Auth handler
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    public void resetViews() {
        if (edtPassword != null) {
            edtPassword.clearFocus();
            edtPassword.hideError();
        }

        if (edtEmail != null) {
            edtEmail.clearFocus();
            edtEmail.hideError();
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
        }

    }

    private void startLogin() {
        hideKeyboard();
        if (Connectivity.isConnected(getActivity()))
            performEmailLogin();
        else
            showSnackbarMessage(getString(R.string.no_internet_new));
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

    private void performEmailLogin() {

        String email = edtEmail.getValue().trim();
        String passwd = edtPassword.getValue().trim();

        boolean emailIsvalid = edtEmail.validate();
        boolean passwordIsValid = edtPassword.validate();

        if (emailIsvalid && passwordIsValid) {
            Trackers.trackEvent(Trackers.EVENT_LOGIN_EMAIL_SUBMIT);
            Bundle b = new Bundle();
            b.putString(EMAIL, email);
            b.putString(PASSWORD, passwd);
            // Sudhanshu
            //getLoaderManager().restartLoader(LOADER_EMAIL_LOGIN, b, loginCallback);

            showProgress();

            mAuth.createUserWithEmailAndPassword(email, passwd)
                    .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Logger.push(Logger.LogType.LOG_DEBUG, TAG + " createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
//                            if (!task.isSuccessful()) {
//                                Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
//                            }
                            hideProgress();
                        }
                    });




        }

    }

    private void showProgress() {
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.please_wait), true, true);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !getActivity().isFinishing()) {
            progressDialog.dismiss();
        }
    }

    private void showSnackbarMessage(String message) {
        if (parentLayout != null) {
            mInfoSnackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_INDEFINITE);
            mInfoSnackbar.setActionTextColor(getResources().getColor(R.color.orange));
            mInfoSnackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLogin();
                }
            });
            View sbView = mInfoSnackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            mInfoSnackbar.show();
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
