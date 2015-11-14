package com.acpcoursera.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Following {

    @Id
    @GeneratedValue
    private Integer id;

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

    public void setInvite(boolean invite) {
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

}
