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
    private boolean pending;
    private boolean isInvite;
    private boolean majorData;
    private boolean minorData;

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

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public boolean isInvite() {
        return isInvite;
    }

    public void setIsInvite(boolean isInvite) {
        this.isInvite = isInvite;
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
                ", pending=" + pending +
                ", isInvite=" + isInvite +
                ", majorData=" + majorData +
                ", minorData=" + minorData +
                '}';
    }

}
