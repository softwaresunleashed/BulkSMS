package com.unleashed.android.helpers.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.activities.BaseActivity;


public class SetNewPasswordActivity extends BaseActivity {

    public static final int REQUEST_CODE = 100;

    public static void startActivityForResult(Fragment fragment, Bundle bundle) {
        Intent intent = new Intent(fragment.getActivity(), SetNewPasswordActivity.class);
        intent.putExtra("data", bundle);
        fragment.startActivityForResult(intent, SetNewPasswordActivity.REQUEST_CODE);
    }

    public static void startActivityForResult(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, SetNewPasswordActivity.class);
        intent.putExtra("data", bundle);
        activity.startActivityForResult(intent, SetNewPasswordActivity.REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.create_new_password);


        if (savedInstanceState == null) {
            initFragment();
        }
    }

    protected void initFragment() {
        Bundle bundle = null;
        if (getIntent() != null)
            bundle = getIntent().getParcelableExtra("data");

        SetNewPasswordFragment setNewPasswordFragment = SetNewPasswordFragment.newInstance(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, setNewPasswordFragment, SetNewPasswordFragment.class.getSimpleName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    public static Intent getActivityIntent(Context context, String email, String hash, String source) {
        Intent intent = new Intent(context, SetNewPasswordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(OtpVerificationActivity.KEY_BUNDLE_OTP_DATA_HASH, hash);
        bundle.putString(OtpVerificationActivity.KEY_BUNDLE_OTP_DATA_EMAIL_PHONE, email);
        bundle.putString(OtpVerificationActivity.KEY_BUNDLE_LAUNCH_SOURCE, source);
        intent.putExtra("data", bundle);
        return intent;
    }
}
