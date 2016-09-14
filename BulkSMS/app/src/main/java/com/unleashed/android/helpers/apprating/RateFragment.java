package com.unleashed.android.helpers.apprating;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.unleashed.android.bulksms2.R;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.Preferences;

import java.util.Date;


public class RateFragment extends Fragment implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, ISimpleDialogListener {
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate, container, false);
        ((RatingBar) view.findViewById(R.id.ratingBar)).setOnRatingBarChangeListener(this);
        view.findViewById(R.id.rateCancel).setOnClickListener(this);
        view.findViewById(R.id.rateCancel2).setOnClickListener(this);
        view.findViewById(R.id.btnRate).setOnClickListener(this);
        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rateCancel:
            case R.id.rateCancel2:
                cancelView();
                break;
            case R.id.btnRate:

                new RateTask().execute(
                        ((TextView) getView().findViewById(R.id.rateEmail)).getText().toString(),
                        ((TextView) getView().findViewById(R.id.rateBody)).getText().toString(),
                        Integer.toString(Math.round(((RatingBar) getView().findViewById(R.id.ratingBar)).getRating()))
                );
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
        /*if (rating >= 4) {
            showRateAppDialog();
        }*/

        getView().findViewById(R.id.rateCancel2).setVisibility(View.GONE);
        getView().findViewById(R.id.rateForm).setVisibility(View.VISIBLE);
        ((TextView) getView().findViewById(R.id.rateDescription)).setText(context.getString(R.string.review_thank_you_for_rating));
    }

    private void showRateAppDialog() {
        SimpleDialogFragment.SimpleDialogBuilder builder = SimpleDialogFragment.createBuilder(getActivity(), getFragmentManager());
        builder.setMessage(R.string.review_go_to_app_store);

        builder.setPositiveButtonText(R.string.review_go);
        builder.setNegativeButtonText(R.string.review_not_now);
        builder.setTag("rateDialog");
        builder.setTargetFragment(this, 0);
        builder.show();
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
                //CommunicationV2.sendRate(strings[0], description);
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(context, context.getString(R.string.review_thank_you_for_feedback), Toast.LENGTH_SHORT).show();
            closeView();
            super.onPostExecute(s);
        }
    }
}