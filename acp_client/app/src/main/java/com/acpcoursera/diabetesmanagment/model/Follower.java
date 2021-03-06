package com.acpcoursera.diabetesmanagment.model;

import android.content.ContentValues;

import com.acpcoursera.diabetesmanagment.provider.DmContract;

public class Follower {

    private static String TAG = Follower.class.getSimpleName();

    private String username;
    private String followerName;
    private String followerFullName;
    private boolean teen;
    private boolean accepted;
    private boolean pending;
    private boolean majorData;
    private boolean minorData;

    public Follower() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }

    public String getFollowerFullName() {
        return followerFullName;
    }

    public void setFollowerFullName(String followerFullName) {
        this.followerFullName = followerFullName;
    }

    public boolean isTeen() {
        return teen;
    }

    public void setTeen(boolean teen) {
        this.teen = teen;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
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
    public String toString() {
        return "Follower{" +
                "username='" + username + '\'' +
                ", followerName='" + followerName + '\'' +
                ", followerFullName='" + followerFullName + '\'' +
                ", teen=" + teen +
                ", accepted=" + accepted +
                ", pending=" + pending +
                ", majorData=" + majorData +
                ", minorData=" + minorData +
                '}';
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(DmContract.Followers.USERNAME, username);
        values.put(DmContract.Followers.FOLLOWER_NAME, followerName);
        values.put(DmContract.Followers.FOLLOWER_FULL_NAME, followerFullName);
        values.put(DmContract.Followers.TEEN, teen ? 1 : 0);
        values.put(DmContract.Followers.ACCEPTED, accepted ? 1 : 0);
        values.put(DmContract.Followers.PENDING, pending ? 1 : 0);
        values.put(DmContract.Followers.MAJOR_DATA, majorData ? 1 : 0);
        values.put(DmContract.Followers.MINOR_DATE, minorData ? 1 : 0);
        return values;
    }

}
