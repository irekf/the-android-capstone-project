package com.acpcoursera.diabetesmanagment.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserSettings implements Parcelable {

    boolean majorData;
    boolean minorData;

    public UserSettings(boolean majorData, boolean minorData) {
        this.majorData = majorData;
        this.minorData = minorData;
    }

    protected UserSettings(Parcel in) {
        majorData = in.readByte() != 0;
        minorData = in.readByte() != 0;
    }

    public static final Creator<UserSettings> CREATOR = new Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel in) {
            return new UserSettings(in);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new UserSettings[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (majorData ? 1 : 0));
        dest.writeByte((byte) (minorData ? 1 : 0));
    }

    public boolean isMajorData() {
        return majorData;
    }

    public void setMajorData(boolean majorData) {
        this.majorData = majorData;
    }

    public boolean isMinorData() {
        return minorData;
    }

    public void setMinorData(boolean minorData) {
        this.minorData = minorData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSettings that = (UserSettings) o;

        if (majorData != that.majorData) return false;
        return minorData == that.minorData;

    }

    @Override
    public int hashCode() {
        int result = (majorData ? 1 : 0);
        result = 31 * result + (minorData ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "majorData=" + majorData +
                ", minorData=" + minorData +
                '}';
    }

}
