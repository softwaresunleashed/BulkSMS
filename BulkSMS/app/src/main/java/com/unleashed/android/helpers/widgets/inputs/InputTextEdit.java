package com.unleashed.android.helpers.widgets.inputs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.unleashed.android.bulksms1.R;
import com.unleashed.android.helpers.Utils.NumberUtils;
import com.unleashed.android.helpers.widgets.inputs.validators.InputTextEditValidator;

import java.util.HashMap;
import java.util.Map;

public class InputTextEdit extends InputBase {
    protected EditText mValue;
    protected int mMinCharacters;
    protected int mMaxCharacters;
    protected int mMinLines;
    protected boolean mSingleLine;
    protected boolean mShowCounter = false;

    protected TextWatcher mCallbackWatcher;
    protected EditTextActionsInterface mEditTextActionWatcher;
    protected OnInputFocus mInputFocusListener;
    protected TextView mPrefix;
    protected View mPrefixDivider;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mCallbackWatcher != null) {
                mCallbackWatcher.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mCallbackWatcher != null) {
                mCallbackWatcher.onTextChanged(s, start, before, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            int length = s.length();
            if (length == 0) {
                hideTitle();
                hideClearBtn();
                setMarkIcon(MarkState.EMPTY);
                hideError();
            } else {
                 showTitle(mFieldTitle);
                if (isClearable && !isReadOnly) {
                    showClearBtn();
                }
            }
            updateCounter(length);
            validateWithoutShowingErrorMessage(length);

            if (mCallbackWatcher != null) {
                mCallbackWatcher.afterTextChanged(s);
            }
        }
    };




    public InputTextEdit(Context context) {
        super(context);
        buildView();
    }

    public InputTextEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyAttributes(context, attrs);
        buildView();
    }

    public InputTextEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyAttributes(context, attrs);
        buildView();
    }

    public void showTitle() {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(mFieldTitle);
    }

    public void hideTitleContainer(){
        mTitleContainer.setVisibility(View.GONE);
    }

    private void buildView() {
        inflateView();
        bindViews();
        setupIME();
    }

    private void bindViews() {
        mValue.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    boolean isValid = validate();
                    if (mEditTextActionWatcher != null) {
                        if (isValid)
                            mEditTextActionWatcher.onEditComplete();
                        else
                            mEditTextActionWatcher.onEditError();
                    }
                } else {
                    if (mEditTextActionWatcher != null)
                        mEditTextActionWatcher.onEditStart();

                }
                if (mInputFocusListener != null) {
                    mInputFocusListener.onFocus(hasFocus);
                }
            }
        });
        mValue.setMinLines(mMinLines);
        mValue.setSingleLine(mSingleLine);
        fillViews();
    }

    public void setHint(String hint) {
        if(!TextUtils.isEmpty(hint)) {
            mValue.setHint(hint);
        }
    }

    public void setEditTextContentDescription(String contentDescription) {
        if (mValue != null)
            mValue.setContentDescription(contentDescription);
    }

    protected void fillViews() {
        super.fillViews();
        String hint=getHint(field);
        if(!TextUtils.isEmpty(hint)){
            mValue.setHint(hint);
            //mValue.setContentDescription(hint);
        }else{
            mValue.setHint(mFieldTitle);
            //mValue.setContentDescription(mFieldTitle);
        }

        if (mMaxCharacters > 0) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(mMaxCharacters);
            mValue.setFilters(filterArray);
        }
        mValue.addTextChangedListener(textWatcher);
        if (mMinCharacters > 0) {
            updateCounter(getValue().length());
        }
    }



    public void addTextChangedListener(TextWatcher watcher) {
        mCallbackWatcher = watcher;
    }

    public void setEditTextActionsListener(EditTextActionsInterface watcher) {
        mEditTextActionWatcher = watcher;
    }

    protected void updateCounter(int length) {
        if (mShowCounter) {
            if (length > 0||stickyCounter) {
                mCounter.setVisibility(View.VISIBLE);
                mCounter.setText(length + " / " + mMaxCharacters);
                mCounter.setTextColor(length > 0 && length < mMinCharacters ? getContext().getResources().getColor(R
                        .color.red_error) : getContext().getResources().getColor(R.color.light_grey));
            } else {
                mCounter.setVisibility(View.GONE);
            }
        }
    }

    protected void inflateView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.widget_input_textedit, this, false);

        EditText editText = (EditText) root.findViewById(R.id.value);
        mPrefix = (TextView) root.findViewById(R.id.prefix);
        mPrefixDivider = root.findViewById(R.id.prefix_divider);

/*
        TintManager manager = new TintManager(getContext());
        view.setBackgroundDrawable(manager.getDrawable(R.drawable.abc_edit_text_material));
*/

//        int leftPadding = getContext().getResources().getDimensionPixelSize(R.dimen.input_left_padding);
//        //int bottomPadding = getContext().getResources().getDimensionPixelSize(R.dimen.input_bottom_padding);
//        root.setPadding(leftPadding, root.getPaddingTop(), root.getPaddingRight(), root.getPaddingBottom());
        mValue = editText;
        setContents(root);
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        TypedArray commonParamsArray = context.obtainStyledAttributes(attrs, R.styleable.CommonPostParams, 0, 0);
        if (commonParamsArray.hasValue(R.styleable.CommonPostParams_FieldTitle)) {
            mFieldTitle = commonParamsArray.getString(R.styleable.CommonPostParams_FieldTitle);
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PostEditText, 0, 0);

        setMinCharacters(typedArray.getInteger(R.styleable.PostEditText_MinCharacters, 0));
        setMaxCharacters(typedArray.getInteger(R.styleable.PostEditText_MaxCharacters, 20000));

        mMinLines = typedArray.getInteger(R.styleable.PostEditText_MinLines, 1);
        mSingleLine = typedArray.getBoolean(R.styleable.PostEditText_SingleLine, false);
    }

    public void setMinCharacters(int charactersCount) {
        this.mMinCharacters = charactersCount;
        setShowCounterBaseOnCharacterLimits();
    }

    public void setMaxCharacters(int maxCharactersCount) {
        this.mMaxCharacters = maxCharactersCount;
        setShowCounterBaseOnCharacterLimits();
    }

    private void setShowCounterBaseOnCharacterLimits() {
        mShowCounter = mMinCharacters > 0 || mMaxCharacters < 20000;
    }

    public void setValidator(InputTextEditValidator validator) {
        mValidator = validator;
    }

    @Override
    public String getValue() {
        return mValue.getText().toString().trim();
    }

    public EditText getValueHandle(){
        return mValue;
    }

    @Override
    public void setValue(String value) {
        mValue.setText(value);

        boolean isEmpty = value == null || value.equals("");
        if (isEmpty) {
            setMarkIcon(MarkState.EMPTY);
        } else {
            setMarkIcon(MarkState.VALID);
            mValue.setSelection(mValue.getText().length());
        }
    }

    public void setInputType(InputMethod inputType) {
        if (inputType == InputMethod.NORMAL) {
            mValue.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (inputType == InputMethod.PASSWORD) {
            mValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (inputType == InputMethod.PHONE) {
            mValue.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (inputType == InputMethod.NORMAL_MULTILINE) {
            mValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else if (inputType == InputMethod.DATE) {
            mValue.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        } else if (inputType == InputMethod.DIGIT) {
            mValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (inputType == InputMethod.EMAIL) {
            mValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else if (inputType == InputMethod.FLOAT) {
            mValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

    public void setBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mValue.setBackground(drawable);
        }
    }

    public void validateWithoutShowingErrorMessage(int length) {
        if (length > 0 && mValidator != null) {
            try {
                mValidator.validate(getValue());
                setMarkIcon(MarkState.VALID);
                hideError();
                hideErrorTintOnEditText();
            } catch (ValidationException e) {
                setMarkIcon(MarkState.ERROR);
                setErrorTintOnEditText();
            }
        }
    }

    /**
     * To show the error tint in editText
     */
    public void setErrorTintOnEditText() {
        if (mValue != null && mValue.getBackground() != null) {
            mValue.getBackground().setColorFilter(getResources().getColor(R.color.red_error), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * To hide the error tint in editText
     */
    public void hideErrorTintOnEditText() {
        if (mValue != null && mValue.getBackground() != null) {
            mValue.getBackground().setColorFilter(getResources().getColor(R.color.black_1), PorterDuff.Mode.SRC_ATOP);
        }
    }




    @Override
    public boolean validate() {
        if (mValidator != null) {
            try {
                mValidator.validate(getValue());
                hideError();
                setMarkIcon(MarkState.VALID);
            } catch (ValidationException e) {
                showError(e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    protected void buildValidator(HashMap<String, String> validators) {
        super.buildValidator(validators);
        InputTextEditValidator.Builder fieldValidatorBuilder = new InputTextEditValidator.Builder();

        Resources res = getContext().getResources();

        for (Map.Entry<String, String> v : validators.entrySet()) {
            String key = v.getKey();
            String value = v.getValue();
            if (key.equals("min")) {
                int num = NumberUtils.parseWithDefault(value, 0);
                fieldValidatorBuilder.withMin(num, String.format(res.getString(R.string.validation_min_value), num));
            } else if (key.equals("max")) {
                int num = NumberUtils.parseWithDefault(value, Integer.MAX_VALUE);
                fieldValidatorBuilder.withMax(num, String.format(res.getString(R.string.validation_max_value), num));
            } else if (key.equals("maxlength")) {
                int num = NumberUtils.parseWithDefault(value, Integer.MAX_VALUE);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(num);
                mValue.setFilters(filterArray);
                fieldValidatorBuilder.withMaxLength(num, String.format(res.getString(R.string.validation_max_length), num));
            } else if (key.equals("digits")) {
                fieldValidatorBuilder.withDigits(res.getString(R.string.validation_field_digits));
                setInputType(InputMethod.DIGIT);
            } else if (key.equals("number")) {
                fieldValidatorBuilder.withDigits(res.getString(R.string.validation_field_digits));

                // If field contains the "digit" validator, force input type to be digit-only (integer)
                if (validators.containsKey("digits")) {
                    setInputType(InputMethod.DIGIT);
                }
                // Allow decimals otherwise
                else {
                    setInputType(InputMethod.FLOAT);
                }

            } else if (key.equals("required")) {
                fieldValidatorBuilder.withRequired(res.getString(R.string.validation_field_required));
            } else if (key.equals("dateISO")) {
                setInputType(InputMethod.DATE);
            } else if (key.equals("dateFuture")) {
                fieldValidatorBuilder.withFutureDate(res.getString(R.string.validation_future_date));
            }
        }


        mValidator = fieldValidatorBuilder.build();
    }

    private void setupIME() {
        mValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validate();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mValue.getWindowToken(), 0);
                    return true;
                }
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    validate();
                }
                return false;
            }
        });
    }

    @Override
    public void setParameterField(ParameterField field) {
        if (field instanceof PriceParameterField) {
            field.value = ((PriceParameterField) field).value.get("1");
        }else if (field instanceof RangeParameterField) {
            field.value = ((RangeParameterField) field).value.containsKey("from") ? ((RangeParameterField) field).value.get("from") : ((RangeParameterField) field).value.get("");
        }
        super.setParameterField(field);
        if (field.value != null && !field.value.equals("")) {
            validate();
        }
    }

    public void setTitle(String title) {
        mValue.setHint(title);
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(title);
    }

    public void setTitleMakeOffer(String title) {
        mValue.setHint(title);
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(title);
        mTitle.setTextColor(getResources().getColor(R.color.Material_54_Black));
        mTitle.setTextSize(12);
    }

//    @Override
//    public void showTitle(String title)
//    {
//        if (!isTitleVisible) {
//            isTitleVisible = true;
//
//            AnimatorSet set = new AnimatorSet();
//            set.setInterpolator(new DecelerateInterpolator());
//            set.playTogether(
//                    ObjectAnimator.ofFloat(mTitleContainer, "alpha", 0, 1),
//                    ObjectAnimator.ofFloat(mTitleContainer, "translationY", mTitleContainer.getHeight(), 0)
//            );
//
//            set.setDuration(600).start();
//
//            mTitle.setVisibility(View.VISIBLE);
//            mTitle.setText(title);
//        }
//    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        ViewState stateToSave = new ViewState();
        stateToSave.base = superState;
        stateToSave.custom = mValue.onSaveInstanceState();

        return stateToSave;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        ViewState savedState = (ViewState) state;
        super.onRestoreInstanceState(savedState.base);
        mValue.onRestoreInstanceState(savedState.custom);
    }

    public EditText getView() {
        return mValue;
    }

    @Override
    public void onClearBtnClick() {
        setValue("");
        hideClearBtn();
        super.onClearBtnClick();
    }

    @Override
    public void onRevealBtnClick() {
        if (mValue != null) {
            if (isShowPassword)
                mValue.setTransformationMethod(null);
            else
                mValue.setTransformationMethod(new PasswordTransformationMethod());

            mValue.setSelection(mValue.getText().length());
        }

        super.onRevealBtnClick();
    }

    @Override
    public void setReadOnly(boolean ro) {
        super.setReadOnly(ro);
        mValue.setEnabled(!ro);
        if (ro == true) {
            setIsClearable(false);
        }
    }

    @Override
    public void clearFocus() {
        super.clearFocus();
        mValue.clearFocus();
    }

    public void setOnFocusListener(OnInputFocus focusListener) {
        this.mInputFocusListener = focusListener;
    }

    public boolean childHasFocus() {
        return mValue.hasFocus();
    }

    public void requestFocusToChild() {
        mValue.requestFocus();
    }

    public void showPrefix(String prefix) {
        mPrefix.setText(prefix);
        mPrefix.setVisibility(View.VISIBLE);
        mPrefixDivider.setVisibility(View.VISIBLE);
    }

    public void hidePrefix() {
        mPrefix.setVisibility(View.GONE);
        mPrefixDivider.setVisibility(View.GONE);
    }

    public enum InputMethod {PHONE, NORMAL, NORMAL_MULTILINE, DIGIT, DATE, EMAIL, InputMethod, FLOAT, PASSWORD}

    public interface OnInputFocus {
        public void onFocus(boolean hasFocus);
    }

    public static class ViewState implements Parcelable {
        public static final Creator<ViewState> CREATOR = new Creator<ViewState>() {
            public ViewState createFromParcel(Parcel source) {
                return new ViewState(source);
            }

            public ViewState[] newArray(int size) {
                return new ViewState[size];
            }
        };
        public Parcelable base;
        public Parcelable custom;

        public ViewState() {
        }

        private ViewState(Parcel in) {
            this.base = in.readParcelable(Parcelable.class.getClassLoader());
            this.custom = in.readParcelable(Parcelable.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.base, 0);
            dest.writeParcelable(this.custom, 0);
        }
    }
}
