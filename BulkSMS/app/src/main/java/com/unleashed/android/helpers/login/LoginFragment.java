package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class LoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = LoginFragment.class.getSimpleName();
    public static final String LOGIN_SOURCE = "login_source";
    private static final int LOADER_LOGIN = 1;
    private static final int LOADER_FB_SESSION = 2;
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final int FB_LOGIN = 1000;
    private static final int LOADER_LOGIN_OTP = 3;
    private static final String LAUNCH_SOURCE = "launch_source";
    private static final String EMAIL_LOGIN_TYPE = "email_login_type";
    private static final String PHONE_LOGIN_TYPE = "phone_login_type";

    private String login_type;
    private String mLaunchSource;
    protected ActionProcessButton btnSignIn;

    private InputTextEdit edtEmail;
    private InputTextEdit edtPassword;
    private InputTextEdit mPhoneLoginEdt;
    private Button mPhoneLoginBtn;

    private View mFbButton;
    private TextView manageAdviaMail;
    private TextView mSkipTV;
    private String mPhone;
    /**********
     * Progressbar, considering moving this to base Fragment?
     **********/
    private boolean isLoading;
    LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>> loginCallback =
            new LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>>() {

                @Override
                public Loader<TaskResponse<LoginResponse>> onCreateLoader(int id, Bundle args) {
                    isLoading = true;
                    showProgress();

                    Loader loader = null;

                    switch (id) {
                        case LOADER_LOGIN:
                            loader = createLoginLoader(args);
                            break;
                    }

                    return loader;
                }

                @Override
                public void onLoadFinished(Loader<TaskResponse<LoginResponse>> loader, TaskResponse<LoginResponse> data) {

                    if (data.error != null) {
                        toast("Check your connection");
                    } else {
                        switch (loader.getId()) {
                            case LOADER_LOGIN:
                                handleLoginResult(data);
                                break;
                        }
                    }
                    getLoaderManager().destroyLoader(loader.getId());
                    hideProgress();
                    isLoading = false;

                }

                @Override
                public void onLoaderReset(Loader<TaskResponse<LoginResponse>> loader) {
                }
            };

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String source) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LAUNCH_SOURCE, source);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static InputTextEditValidator getPasswordValidator(Context context) {
        return new InputTextEditValidator.Builder()
                .withRequired(context.getString(R.string.validation_field_required))
                .withMinLength(3, String.format(context.getString(R.string.validation_min_length), 3))
                .build();

    }

    public static InputTextEditValidator getNonEmptyValidator(Context context) {
        return new InputTextEditValidator.Builder()
                .withRequired(context.getString(R.string.validation_field_required))
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mLaunchSource = getArguments().getString(LAUNCH_SOURCE);
        }

        if (savedInstanceState != null) {
            isLoading = savedInstanceState.getBoolean("isLoading");
        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        btnSignIn = (ActionProcessButton) v.findViewById(R.id.btnLogInNew);

        //decideSkipVisibility();

        isLoading = false;
        hideProgress();

        handleClick(btnSignIn);
        handleClick(v.findViewById(R.id.txtForgetPassword));
        handleClick(v.findViewById(R.id.txtTou));

        edtEmail = (InputTextEdit) v.findViewById(R.id.edtEmailPhone);
        edtPassword = (InputTextEdit) v.findViewById(R.id.edtPassword);
        mPhoneLoginEdt = (InputTextEdit) v.findViewById(R.id.edtPhoneOtp);
        mPhoneLoginBtn = (Button) v.findViewById(R.id.btn_otp_login);

        mPhoneLoginEdt.setInputType(InputTextEdit.InputMethod.PHONE);
        //mPhoneLoginEdt.setValidator(PostadValidators.getPhoneValidator(getActivity()));

        edtEmail.setInputType(InputTextEdit.InputMethod.EMAIL);
        edtEmail.setValidator(getNonEmptyValidator(getActivity()));
        edtEmail.setEditTextContentDescription(getString(R.string.username));

        edtPassword.setInputType(InputTextEdit.InputMethod.EMAIL);
        edtPassword.getView().setTransformationMethod(new PasswordTransformationMethod());
        edtPassword.setValidator(getPasswordValidator(getActivity()));
        edtPassword.setEditTextContentDescription(getString(R.string.password));

        edtPassword.getView().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                    return true;
                }

                return false;
            }
        });


        mPhoneLoginEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    mPhone = editable.toString();
                }

                refreshLoginButton(isValidPhoneEntered());
            }
        });

/*        mPhoneLoginEdt.hideErrorIndicator(true);
        edtEmail.hideErrorIndicator(true);
        edtPassword.hideErrorIndicator(true);

        mPhoneLoginEdt.setNestedComponentsMargin(0);
        edtEmail.setNestedComponentsMargin(0);
        edtPassword.setNestedComponentsMargin(0);*/

        mPhoneLoginEdt.setHint(getActivity().getResources().getString(R.string.enter_mobile_number_hint));
        edtEmail.setHint(getActivity().getResources().getString(R.string.email_mobile));
        edtPassword.setHint(getActivity().getResources().getString(R.string.password));

        mPhoneLoginEdt.setStickyTitle(false);
        edtEmail.setStickyTitle(false);
        edtPassword.setStickyTitle(false);

        mPhoneLoginEdt.hideTitleContainer();
        edtEmail.hideTitleContainer();
        edtPassword.hideTitleContainer();

        edtPassword.setIsPasswordUI(true);

        mPhoneLoginBtn.setOnClickListener(this);

        return v;
    }


    private void refreshLoginButton(boolean enabled) {
        mPhoneLoginBtn.setEnabled(enabled);
    }


    private boolean isValidPhoneEntered() {
        return ((mPhone != null) && (mPhone.trim().length() == 10) && TextUtils.isNumeric(mPhone));
    }

    private void decideSkipVisibility() {

        if (mLaunchSource != null && mLaunchSource.contains(MainActivity.HOME_SCREEN)) {
            ApptimizeABTest.MandatoryRegistrationValues value = ApptimizeABTest.getMandatoryRegistrationABTestValues();
            if (value == ApptimizeABTest.MandatoryRegistrationValues.WITH_SKIP)
                mSkipTV.setVisibility(View.VISIBLE);
            else
                mSkipTV.setVisibility(View.GONE);
        }


        if (mLaunchSource != null && (mLaunchSource.contains(PostAdActivity.SOURCE) || mLaunchSource.contains(BaseAdFragment.AD_DETAIL_LAUNCH) || mLaunchSource.contains(AdAdapterWithGallery1.AD_LIST_CHAT_SOURCE))) {
            ApptimizeABTest.MandatoryRegistrationValues value2 = ApptimizeABTest.getMandatoryRegistration2ABTestValues();
            if (value2 == ApptimizeABTest.MandatoryRegistrationValues.WITH_SKIP)
                mSkipTV.setVisibility(View.VISIBLE);
            else
                mSkipTV.setVisibility(View.GONE);
        }

    }

    private void handleClick(View view) {
        view.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isLoading", isLoading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtForgetPassword:
                startForgotPasswordActivity();
                break;
            case R.id.btnLogInNew: {
                CustomerAssistance.clearCurrentChatInstance();
                login();
                hideKeyboard();
            }
            break;
            case R.id.txtTou:
                displayWeb("rules", getString(R.string.term_of_use));
                break;
            case R.id.btn_otp_login: {
                requestLoginOtp();
            }
            break;
        }
    }

    private void requestLoginOtp() {
        getLoaderManager().initLoader(LOADER_LOGIN_OTP, null, mLoginLoaderCallback);
    }

    private LoaderManager.LoaderCallbacks<TaskResponse<OtpRequestResponse>> mLoginLoaderCallback = new LoaderManager.LoaderCallbacks<TaskResponse<OtpRequestResponse>>() {
        @Override
        public Loader<TaskResponse<OtpRequestResponse>> onCreateLoader(int id, Bundle args) {
            return new OTPLoginLoader(getActivity(), mPhone);
        }

        @Override
        public void onLoadFinished(Loader<TaskResponse<OtpRequestResponse>> loader, TaskResponse<OtpRequestResponse> data) {
            if (data != null && data.data != null) {
                if (data.data.isSuccessful()) {
                    hideOtpKeyboard();
                    startPhoneOtpLoginActivtiy(data.data.hash);
                } else {
                    ToastUtil.show(getActivity(), data.data.getErrorMsg());
                }
            } else {
                ToastUtil.show(getActivity(), R.string.error_default);
            }
            getLoaderManager().destroyLoader(LOADER_LOGIN_OTP);
        }


        @Override
        public void onLoaderReset(Loader<TaskResponse<OtpRequestResponse>> loader) {

        }
    };


    private void startPhoneOtpLoginActivtiy(String hash) {
        getActivity().startActivityForResult(OtpVerificationActivity.getIntentForAccountLoginOtp(getActivity(), mPhone, hash), OtpVerificationActivity.REQUEST_CODE);
    }


    private void startForgotPasswordActivity() {
        ForgotPasswordActivity.startActivityForResult(getActivity(), mLaunchSource);
    }

    private void login() {
        String email_phone = edtEmail.getValue().trim();
        String passwd = edtPassword.getValue().trim();

        boolean emailIsvalid = edtEmail.validate();
        boolean passwordIsValid = edtPassword.validate();

        if (emailIsvalid && passwordIsValid) {
            Bundle b = new Bundle();
            b.putString(EMAIL, email_phone);
            b.putString(PASSWORD, passwd);
            getLoaderManager().restartLoader(LOADER_LOGIN, b, loginCallback);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtPassword.getWindowToken(), 0);
    }

    private void hideOtpKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPhoneLoginEdt.getWindowToken(), 0);
    }

    private void displayWeb(String url, String title) {
        String displayUrl = Config.getBaseUrl() + url;
        Logger.push(Logger.LogType.LOG_DEBUG, TAG, "Displaying url: " + displayUrl);
        WebViewActivity.startWebViewActivity(getActivity(), displayUrl, title);
    }

    private void toast(String message) {
        if (isAdded()) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private HashMap<String, String> preparePageParams(String action, String origin) {
        XtraAtInternetParams.Builder paramBuilder = new XtraAtInternetParams.Builder();
        paramBuilder.setUserLocationParams(TablicaApplication.getCurrentParametersController());
        paramBuilder.setOrigin(origin);
        paramBuilder.setActionType(action);
        return paramBuilder.build();
    }

    private AsyncTaskLoader createLoginLoader(Bundle args) {
        String email = null, password = null;

        if (args != null) {
            email = args.getString(EMAIL);
            password = args.getString(PASSWORD);
        }
        return new LoginLoader(getActivity(), email, password);
    }

    private void handleLoginResult(TaskResponse<LoginResponse> data) {
        if (data.data != null) {
            if (LoginResponse.OK.equals(data.data.getStatus())) {
                informAboutLoginSuccess(true);
                UserNameManager.setUserId(data.data.getUserId());
                UserNameManager.setUsername(data.data.getName());
                UserNameManager.setUserPhone(data.data.getPhone());
                UserNameManager.setAlreadyFacebookLogin(data.data.isFacebookUser());
            } else {
                handleLoginError(data.data);
            }
        }
    }

    private void handleLoginError(LoginResponse data) {
        Map<String, Object> formErrors = data.getFormErrors();
        if (formErrors != null && formErrors.size() > 0) {
            Set<String> keyset = formErrors.keySet();
            String error_type = null;
            Iterator<String> it = keyset.iterator();
            while (it.hasNext()) {
                String key = it.next();
                if ("password".equals(key)) {
                    error_type = formErrors.get(key).toString();
                    edtPassword.showError(error_type);
                } else if ("email_phone".equals(key) || "login".equals(key)) {
                    error_type = formErrors.get(key).toString();
                    edtEmail.showError(error_type);
                } else if ("permission".equals(key)) {
                    error_type = formErrors.get(key).toString();
                    toast(error_type);
                } else {
                    error_type = formErrors.get(key).toString();
                    toast(error_type);
                    break;
                }
            }
        }
    }

    private void showProgress() {
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setProgress(1);
    }

    private void hideProgress() {
        btnSignIn.setProgress(0);
    }

    private void informAboutLoginSuccess(boolean success) {
        Fragment parentFragment = getParentFragment();
        informAboutLoginSuccess(parentFragment, success);

        FragmentActivity activity = getActivity();
        informAboutLoginSuccess(activity, success);

        Fragment targetFragment = getParentFragment();
        informAboutLoginSuccess(targetFragment, success);
        if (success) {
            MAT.trackEvent(getContext(), MAT.LOGIN);
            Config.isLogged = true;
        }
    }

    private void informAboutLoginSuccess(Object potentialListener, boolean success) {
        if (potentialListener != null && potentialListener instanceof LoginChangeListener) {
            if (success) {
                ((LoginChangeListener) potentialListener).onLoginSuccess();
            } else {
                ((LoginChangeListener) potentialListener).onLoginFailed();
            }
        }
    }

    public interface LoginChangeListener {
        void onLoginSuccess();

        void onLoginFailed();
    }
}
