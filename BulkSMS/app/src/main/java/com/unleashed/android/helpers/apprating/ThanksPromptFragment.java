package com.unleashed.android.helpers.apprating;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Preferences;


public class ThanksPromptFragment extends SimpleDialogFragment {
    public final static String TAG = ThanksPromptFragment.class.getSimpleName();

    public static ThanksPromptFragment newInstance() {

        Bundle args = new Bundle();
        ThanksPromptFragment fragment = new ThanksPromptFragment();
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
        View v = getActivity().getLayoutInflater().inflate(R.layout.custom_rating_layout_step3, null);
        TextView txtViewOkGotIt = (TextView) v.findViewById(R.id.dialog_ok_got_it);

        txtViewOkGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                handlePositiveButtonClick();
            }
        });

        return v;
    }

    private void handlePositiveButtonClick() {
        Preferences.setAppPreference(getContext(), Preferences.SHOW_RATING_PROMPT, false);
        closeView();
    }

    protected void closeView() {
        Preferences.setAppPreference(getContext(), Preferences.getRatedKey(getContext()), "1");
        if (getActivity() != null)
            getActivity().finish();
    }
}
