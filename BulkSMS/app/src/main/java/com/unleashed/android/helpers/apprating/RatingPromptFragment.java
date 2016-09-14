package com.unleashed.android.helpers.apprating;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.unleashed.android.bulksms2.R;
import com.unleashed.android.helpers.Helpers;
import com.unleashed.android.helpers.Preferences;

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
        builder.setTitle(R.string.rating_prompt_title);
        builder.setMessage(R.string.rating_prompt_message);

        builder.setNegativeButton(R.string.rating_prompt_negative_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNegativeButtonClick();
                dismiss();
            }
        });

        builder.setPositiveButton(R.string.rating_prompt_positive_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePositiveButtonClick();
                dismiss();
            }
        });

        builder.setNeutralButton(R.string.rating_prompt_neutral_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNeutralButtonClick();
                dismiss();
            }
        });

        return builder;
    }

    private void handleNegativeButtonClick(){
        Preferences.setAppPreference(getContext(), Preferences.SHOW_RATING_PROMPT, false);

    }

    private void handlePositiveButtonClick(){
        Preferences.setAppPreference(getContext(), Preferences.SHOW_RATING_PROMPT, false);
        Helpers.rateApp(getContext());
    }

    private void handleNeutralButtonClick(){
        Preferences.setAppPreference(getContext(), Preferences.REMIND_RATING_PROMPT_LATER, true);
        Preferences.setAppPreference(getContext(), Preferences.RATING_PROMPT_TIME_STAMP, System.currentTimeMillis());
    }
}
