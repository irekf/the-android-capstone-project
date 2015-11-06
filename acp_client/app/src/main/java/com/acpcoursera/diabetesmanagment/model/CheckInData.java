package com.acpcoursera.diabetesmanagment.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class CheckInData implements Parcelable {

    private static String TAG = CheckInData.class.getSimpleName();

    private float sugarLevel;
    private String sugarLevelTime;
    private String meal;
    private String mealTime;
    private float insulinDosage;
    private String insulinAdministrationTime;

    private int moodLevel;
    private int stressLevel;
    private int energyLevel;

    private Timestamp checkInTimestamp;

    public CheckInData() {

    }

    protected CheckInData(Parcel in) {
        sugarLevel = in.readFloat();
        sugarLevelTime = in.readString();
        meal = in.readString();
        mealTime = in.readString();
        insulinDosage = in.readFloat();
        insulinAdministrationTime = in.readString();
        moodLevel = in.readInt();
        stressLevel = in.readInt();
        energyLevel = in.readInt();
        checkInTimestamp = (Timestamp) in.readSerializable();
    }

    public static final Creator<CheckInData> CREATOR = new Creator<CheckInData>() {
        @Override
        public CheckInData createFromParcel(Parcel in) {
            return new CheckInData(in);
        }

        @Override
        public CheckInData[] newArray(int size) {
            return new CheckInData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(sugarLevel);
        dest.writeString(sugarLevelTime);
        dest.writeString(meal);
        dest.writeString(mealTime);
        dest.writeFloat(insulinDosage);
        dest.writeString(insulinAdministrationTime);
        dest.writeInt(moodLevel);
        dest.writeInt(stressLevel);
        dest.writeInt(energyLevel);
        dest.writeSerializable(checkInTimestamp);
    }

    public float getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(float sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public String getSugarLevelTime() {
        return sugarLevelTime;
    }

    public void setSugarLevelTime(String sugarLevelTime) {
        this.sugarLevelTime = sugarLevelTime;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public float getInsulinDosage() {
        return insulinDosage;
    }

    public void setInsulinDosage(float insulinDosage) {
        this.insulinDosage = insulinDosage;
    }

    public String getInsulinAdministrationTime() {
        return insulinAdministrationTime;
    }

    public void setInsulinAdministrationTime(String insulinAdministrationTime) {
        this.insulinAdministrationTime = insulinAdministrationTime;
    }

    public int getMoodLevel() {
        return moodLevel;
    }

    public void setMoodLevel(int moodLevel) {
        this.moodLevel = moodLevel;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(int stressLevel) {
        this.stressLevel = stressLevel;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(int energyLevel) {
        this.energyLevel = energyLevel;
    }

    public Timestamp getCheckInTimestamp() {
        return checkInTimestamp;
    }

    public void setCheckInTimestamp(Timestamp checkInTimestamp) {
        this.checkInTimestamp = checkInTimestamp;
    }

    @Override
    public String toString() {
        return "CheckInData{" +
                "sugarLevel=" + sugarLevel +
                ", sugarLevelTime='" + sugarLevelTime + '\'' +
                ", meal='" + meal + '\'' +
                ", mealTime='" + mealTime + '\'' +
                ", insulinDosage=" + insulinDosage +
                ", insulinAdministrationTime='" + insulinAdministrationTime + '\'' +
                ", moodLevel=" + moodLevel +
                ", stressLevel=" + stressLevel +
                ", energyLevel=" + energyLevel +
                ", checkInTimestamp='" + checkInTimestamp.toString() + '\'' +
                '}';
    }

}
