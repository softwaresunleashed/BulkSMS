package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class SetNewPasswordFragment extends Fragment implements View.OnClickListener {

    private static final String KEY_BUNDLE_PHONE_EMAIL = "phone_email";
    private static final String KEY_BUNDLE_PASSWORD = "password";
    private static final String KEY_BUNDLE_HASH = "hash";
    private static final String KEY_BUNDLE_LAUNCH_SOURCE = "launch_source";

    private static final int LOADER_CREATE_PASSWORD = 0;

    private InputTextEdit mPassIte;
    private ProgressDialog progressDialog;
    private TextView mTvUpdatePass;

    private String mPhone_email;
    private String mHash;
    private String mPassword;
    private String mLaunchSource;

    LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>> setNewPasswordCallback =
            new LoaderManager.LoaderCallbacks<TaskResponse<LoginResponse>>() {

                @Override
                public Loader<TaskResponse<LoginResponse>> onCreateLoader(int id, Bundle args) {
                    showProgress();
                    return createSetNewPasswordLoader(args);
                }

                @Override
                public void onLoadFinished(Loader<TaskResponse<LoginResponse>> loader, TaskResponse<LoginResponse> data) {

                    if (data != null) {
                        handleResponse(data);
                    } else {
                        ToastUtil.show(getActivity(), R.string.error_default);
                    }

                    hideProgress();
                    getLoaderManager().destroyLoader(loader.getId());

                }

                @Override
                public void onLoaderReset(Loader<TaskResponse<LoginResponse>> loader) {
                }
            };

    private Loader<TaskResponse<LoginResponse>> createSetNewPasswordLoader(Bundle args) {
        return new SetNewPasswordLoader(getActivity(), mPassword, mPhone_email, mHash);
    }

    private void handleLoginError(LoginResponse data) {
        if (data != null) {
            Map<String, Object> formErrors = data.getFormErrors();
            if (formErrors != null && formErrors.size() > 0) {
                Set<String> keyset = formErrors.keySet();
                Iterator<String> it = keyset.iterator();

                while (it.hasNext()) {
                    String key = it.next();
                    if ("password".equals(key)) {
                        mPassIte.showError(formErrors.get(key).toString());
                        break;
                    } else {
                        ToastUtil.show(getActivity(), formErrors.get(key).toString());
                        break;
                    }
                }
            }
        } else {
            ToastUtil.show(getActivity(), R.string.error_default);
        }
    }

    private void handleResponse(TaskResponse<LoginResponse> data) {
        if (data.data != null) {
            if (data.data.isSucceeded()) {
                ToastUtil.show(getActivity(), R.string.password_set_success);
                UserNameManager.setUserId(data.data.getUserId());
                UserNameManager.setUsername(data.data.getName());
                UserNameManager.setUserPhone(data.data.getPhone());
                UserNameManager.setAlreadyFacebookLogin(data.data.isFacebookUser());
                Config.isLogged = true;
                sendLoginSuccessEvent();
                setResultAndFinish();
            } else {
                handleLoginError(data.data);
            }
        }
    }

    private void sendLoginSuccessEvent() {
    }

    private void setResultAndFinish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.FINAL_LAUNCH_SOURCE, mLaunchSource);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mPhone_email = savedInstanceState.getString(KEY_BUNDLE_PHONE_EMAIL);
            mHash = savedInstanceState.getString(KEY_BUNDLE_HASH);
            mPassword = savedInstanceState.getString(KEY_BUNDLE_PASSWORD);
            mLaunchSource = savedInstanceState.getString(KEY_BUNDLE_LAUNCH_SOURCE);
        } else {
            Bundle data = getArguments();
            initBundleData(data);
            HashMap<String, String> params = preparePageParams(null);
        }
    }

    private void initBundleData(Bundle data) {
        if (data == null)
            return;
        mPhone_email = data.getString(OtpVerificationActivity.KEY_BUNDLE_OTP_DATA_EMAIL_PHONE);
        mHash = data.getString(OtpVerificationActivity.KEY_BUNDLE_OTP_DATA_HASH);
        mLaunchSource = data.getString(OtpVerificationActivity.KEY_BUNDLE_LAUNCH_SOURCE);
    }

    private void showProgress() {
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.please_wait), true, true);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing() && !getActivity().isFinishing()) {
            progressDialog.dismiss();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_password, container, false);
        mPassIte = (InputTextEdit) view.findViewById(R.id.ite_password);
        mTvUpdatePass = (TextView) view.findViewById(R.id.tv_update_password);

        mPassIte.setIsPasswordUI(true);
        mPassIte.setInputType(InputTextEdit.InputMethod.EMAIL);
        mPassIte.getView().setTransformationMethod(new PasswordTransformationMethod());
        mPassIte.setValidator(PostadValidators.getPasswordValidator(getActivity()));
        mPassIte.setHint(getString(R.string.enter_new_password));

        mTvUpdatePass.setOnClickListener(this);

        return view;
    }

    public static InputTextEditValidator getPasswordValidator(Context context) {
        return new InputTextEditValidator.Builder()
                .withRequired(context.getString(R.string.validation_field_required))
                .withMinLength(3, String.format(context.getString(R.string.validation_min_length), 3))
                .build();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_update_password:
                submitNewPassword();
                break;
        }

    }

    private HashMap<String, String> preparePageParams(String action) {
        XtraAtInternetParams.Builder paramBuilder = new XtraAtInternetParams.Builder();
        paramBuilder.setUserLocationParams(TablicaApplication.getCurrentParametersController());
        paramBuilder.setOrigin(mLaunchSource);
        return paramBuilder.build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_BUNDLE_PHONE_EMAIL, mPhone_email);
        outState.putString(KEY_BUNDLE_PASSWORD, mPassword);
        outState.putString(KEY_BUNDLE_HASH, mHash);
        outState.putString(KEY_BUNDLE_LAUNCH_SOURCE, mLaunchSource);
    }

    private void submitNewPassword() {

        String password = mPassIte.getValue().trim();
        boolean passIsValid = mPassIte.validate();

        if (passIsValid) {
            if (mPassIte != null)
                mPassword = mPassIte.getValue();

            getLoaderManager().initLoader(LOADER_CREATE_PASSWORD, null, setNewPasswordCallback);
        }
    }

    public static SetNewPasswordFragment newInstance(Bundle bundle) {
        SetNewPasswordFragment setNewPasswordFragment = new SetNewPasswordFragment();
        setNewPasswordFragment.setArguments(bundle);
        return setNewPasswordFragment;
    }
}
