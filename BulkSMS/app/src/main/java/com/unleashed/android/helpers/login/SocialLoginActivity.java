package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.activities.BaseActivity;
import com.unleashed.android.helpers.constants.Constants;

import java.util.ArrayList;

public class SocialLoginActivity extends BaseActivity implements SocialLoginFragment.SocialLoginListener, ISimpleDialogListener {

    public static final int REQUEST_CODE = 111;
    private static final String LAUNCH_CREATE_PASSWORD = "launch_password";
    private ArrayList<Intent> intentStackList;

    public static void startActivityResult(Activity activity, Bundle data) {
        Intent intent = new Intent(activity, SocialLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(LAUNCH_CREATE_PASSWORD, data);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void startActivityForResult(Activity activity) {
        Intent intent = new Intent(activity, SocialLoginActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().hasExtra("task_stack"))
            intentStackList = (ArrayList<Intent>) getIntent().getSerializableExtra("task_stack");

        if (savedInstanceState == null) {
            initFragments();
        }

        if (getIntent() != null && getIntent().hasExtra(LAUNCH_CREATE_PASSWORD)) {
            SetNewPasswordActivity.startActivityForResult(this, getIntent().getBundleExtra(LAUNCH_CREATE_PASSWORD));
        }
    }

    private void launchTaskStack() {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        for (Intent intent : intentStackList)
            stackBuilder.addNextIntent(intent);
        stackBuilder.startActivities();
    }

    private void initFragments() {
        SocialLoginFragment socialLoginFragment = new SocialLoginFragment().newInstance("login");
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, socialLoginFragment, LoginFragment.class.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onSocialLoginSuccess() {
        setResultandFinish();
    }

    @Override
    public void onBackPressed() {
        if (Helpers.isMandatoryRegEnabled(this))
            showMandatoryRegistrationPopup(R.string.login, R.string.login_required_message, R.string.continue_btn, R.string.exit_app);
        else {
            super.onBackPressed();
        }
    }

    private void showMandatoryRegistrationPopup(int title, int message, int positiveMsg, int negativeMsg) {
        SimpleDialogFragment.SimpleDialogBuilder dialogBuilder = SimpleDialogFragment.createBuilder(SocialLoginActivity.this, getSupportFragmentManager());
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        if (positiveMsg != -1)
            dialogBuilder.setPositiveButtonText(positiveMsg);
        dialogBuilder.setNegativeButtonText(negativeMsg);
        dialogBuilder.setCancelableOnTouchOutside(true);
        dialogBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SetNewPasswordActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResultandFinish();
        }
    }

    private void setResultandFinish() {
        if (intentStackList == null || intentStackList.isEmpty()) {
            setResult(RESULT_OK);
            finish();
        } else {
            launchTaskStack();
        }
    }

    private void setCancelledResultandFinish() {
        Intent intent = new Intent();
        intent.putExtra(Constants.FINISH_APP, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        setCancelledResultandFinish();
    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {

    }

}
