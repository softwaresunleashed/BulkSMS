package com.unleashed.android.helpers.widgets.inputs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Utils.ViewUtils;
import com.unleashed.android.helpers.widgets.inputs.validators.InputTextEditValidator;

import java.util.HashMap;


public class InputBase extends LinearLayout {
    protected MarkState mMarkState = MarkState.EMPTY;
    //    protected ImageView mErrorMark;
    protected AppCompatImageView mIconImage;
    protected View mContainer;
    protected View mTitleContainer;
    protected View mClearBtn;
    protected ViewGroup mContents;
    protected TextView mTitle;
    protected TextView mCounter;
    protected TextView mMessage;
    protected TextView mToolTipTitle;
    protected TextView mToolTipDesc;
    protected View mTooltipContainer;
    protected FrameLayout mComponentContainerFL;

    protected boolean stickyTitle = false;
    protected boolean stickyCounter = false;

    protected boolean isClearable = true;
    protected boolean isPasswordUI = false;
    protected TextView mErrorMsg;
    protected LinearLayout mErrorContainerLL;
    //protected ParameterField field;
    protected String mFieldTitle;

    protected boolean isReadOnly;
    protected boolean useCustomIcon = false;
    protected boolean mIconDisabled = false;
    protected InputTextEditValidator mValidator;

    protected boolean isTitleVisible = false;
    protected OnChangeListener onChangeListener;
    private Drawable mIconResource;
    private OnClearListener onClearListener;
    protected boolean isShowPassword;

    public InputBase(Context context) {
        super(context);
        buildView();
    }

    public InputBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttributes(context, attrs);
        buildView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public InputBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyAttributes(context, attrs);
        buildView();
    }

    public void setContents(View view) {
//        ViewGroup container = (ViewGroup) findViewById(R.id.contents);
        mContents.removeAllViews();
        mContents.addView(view);
    }

    private void buildView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_input_base, this, true);
        bindViews();
    }

    private void bindViews() {
//        mErrorMark = (ImageView)findViewById(R.id.errorMark);
        mComponentContainerFL = (FrameLayout) findViewById(R.id.fl_componentContainer);
        mContainer = findViewById(R.id.container);

        mContents = (ViewGroup) findViewById(R.id.contents);

        mTooltipContainer = findViewById(R.id.tooltipContainer);
        mToolTipTitle = (TextView) findViewById(R.id.tooltipTitle);
        mToolTipDesc = (TextView) findViewById(R.id.tooltipDesc);

        mIconImage = (AppCompatImageView) findViewById(R.id.iconImage);
        mTitleContainer = findViewById(R.id.titleContainer);
        mTitle = (TextView) findViewById(R.id.title);
        mErrorContainerLL = (LinearLayout) findViewById(R.id.errorContainer);
        mErrorMsg = (TextView) findViewById(R.id.errorMsg);
        mCounter = (TextView) findViewById(R.id.counter);
        mMessage = (TextView) findViewById(R.id.message);
        mClearBtn = findViewById(R.id.iconClear);
        mClearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordUI) {
                    isShowPassword = !isShowPassword;
                    onRevealBtnClick();
                } else {
                    onClearBtnClick();
                }
            }
        });
    }

    protected void fillViews() {
        if (mIconResource != null) {
//            useCustomIcon = true;
            mIconImage.setVisibility(View.VISIBLE);
            mIconImage.setImageDrawable(mIconResource);
        } else {
            setMarkIcon(MarkState.EMPTY);
        }
        if (mIconDisabled) {
            setMarkIcon(MarkState.INVISIBLE);
        }
    }

    public void setNestedComponentsMargin(int margin) {
        if (mComponentContainerFL != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int layoutLeftMargin = (int) ViewUtils.convertDpToPixel(margin, getContext());
            params.setMargins(layoutLeftMargin, 0, 0, 0);
            mComponentContainerFL.setLayoutParams(params);

        }
    }

    public void hideCloseButton() {
        mClearBtn.setVisibility(View.GONE);
    }

    public void showError(String msg) {
        if (TextUtils.isEmpty(msg)) {
            hideError();
        } else {
            mErrorMsg.setText(msg);
            mErrorMsg.setVisibility(View.VISIBLE);
            mErrorContainerLL.setVisibility(View.VISIBLE);
            setMarkIcon(MarkState.ERROR);
        }
    }

    public void setStickyTitle(boolean stickyTitle) {
        this.stickyTitle = stickyTitle;
        if (stickyTitle) {
            showTitle(mFieldTitle);
        }
    }

    public void setStickyCounter(boolean stickyCounter) {
        this.stickyCounter = stickyCounter;
        if (stickyCounter) {
            showCounter();
        }
    }

    public void hideError() {
        mErrorMsg.setText("");
        mErrorMsg.setVisibility(View.GONE);
        mErrorContainerLL.setVisibility(View.GONE);
    }

    public String getError() {
        return mErrorMsg.getText().toString();
    }

    public void showTitle(String title) {
        if (!stickyTitle) {
            if (!isTitleVisible) {
                isTitleVisible = true;

                AnimatorSet set = new AnimatorSet();
                set.setInterpolator(new DecelerateInterpolator());
                set.playTogether(
                        ObjectAnimator.ofFloat(mTitleContainer, "alpha", 0, 1),
                        ObjectAnimator.ofFloat(mTitleContainer, "translationY", mTitleContainer.getHeight(), 0)
                );

                set.setDuration(600).start();

                mTitle.setVisibility(View.VISIBLE);
                mTitle.setText(title);
            }
        } else {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(title);
        }

    }


//    public void showTitle(String title)
//    {
//        if (!isTitleVisible) {
//            isTitleVisible = true;
//            mTitle.setVisibility(View.VISIBLE);
//            mTitle.setText(title);
//        }
//    }

    public void hideTitle() {
        isTitleVisible = false;
        if (!stickyTitle) {
            mTitle.setVisibility(View.GONE);

        }
        if (!stickyCounter) {
            mCounter.setVisibility(View.GONE);
        }
    }

    public void showCounter() {
        mCounter.setVisibility(View.VISIBLE);
    }

    public void setIconImage(int resourceId) {
        mIconImage.setImageResource(View.VISIBLE);
        mIconImage.setImageResource(resourceId);
    }

    public void setIconImageIfNotDisabled(int resourceId) {
        if (!mIconDisabled) {
            mIconImage.setImageResource(View.VISIBLE);
            mIconImage.setImageResource(resourceId);
        }
    }

    public void setMarkIcon(MarkState state) {
        mMarkState = state;
        switch (state) {
            case VALID: {
                if (useCustomIcon) {
                    mIconImage.setColorFilter(getContext().getResources().getColor(R.color.input_icon_valid));
                } else {
                    setIconImageIfNotDisabled(R.drawable.ic_valid_indicator);
                }
                if (mIconDisabled) {
                    mIconImage.setVisibility(View.GONE);
                } else {
                    mIconImage.setVisibility(View.VISIBLE);
                }
                break;
            }
            case ERROR: {
                if (useCustomIcon) {
                    mIconImage.setColorFilter(getContext().getResources().getColor(R.color.input_icon_invalid));
                } else {
                    setIconImageIfNotDisabled(R.drawable.ic_errror_indicator);
                }
                if (mIconDisabled) {
                    mIconImage.setVisibility(View.GONE);
                } else {
                    mIconImage.setVisibility(View.VISIBLE);
                }
                break;
            }
            case EMPTY: {
                if (useCustomIcon) {
                    mIconImage.setColorFilter(getContext().getResources().getColor(R.color.input_icon_neutral));
                } else {
                    setIconImageIfNotDisabled(R.drawable.post_gray_dot);
                }
                if (mIconDisabled) {
                    mIconImage.setVisibility(View.GONE);
                } else {
                    mIconImage.setVisibility(View.INVISIBLE);
                }

                break;
            }
            case INVISIBLE: {
                mIconImage.setVisibility(View.GONE);
                break;
            }
        }
    }

    public String getValue() {
        return null;
    }

    public void setValue(String value) {
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        TypedArray commonParamsArray = context.obtainStyledAttributes(attrs, R.styleable.CommonPostParams, 0, 0);
        if (commonParamsArray.hasValue(R.styleable.CommonPostParams_FieldIcon)) {
            mIconResource = commonParamsArray.getDrawable(R.styleable.CommonPostParams_FieldIcon);
            useCustomIcon = true;
        }
        if (commonParamsArray.hasValue(R.styleable.CommonPostParams_IconDisabled)) {
            mIconDisabled = commonParamsArray.getBoolean(R.styleable.CommonPostParams_IconDisabled, false);
        }
        if (commonParamsArray.hasValue(R.styleable.CommonPostParams_Clearable)) {
            isClearable = commonParamsArray.getBoolean(R.styleable.CommonPostParams_Clearable, false);
        }
    }

    public void setIsClearable(boolean isClearable) {
        this.isClearable = isClearable;
        hideClearBtn();
        showClearBtnIfClearable();
    }

    public void setIsPasswordUI(boolean isPasswordUI) {
        this.isPasswordUI = isPasswordUI;
        changeClearBtnImage();
    }

    private void changeClearBtnImage() {
        if (isShowPassword)
            ((ImageView) mClearBtn).setImageDrawable(Helpers.getSvgAsDrawable(getActivity(), R.drawable.ic_visibility_off));
        else
            ((ImageView) mClearBtn).setImageDrawable(Helpers.getSvgAsDrawable(getActivity(), R.drawable.ic_visibility));

        ((ImageView) mClearBtn).setColorFilter(Color.parseColor("#8f000000"));
    }

    public void setFieldTitle(String fieldtitle) {
        // if (TextUtils.isEmpty(getLabel()))
        mFieldTitle = fieldtitle;
    }

    public void setTitle(String title) {
        // if (TextUtils.isEmpty(getLabel()))
        if (mTitle != null)
            mTitle.setText(title);
    }

    protected String getLabel() {
        if (this.field != null) {
            return this.field.label + (this.field.suffix != null && !this.field.suffix.equals("") ? (" (" + this.field.suffix + ")") : "");
        }
        return "";
    }

//    public ParameterField getParameterField() {
//        return this.field;
//    }
//
//    public void setParameterField(ParameterField field) {
//        this.field = field;
//        setReadOnly(field.isReadOnly);
//        mFieldTitle = getLabel();
//        showTitle(mFieldTitle);
//        fillViews();
//        if (field != null) {
//            if (field.value != null) {
//                setValue(field.value);
//            }
//            if (field.validators != null && field.validators.size() > 0) {
//                buildValidator(field.validators);
//            }
//        }
//    }

    public boolean isError() {
        return !TextUtils.isEmpty(mErrorMsg.getText().toString());
//        return !mErrorMsg.getText().toString().equals("");
    }

    /**
     * To Enable or disable the error or valid icon prefix to editText
     */
    public void hideErrorIndicator(boolean hideErrorIcon) {
        if (mIconImage != null) {
            if (hideErrorIcon) {
                mIconDisabled = hideErrorIcon;
                mIconImage.setVisibility(ImageView.GONE);
            }
        }
    }

    public boolean validate() {
        return true;
    }

    protected void buildValidator(HashMap<String, String> validator) {
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    public void setReadOnly(boolean ro) {
        this.isReadOnly = ro;
    }

    public void hideClearBtn() {
        mClearBtn.setVisibility(View.GONE);
    }

    public void showClearBtnIfClearable() {
        if (isClearable && !isReadOnly()) {
            showClearBtn();
        }
    }

    public void showClearBtn() {
        mClearBtn.setVisibility(View.VISIBLE);
    }

    public void onClearBtnClick() {
        if (onClearListener != null) {
            onClearListener.onClear();
        }
    }


    public void onRevealBtnClick() {
        changeClearBtnImage();
    }

//    protected String getHint(ParameterField label) {
//        if (label != null && label.label != null) {
//            String hint = HintHelper.getInstance().getHint(label.label);
//            if (!TextUtils.isEmpty(hint)) {
//                return hint;
//            }
//        }
//        return "";
//    }

    public void setOnClearListener(OnClearListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void showMessage(String message) {
        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {
        mMessage.setVisibility(View.GONE);
    }

    public void showBorder() {
        mContainer.setBackgroundResource(R.drawable.post_ad_view_border);
    }

    public void showBorder(int resource) {
        mContainer.setBackgroundResource(resource);
    }

    public void hideBorder() {
        mContainer.setBackgroundDrawable(null);
    }

    public void showToolTip(String title, String desc) {
        if (title != null) {
            mToolTipTitle.setText(title);
        }
        if (desc != null) {
            mToolTipDesc.setText(desc);
        }
        mToolTipTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        mToolTipDesc.setVisibility(TextUtils.isEmpty(desc) ? View.GONE : View.VISIBLE);
        mTooltipContainer.setVisibility(TextUtils.isEmpty(title) && TextUtils.isEmpty(desc) ? View.GONE : View.VISIBLE);
    }

    public void hideToolTip() {
        mToolTipTitle.setVisibility(View.GONE);
        mToolTipDesc.setVisibility(View.GONE);
        mTooltipContainer.setVisibility(View.GONE);
    }

    protected Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public enum MarkState {
        VALID, EMPTY, ERROR, INVISIBLE
    }

    public interface OnClearListener {
        void onClear();
    }

    public interface OnChangeListener {
        void onChange();
    }

}
