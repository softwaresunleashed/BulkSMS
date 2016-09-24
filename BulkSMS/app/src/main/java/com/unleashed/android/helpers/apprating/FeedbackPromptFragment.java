package com.unleashed.android.helpers.apprating;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Preferences;
import com.unleashed.android.helpers.trackers.TrackerEvents;
import com.unleashed.android.helpers.trackers.Trackers;


public class FeedbackPromptFragment extends SimpleDialogFragment{

    public static final String TAG = FeedbackPromptFragment.class.getSimpleName();
    private static final int THRESHOLD = 7 * 24 * 60 * 60 * 1000; // 7days
    public static final int FIRST_THRESHOLD = 3 * 24 * 60 * 60 * 1000; // 7 days, for first time rating prompt

    public static boolean showFeedbackPromptIfPossible(Context context, FragmentManager fragmentManager){
        boolean showPrompt = Preferences.getAppPreference(context, Preferences.SHOW_RATING_PROMPT, true);
        if(showPrompt){
            if(!Preferences.getAppPreference(context, Preferences.REMIND_RATING_PROMPT_LATER, false)) {
                showFeedbackPrompt(context, fragmentManager);
                return true;
            }else{
                long lastTimeStamp = Preferences.getAppPreference(context, Preferences.RATING_PROMPT_TIME_STAMP, 0L);
                if(System.currentTimeMillis() - lastTimeStamp >= THRESHOLD ){
                    showFeedbackPrompt(context, fragmentManager);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean showFirstTimeFeedbackPromptIfPossible(Context context, FragmentManager fragmentManager) {
        boolean showPrompt = Preferences.getAppPreference(context, Preferences.SHOW_FIRST_RATING_PROMPT, true);
        if (showPrompt) {
            long lastTimeStamp = Preferences.getAppPreference(context, Preferences.FIRST_RATING_PROMPT_TIME_STAMP, 0L);
            if (System.currentTimeMillis() - lastTimeStamp >= FIRST_THRESHOLD) {
                Preferences.setAppPreference(context, Preferences.SHOW_FIRST_RATING_PROMPT, false);
                showFeedbackPrompt(context, fragmentManager);
                return true;
            }
        }
        return false;
    }

    private static void showFeedbackPrompt(Context context, FragmentManager fragmentManager) {
        FeedbackPromptFragment feedbackPromptFragment = FeedbackPromptFragment.newInstance();
        feedbackPromptFragment.show(fragmentManager, FeedbackPromptFragment.TAG);
        Trackers.trackEvent(TrackerEvents.RATING_PROMPT_SHOWN);
    }

    public static FeedbackPromptFragment newInstance() {
        Bundle args = new Bundle();
        FeedbackPromptFragment fragment = new FeedbackPromptFragment();
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
        View v = getActivity().getLayoutInflater().inflate(R.layout.custom_rating_layout, null);
        LinearLayout layoutNoLike = (LinearLayout) v.findViewById(R.id.layout_no_like);
        LinearLayout layoutYesLike = (LinearLayout) v.findViewById(R.id.layout_yes_like);

        layoutNoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                handleNegativeButtonClick();
            }
        });

        layoutYesLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                handlePositiveButtonClick();
            }
        });

        return v;
    }

    private void handleNegativeButtonClick() {
        RateActivity.startRateActivity(getContext());
        Trackers.trackEvent(TrackerEvents.RATING_PROMPT_LIKE_NO);
    }

    private void handlePositiveButtonClick() {
        RatingPromptFragment ratingPromptFragment = RatingPromptFragment.newInstance();
        ratingPromptFragment.show(getFragmentManager(), RatingPromptFragment.TAG);
        Trackers.trackEvent(TrackerEvents.RATING_PROMPT_LIKE_YES);
    }
}
