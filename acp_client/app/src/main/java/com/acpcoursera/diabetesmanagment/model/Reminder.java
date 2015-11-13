package com.acpcoursera.diabetesmanagment.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Reminder implements Parcelable {

    int id;
    int hourOfDay;
    int minute;
    boolean isEnabled;

    protected Reminder(Parcel in) {
        id = in.readInt();
        hourOfDay = in.readInt();
        minute = in.readInt();
        isEnabled = in.readByte() != 0;
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(hourOfDay);
        dest.writeInt(minute);
        dest.writeByte((byte) (isEnabled ? 1 : 0));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", hourOfDay=" + hourOfDay +
                ", minute=" + minute +
                ", isEnabled=" + isEnabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reminder reminder = (Reminder) o;

        return id == reminder.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

}
