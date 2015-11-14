package com.acpcoursera.diabetesmanagment.model;

import android.content.ContentValues;

import com.acpcoursera.diabetesmanagment.provider.DmContract;

public class Following {

    private static String TAG = Following.class.getSimpleName();

    private String username;
    private String followingName;
    private String followingFullName;
    private boolean pending;
    private boolean invite;
    private boolean majorData;
    private boolean minorData;

    public Following() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFollowingName() {
        return followingName;
    }

    public void setFollowingName(String followingName) {
        this.followingName = followingName;
    }

    public String getFollowingFullName() {
        return followingFullName;
    }

    public void setFollowingFullName(String followingFullName) {
        this.followingFullName = followingFullName;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean isInvite) {
        this.invite = invite;
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
        return "Following{" +
                "username='" + username + '\'' +
                ", followingName='" + followingName + '\'' +
                ", followingFullName='" + followingFullName + '\'' +
                ", pending=" + pending +
                ", invite=" + invite +
                ", majorData=" + majorData +
                ", minorData=" + minorData +
                '}';
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(DmContract.Following.USERNAME, username);
        values.put(DmContract.Following.FOLLOWING_NAME, followingName);
        values.put(DmContract.Following.FOLLOWING_FULL_NAME, followingFullName);
        values.put(DmContract.Following.PENDING, pending ? 1 : 0);
        values.put(DmContract.Following.INVITE, invite ? 1 : 0);
        values.put(DmContract.Following.MAJOR_DATA, majorData ? 1 : 0);
        values.put(DmContract.Following.MINOR_DATE, minorData ? 1 : 0);
        return values;
    }

}
