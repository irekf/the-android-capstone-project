package com.acpcoursera.diabetesmanagment.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {

    private static String TAG = UserInfo.class.getSimpleName();

    private String mFirstName;
    private String mSecondName;
    private String mBirthDate;
    private String mMedicalRecordNumber;
    private String mUserName;
    private String mPassword;
    private String mEmail;

    public UserInfo() {

    }

    protected UserInfo(Parcel in) {
        mFirstName = in.readString();
        mSecondName = in.readString();
        mBirthDate = in.readString();
        mMedicalRecordNumber = in.readString();
        mUserName = in.readString();
        mPassword = in.readString();
        mEmail = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mSecondName);
        dest.writeString(mBirthDate);
        dest.writeString(mMedicalRecordNumber);
        dest.writeString(mUserName);
        dest.writeString(mPassword);
        dest.writeString(mEmail);
    }

    // getter and setter of user info fields
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getSecondName() {
        return mSecondName;
    }

    public void setSecondName(String secondName) {
        mSecondName = secondName;
    }

    public String getBirthDate() {
        return mBirthDate;
    }

    public void setBirthDate(String birthDate) {
        mBirthDate = birthDate;
    }

    public String getMedicalRecordNumber() {
        return mMedicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        mMedicalRecordNumber = medicalRecordNumber;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    @Override
    public String toString() {
        return "First name: " + mFirstName + ", Second name: " + mSecondName + ", Birth date: "
                + mBirthDate + ", MRN: " + mMedicalRecordNumber + ", User name: " + mUserName
                + ", Password: " + mPassword + ", e-mail: " + mEmail;
    }

}
