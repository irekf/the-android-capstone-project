package com.acpcoursera.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class CheckInData {

    @Id
    @GeneratedValue
    private Integer id;

    private String username;

    private float sugarLevel;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss.S")
    private Timestamp sugarLevelTime;
    private String meal;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss.S")
    private Timestamp mealTime;
    private float insulinDosage;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss.S")
    private Timestamp insulinTime;

    private int moodLevel;
    private int stressLevel;
    private int energyLevel;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss.S")
    private Timestamp checkInTimestamp;

    public CheckInData() {

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
                ", checkInTimestamp='" + checkInTimestamp + '\'' +
                '}';
    }

}
