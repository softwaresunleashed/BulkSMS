package com.unleashed.android.helpers.widgets.inputs.validators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostEditTextValidator {

    private static final String[] SEEKING_WORDS =
            {"poszukuję", "kupię", "przyjmę", "'poszukuje", "pszyjmę", "przyjme", "kupie", "szukam"};

    private static final String EMAIL_PATTERN =
            "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

    private static final String SKYPE_PATTERN = "^[:a-z0-9._-]+$";

    private static final String PERSON_PATTERN = "[`~!@#$%^&*()_|+\\=?;:\",.<>\\{\\}\\[\\]\\\\\\/]+";

    private static final String WWW_PATTERN = "(ftp|http|https):\\/\\/(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)"
            + "?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?|www\\.(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?";

    private static final String PHONE_PATTERN = "^[0-9 \\-,\\+\\(\\)]+$";

    private Pattern mEmailPattern;
    private Pattern mSkypePattern;
    private Pattern mPersonPattern;
    private Pattern mWwwPattern;
    private Pattern mPhonePattern;

    private boolean mRequired;

    private boolean mDisallowSeekingWords;

    private boolean mDisallowEmail;
    private boolean mEnforceEmail;
    private boolean mEnforcePhone;
    private boolean mDisallowWww;
    private boolean mEnforceSkype;
    private boolean mEnforcePerson;
    private boolean mUppercaseCheck;
    private boolean mDigits;
    private boolean mFutureDate;

    private Integer mMinLength;
    private Integer mMaxLength;
    private Integer mMin;
    private Integer mMax;

    private String mRequiredValidationError;
    private String mMinLengthValidationError;
    private String mMaxLengthValidationError;
    private String mMinValidationError;
    private String mMaxValidationError;
    private String mSeekingWordsValidationError;
    private String mEnforceEmailValidationError;
    private String mDisallowEmailValidationError;
    private String mEnforceSkypeValidationError;
    private String mUppercaseCheckValidationError;
    private String mEnforcePersonValidationError;
    private String mDisallowWwwValidationError;
    private String mEnforcePhoneValidationError;
    private String mWithDigitsError;
    private String mWithFutureDateError;

    public PostEditTextValidator(Builder builder) {
        mRequired = builder.mRequired;
        mDisallowSeekingWords = builder.mDisallowSeekingWords;
        mMinLength = builder.mMinLength;
        mMaxLength = builder.mMaxLength;
        mEnforceEmail = builder.mEnforceEmail;
        mDisallowEmail = builder.mDisallowEmail;
        mEnforceSkype = builder.mEnforceSkype;
        mUppercaseCheck = builder.mUppercaseCheck;
        mEnforcePerson = builder.mEnforcePerson;
        mDisallowWww = builder.mDisallowWww;
        mEnforcePhone = builder.mEnforcePhone;
        mMin = builder.mMin;
        mMax = builder.mMax;
        mDigits = builder.mDigit;
        mFutureDate = builder.mFutureDate;

        mRequiredValidationError = builder.mRequiredValidationError;
        mMinLengthValidationError = builder.mMinLengthValidationError;
        mMaxLengthValidationError = builder.mMaxLengthValidationError;
        mSeekingWordsValidationError = builder.mSeekingWordsValidationError;
        mEnforceEmailValidationError = builder.mEnforceEmailValidationError;
        mDisallowEmailValidationError = builder.mDisallowEmailValidationError;
        mEnforceSkypeValidationError = builder.mEnforceSkypeValidationError;
        mUppercaseCheckValidationError = builder.mUppercaseCheckValidationError;
        mEnforcePersonValidationError = builder.mEnforcePersonValidationError;
        mDisallowWwwValidationError = builder.mDisallowWwwValidationError;
        mEnforcePhoneValidationError = builder.mEnforcePhoneValidationError;
        mMinValidationError = builder.mMinValidationError;
        mMaxValidationError = builder.mMaxValidationError;
        mWithDigitsError = builder.mWithDigitsError;
        mWithFutureDateError = builder.mWithFutureDateError;

        mEmailPattern = Pattern.compile(EMAIL_PATTERN);
        mSkypePattern = Pattern.compile(SKYPE_PATTERN);
        mPersonPattern = Pattern.compile(PERSON_PATTERN);
        mWwwPattern = Pattern.compile(WWW_PATTERN);
        mPhonePattern = Pattern.compile(PHONE_PATTERN);
    }

    public void validate(String value) throws ValidationException {

        if (mRequired) {
            if (value.length() == 0) {
                throw new ValidationException(mRequiredValidationError);
            }
        }

        if (mRequired || !value.equals("")) {
            if (mMinLength != null) {
                if (value.length() < mMinLength) {
                    throw new ValidationException(mMinLengthValidationError);
                }
            }

            if (mMin != null) {
                try {
                    if (Long.parseLong(value) < mMin) {
                        throw new ValidationException(mMinValidationError);
                    }
                } catch (Exception e) {
                    throw new ValidationException(e.getMessage());
                }
            }

            if (mMax != null) {
                try {
                    if (Long.parseLong(value) > mMax) {
                        throw new ValidationException(mMaxValidationError);
                    }
                } catch (Exception e) {
                    throw new ValidationException(mMaxValidationError);
                }
            }

            if (mMaxLength != null) {
                if (value.length() > mMaxLength) {
                    throw new ValidationException(mMaxLengthValidationError);
                }
            }

            if (mDisallowSeekingWords) {
                for (int i = 0; i < SEEKING_WORDS.length; i++) {
                    if (value.contains(SEEKING_WORDS[i])) {
                        throw new ValidationException(mSeekingWordsValidationError);
                    }
                }
            }

            if (mEnforceEmail) {
                Matcher matcher = mEmailPattern.matcher(value);
                if (!matcher.matches()) {
                    throw new ValidationException(mEnforceEmailValidationError);
                }
            }

            if (mDisallowEmail) {
                Matcher matcher = mEmailPattern.matcher(value);
                if (matcher.find()) {
                    throw new ValidationException(mDisallowEmailValidationError);
                }
            }

            if (mDisallowWww) {
                Matcher matcher = mWwwPattern.matcher(value);
                if (matcher.find()) {
                    throw new ValidationException(mDisallowWwwValidationError);
                }
            }

            if (mEnforceSkype) {
                Matcher matcher = mSkypePattern.matcher(value);
                if (!matcher.matches()) {
                    throw new ValidationException(mEnforceSkypeValidationError);
                }
            }

            if (mEnforcePerson) {
                Matcher matcher = mPersonPattern.matcher(value);
                if (matcher.find()) {
                    throw new ValidationException(mEnforcePersonValidationError);
                }
            }

            if (mEnforcePhone) {
                Matcher matcher = mPhonePattern.matcher(value);
                if (!matcher.matches()) {
                    throw new ValidationException(mEnforcePhoneValidationError);
                }

                String[] segments = value.split(",");

                for (String s : segments) {
                    if (s.length() < 7 || s.length() > 14) {
                        throw new ValidationException(mEnforcePhoneValidationError);
                    }
                }
            }

            if (mUppercaseCheck) {
                int upperCaseCount = 0;
                for (char c : value.toCharArray()) {

                    if (Character.isUpperCase(c)) {
                        upperCaseCount++;
                    }

                    if ((double) upperCaseCount / value.length() > 0.7) {
                        throw new ValidationException(mUppercaseCheckValidationError);
                    }
                }
            }

            if (mDigits) {
                if (!value.matches("[-+]?\\d*\\.?\\d+")) {
                    throw new ValidationException(mWithDigitsError);
                }
            }

        }
        if (mFutureDate) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                if (date.before(new Date())) {
                    throw new ValidationException(mWithFutureDateError);
                }
            } catch (Exception e) {
                throw new ValidationException(e.getMessage());
            }
        }
    }

    public boolean isRequired() {
        return mRequired;
    }

    public static class Builder {

        private boolean mRequired;
        private boolean mDisallowSeekingWords;

        private boolean mDisallowEmail;
        private boolean mEnforceEmail;
        private boolean mEnforcePhone;
        private boolean mDisallowWww;
        private boolean mEnforceSkype;
        private boolean mEnforcePerson;
        private boolean mUppercaseCheck;
        private boolean mDigit;
        private boolean mFutureDate;

        private Integer mMin;
        private Integer mMax;

        private Integer mMinLength;
        private Integer mMaxLength;

        private String mRequiredValidationError;
        private String mMinLengthValidationError;
        private String mMaxLengthValidationError;
        private String mMinValidationError;
        private String mMaxValidationError;
        private String mSeekingWordsValidationError;
        private String mEnforceEmailValidationError;
        private String mDisallowEmailValidationError;
        private String mEnforceSkypeValidationError;
        private String mUppercaseCheckValidationError;
        private String mEnforcePersonValidationError;
        private String mDisallowWwwValidationError;
        private String mEnforcePhoneValidationError;
        private String mWithDigitsError;
        private String mWithFutureDateError;

        public Builder withRequired(String error) {
            mRequired = true;
            mRequiredValidationError = error;
            return this;
        }

        public Builder withRequired(Boolean isRequired, String error) {
            if (isRequired) {
                mRequired = true;
                mRequiredValidationError = error;
            }
            return this;
        }

        public Builder withEnforceEmail(String error) {
            mEnforceEmail = true;
            mEnforceEmailValidationError = error;
            return this;
        }

        public Builder withDisallowEmail(String error) {
            mDisallowEmail = true;
            mDisallowEmailValidationError = error;
            return this;
        }

        public Builder withEnforcePhone(String error) {
            mEnforcePhone = true;
            mEnforcePhoneValidationError = error;
            return this;
        }

        public Builder withDisallowWww(String error) {
            mDisallowWww = true;
            mDisallowWwwValidationError = error;
            return this;
        }

        public Builder withDisallowSeekingWords(String error) {
            mDisallowSeekingWords = true;
            mSeekingWordsValidationError = error;
            return this;
        }

        public Builder withMinLength(int minLength, String error) {
            mMinLength = minLength;
            mMinLengthValidationError = error;
            return this;
        }

        public Builder withMaxLength(int maxLength, String error) {
            mMaxLength = maxLength;
            mMaxLengthValidationError = error;
            return this;
        }

        public Builder withEnforceSkype(String error) {
            mEnforceSkype = true;
            mEnforceSkypeValidationError = error;
            return this;
        }

        public Builder withEnforcePerson(String error) {
            mEnforcePerson = true;
            mEnforcePersonValidationError = error;
            return this;
        }

        public Builder withUppercaseCheck(String error) {
            mUppercaseCheck = true;
            mUppercaseCheckValidationError = error;
            return this;
        }

        public Builder withMin(int min, String error) {
            mMin = min;
            mMinValidationError = error;
            return this;
        }

        public Builder withMax(int max, String error) {
            mMax = max;
            mMaxValidationError = error;
            return this;
        }

        public Builder withDigits(String error) {
            mDigit = true;
            mWithDigitsError = error;
            return this;
        }

        public Builder withFutureDate(String error) {
            mFutureDate = true;
            mWithFutureDateError = error;
            return this;
        }

        public PostEditTextValidator build() {
            return new PostEditTextValidator(this);
        }

    }
}
