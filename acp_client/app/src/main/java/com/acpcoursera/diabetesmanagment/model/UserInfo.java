package com.acpcoursera.diabetesmanagment.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {

    private static String TAG = UserInfo.class.getSimpleName();

    private String firstName;
    private String secondName;
    private String birthDate;
    private String medicalRecordNumber;
    private String username;
    private String password;
    private String email;

    public UserInfo() {

    }

    protected UserInfo(Parcel in) {
        firstName = in.readString();
        secondName = in.readString();
        birthDate = in.readString();
        medicalRecordNumber = in.readString();
        username = in.readString();
        password = in.readString();
        email = in.readString();
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
        dest.writeString(firstName);
        dest.writeString(secondName);
        dest.writeString(birthDate);
        dest.writeString(medicalRecordNumber);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
    }

    // getter and setter of user info fields
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "First name: " + firstName + ", Second name: " + secondName + ", Birth date: "
                + birthDate + ", MRN: " + medicalRecordNumber + ", User name: " + username
                + ", Password: " + password + ", e-mail: " + email;
    }

}
