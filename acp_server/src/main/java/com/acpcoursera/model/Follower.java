package com.acpcoursera.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Follower {

    @Id
    @GeneratedValue
    private Integer id;

    private String username;
    private String followerName;
    private boolean accepted;
    private boolean majorData;
    private boolean minorData;

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

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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
                ", accepted=" + accepted +
                ", majorData=" + majorData +
                ", minorData=" + minorData +
                '}';
    }

}
