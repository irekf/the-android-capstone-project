package com.acpcoursera.diabetesmanagment.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.acpcoursera.diabetesmanagment.provider.DmContract;

import java.sql.Timestamp;

public class CheckInData implements Parcelable {

    private static String TAG = CheckInData.class.getSimpleName();

    private String username;

    private float sugarLevel;
    private Timestamp sugarLevelTime;
    private String meal;
    private Timestamp mealTime;
    private float insulinDosage;
    private Timestamp insulinTime;

    private int moodLevel;
    private int stressLevel;
    private int energyLevel;
    private String sugarLevelWho;
    private String sugarLevelWhere;
    private String feelings;

    private Timestamp checkInTimestamp;

    public CheckInData() {

    }

    protected CheckInData(Parcel in) {
        sugarLevel = in.readFloat();
        sugarLevelTime = (Timestamp) in.readSerializable();
        meal = in.readString();
        mealTime = (Timestamp) in.readSerializable();
        insulinDosage = in.readFloat();
        insulinTime = (Timestamp) in.readSerializable();
        moodLevel = in.readInt();
        stressLevel = in.readInt();
        energyLevel = in.readInt();
        sugarLevelWho = in.readString();
        sugarLevelWhere = in.readString();
        feelings = in.readString();
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
        dest.writeSerializable(sugarLevelTime);
        dest.writeString(meal);
        dest.writeSerializable(mealTime);
        dest.writeFloat(insulinDosage);
        dest.writeSerializable(insulinTime);
        dest.writeInt(moodLevel);
        dest.writeInt(stressLevel);
        dest.writeInt(energyLevel);
        dest.writeString(sugarLevelWho);
        dest.writeString(sugarLevelWhere);
        dest.writeString(feelings);
        dest.writeSerializable(checkInTimestamp);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(float sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public Timestamp getSugarLevelTime() {
        return sugarLevelTime;
    }

    public void setSugarLevelTime(Timestamp sugarLevelTime) {
        this.sugarLevelTime = sugarLevelTime;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public Timestamp getMealTime() {
        return mealTime;
    }

    public void setMealTime(Timestamp mealTime) {
        this.mealTime = mealTime;
    }

    public float getInsulinDosage() {
        return insulinDosage;
    }

    public void setInsulinDosage(float insulinDosage) {
        this.insulinDosage = insulinDosage;
    }

    public Timestamp getInsulinTime() {
        return insulinTime;
    }

    public void setInsulinTime(Timestamp insulinTime) {
        this.insulinTime = insulinTime;
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

    public String getSugarLevelWho() {
        return sugarLevelWho;
    }

    public void setSugarLevelWho(String sugarLevelWho) {
        this.sugarLevelWho = sugarLevelWho;
    }

    public String getSugarLevelWhere() {
        return sugarLevelWhere;
    }

    public void setSugarLevelWhere(String sugarLevelWhere) {
        this.sugarLevelWhere = sugarLevelWhere;
    }

    public String getFeelings() {
        return feelings;
    }

    public void setFeelings(String feelings) {
        this.feelings = feelings;
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
                "userName=" + username +
                ", sugarLevel=" + sugarLevel +
                ", sugarLevelTime='" + sugarLevelTime + '\'' +
                ", meal='" + meal + '\'' +
                ", mealTime='" + mealTime + '\'' +
                ", insulinDosage=" + insulinDosage +
                ", insulinTime='" + insulinTime + '\'' +
                ", moodLevel=" + moodLevel +
                ", stressLevel=" + stressLevel +
                ", energyLevel=" + energyLevel +
                ", sugarLevelWho=" + sugarLevelWho +
                ", sugarLevelWhere=" + sugarLevelWhere +
                ", feelings=" + feelings +
                ", checkInTimestamp='" + checkInTimestamp + '\'' +
                '}';
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(DmContract.CheckInData.USERNAME, username);
        values.put(DmContract.CheckInData.SUGAR_LEVEL, sugarLevel);
        values.put(DmContract.CheckInData.SUGAR_LEVEL_TIME, sugarLevelTime.toString());
        values.put(DmContract.CheckInData.MEAL, meal);
        values.put(DmContract.CheckInData.MEAL_TIME, mealTime.toString());
        values.put(DmContract.CheckInData.INSULIN_DOSAGE, insulinDosage);
        values.put(DmContract.CheckInData.INSULIN_TIME, insulinTime.toString());
        values.put(DmContract.CheckInData.MOOD_LEVEL, moodLevel);
        values.put(DmContract.CheckInData.STRESS_LEVEL, stressLevel);
        values.put(DmContract.CheckInData.ENERGY_LEVEL, energyLevel);
        values.put(DmContract.CheckInData.SUGAR_LEVEL_WHO, sugarLevelWho);
        values.put(DmContract.CheckInData.SUGAR_LEVEL_WHERE, sugarLevelWhere);
        values.put(DmContract.CheckInData.FEELINGS, feelings);
        values.put(DmContract.CheckInData.CHECK_IN_TIME, checkInTimestamp.toString());
        return values;
    }

}
