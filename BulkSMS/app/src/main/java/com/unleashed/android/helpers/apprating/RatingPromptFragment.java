package com.unleashed.android.helpers.apprating;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.Preferences;
import com.unleashed.android.helpers.trackers.TrackerEvents;
import com.unleashed.android.helpers.trackers.Trackers;

public class RatingPromptFragment extends SimpleDialogFragment{

    public final static String TAG = RatingPromptFragment.class.getSimpleName();

    public static RatingPromptFragment newInstance() {

        Bundle args = new Bundle();
        RatingPromptFragment fragment = new RatingPromptFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Builder build(Builder builder) {
        builder.setView(getFormView());
        return builder;
    }

    private View getFormView() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.custom_rating_layout_step2, null);
        TextView txtViewRateUs = (TextView) v.findViewById(R.id.dialog_rate_us);
        TextView txtViewRemindLater = (TextView) v.findViewById(R.id.dialog_remind_later);
        TextView txtViewNoThanks = (TextView) v.findViewById(R.id.dialog_no_thanks);

        txtViewRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                handlePositiveButtonClick();
            }
        });

        txtViewRemindLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                handleNeutralButtonClick();
            }
        });

        txtViewNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                handleNegativeButtonClick();
            }
        });

        return v;
    }

    private void handleNegativeButtonClick() {
        Preferences.setAppPreference(getContext(), Preferences.SHOW_RATING_PROMPT, false);
        Trackers.trackEvent(TrackerEvents.RATING_PROMPT_DONT_RATE);
    }

    private void handlePositiveButtonClick() {
        Preferences.setAppPreference(getContext(), Preferences.SHOW_RATING_PROMPT, false);
        Helpers.rateApp(getContext());
        Trackers.trackEvent(TrackerEvents.RATING_PROMPT_RATE);
    }

    private void handleNeutralButtonClick() {
        Preferences.setAppPreference(getContext(), Preferences.REMIND_RATING_PROMPT_LATER, true);
        Preferences.setAppPreference(getContext(), Preferences.RATING_PROMPT_TIME_STAMP, System.currentTimeMillis());
        Trackers.trackEvent(TrackerEvents.RATING_PROMPT_REMIND_LATER);
    }
}
