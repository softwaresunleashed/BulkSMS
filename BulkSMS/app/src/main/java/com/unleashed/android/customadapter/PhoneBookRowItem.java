package com.unleashed.android.customadapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gupta on 7/17/2015.
 */
public class PhoneBookRowItem implements Parcelable {

    private String phone_user_name;
    private String phone_user_number;
    private boolean switch_state;


    public static final Creator<PhoneBookRowItem> CREATOR = new Creator<PhoneBookRowItem>() {
        @Override
        public PhoneBookRowItem createFromParcel(Parcel in) {
            return new PhoneBookRowItem(in);
        }

        @Override
        public PhoneBookRowItem[] newArray(int size) {
            return new PhoneBookRowItem[size];
        }
    };

    public String getPhoneUserName(){
        return phone_user_name;
    }
    public String getPhoneNumber(){
        return phone_user_number;
    }

    public void setPhoneName(String user_name){
        phone_user_name = user_name;
    }
    public void setPhoneNumber(String user_number){
        phone_user_number = user_number;
    }

    public void setState(boolean state){
        switch_state = state;
    }
    public boolean getState(){
        return switch_state;
    }
    public boolean toggleState(){
        switch_state=(!switch_state);
        return switch_state;
    }


    public PhoneBookRowItem()
    {

    }


    private PhoneBookRowItem(Parcel in) {
        phone_user_name = in.readString();
        phone_user_number = in.readString();
        switch_state = (in.readByte() != 0);  // switch_state == true if byte != 0
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(phone_user_name);
        parcel.writeString(phone_user_number);
        parcel.writeByte((byte) (switch_state ? 1 : 0));
    }
}
