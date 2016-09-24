package com.unleashed.android.helpers.apprating;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.unleashed.android.application.SUApplication;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.Preferences;
import com.unleashed.android.helpers.Utils.ToastUtil;
import com.unleashed.android.helpers.crashreporting.CrashReportBase;
import com.unleashed.android.helpers.fragments.BaseFragment;
import com.unleashed.android.helpers.networkops.Connectivity;
import com.unleashed.android.helpers.trackers.TrackerEvents;
import com.unleashed.android.helpers.trackers.Trackers;

import java.util.Date;


public class RateFragment extends BaseFragment implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, ISimpleDialogListener {
    private Context context;

    private DrawableRatingBar ratingBar;
    private Button btnSubmit;
    private TextView rateEmail;
    private TextView rateBody;
    private LinearLayout rateForm;
    private TextView rateDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);
        ratingBar = (DrawableRatingBar) view.findViewById(R.id.ratingBar);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit_rating);
        rateEmail = (TextView) view.findViewById(R.id.rateEmail);
        rateBody = (TextView) view.findViewById(R.id.rateBody);
        rateForm = (LinearLayout) view.findViewById(R.id.rateForm);
        rateDescription = (TextView) view.findViewById(R.id.rateDescription);

        ratingBar.setOnRatingBarChangeListener(this);
        btnSubmit.setOnClickListener(this);
        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit_rating:
                if(Math.round(ratingBar.getRating()) == 0) {
                    ToastUtil.show(getContext(), "Can't submit empty form. Please provide a rating");
                } else {
                    if(Connectivity.isConnected(getContext())) {
                        new RateTask().execute(
                                rateEmail.getText().toString(),
                                rateBody.getText().toString(),
                                Integer.toString(Math.round(ratingBar.getRating()))
                        );
                    } else {
                        ToastUtil.show(getContext(), "No internet connection. Please check internet connectivity");
                    }
                }

                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resetTargetFragmentToDialogFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplication();
    }

    private void resetTargetFragmentToDialogFragment() {
        Fragment dialog = getFragmentManager().findFragmentByTag("rateDialog");
        if (dialog != null) {
            dialog.setTargetFragment(this, 0);
        }
    }

    public void onRatingChanged(RatingBar rateBar, float rating, boolean fromUser) {
        rateForm.setVisibility(View.VISIBLE);
        rateDescription.setText(context.getString(R.string.review_thank_you_for_rating));
    }

    @Override
    public void onPositiveButtonClicked(int i) {
        openPlay();
    }

    @Override
    public void onNegativeButtonClicked(int i) {
        cancelView();
    }

    @Override
    public void onNeutralButtonClicked(int i) {

    }

    protected void openPlay() {
        Helpers.rateApp(getContext());
        closeView();
    }

    protected void cancelView() {
        Date date = new Date(System.currentTimeMillis());
        Preferences.setAppPreference(context, Preferences.getRateKey(context), date.getTime());
        if (getActivity() != null)
            getActivity().finish();
    }

    protected void closeView() {
        Preferences.setAppPreference(context, Preferences.getRatedKey(context), "1");
        if (getActivity() != null)
            getActivity().finish();
    }

    class RateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String description = "User rating is: " + strings[2] + "* \n" + strings[1];
            try {
                CrashReportBase.sendLog(strings[0] + " " + description);
            } catch (Exception e) {
                CrashReportBase.sendCrashReport(e);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Context appContext = SUApplication.getContext();
            Preferences.setAppPreference(appContext, Preferences.SHOW_RATING_PROMPT, false);

            Trackers.trackEvent(TrackerEvents.EVENT_RATING_FEEDBACK_SUBMIT);

            super.onPostExecute(s);

            ThanksPromptFragment thanksPromptFragment = ThanksPromptFragment.newInstance();
            thanksPromptFragment.show(getFragmentManager(), ThanksPromptFragment.TAG);
            thanksPromptFragment.setCancelable(false);
        }
    }
}