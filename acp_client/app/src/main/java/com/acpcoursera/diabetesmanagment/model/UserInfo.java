package com.acpcoursera.diabetesmanagment.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

public class UserInfo implements Parcelable {

    private static String TAG = UserInfo.class.getSimpleName();

    public static String TYPE_TEEN  = "teen";
    public static String TYPE_FOLLOWER  = "follower";

    private String userType;
    private String firstName;
    private String secondName;
    private Date birthDate;
    private String medicalRecordNumber;
    private String username;
    private String password;
    private String email;

    public UserInfo() {

    }

    protected UserInfo(Parcel in) {
        userType = in.readString();
        firstName = in.readString();
        secondName = in.readString();
        birthDate = (Date) in.readSerializable();
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
        dest.writeString(userType);
        dest.writeString(firstName);
        dest.writeString(secondName);
        dest.writeSerializable(birthDate);
        dest.writeString(medicalRecordNumber);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
    }

    // getter and setter of user info fields
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
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
        return "UserInfo{" +
                "userType='" + userType + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", birthDate='" + birthDate.toString() + '\'' +
                ", medicalRecordNumber='" + medicalRecordNumber + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
