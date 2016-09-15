package com.unleashed.android.helpers.apprating;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Preferences;


public class FeedbackPromptFragment extends SimpleDialogFragment{

    public static final String TAG = FeedbackPromptFragment.class.getSimpleName();
    private static final int THRESHOLD = 7 * 24 * 60 * 60 * 1000; // 7days

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

    private static void showFeedbackPrompt(Context context, FragmentManager fragmentManager){
        FeedbackPromptFragment feedbackPromptFragment = FeedbackPromptFragment.newInstance();
        feedbackPromptFragment.show(fragmentManager, FeedbackPromptFragment.TAG);
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
        builder.setTitle(R.string.feedback_prompt_title);

        builder.setNegativeButton(R.string.feedback_prompt_negative_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNegativeButtonClick();
                dismiss();
            }
        });

        builder.setPositiveButton(R.string.feedback_prompt_positive_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePositiveButtonClick();
                dismiss();
            }
        });

        return builder;
    }

    private void handleNegativeButtonClick(){
        Preferences.setAppPreference(getContext(), Preferences.SHOW_RATING_PROMPT, false);
        RateActivity.startRateActivity(getContext());
    }

    private void handlePositiveButtonClick(){
        RatingPromptFragment ratingPromptFragment = RatingPromptFragment.newInstance();
        ratingPromptFragment.show(getFragmentManager(), RatingPromptFragment.TAG);
    }
}
