package com.example.caly.caly;

import android.os.Parcel;
import android.os.Parcelable;

public class ReservationViewOnList implements Parcelable {

    private String name;
    private String time;

    public ReservationViewOnList(String name, String time) {
        this.name = name;
        this.time = time;
    }

    protected ReservationViewOnList(Parcel in) {
        name = in.readString();
        time = in.readString();
    }

    public static final Creator<ReservationViewOnList> CREATOR = new Creator<ReservationViewOnList>() {
        @Override
        public ReservationViewOnList createFromParcel(Parcel in) {
            return new ReservationViewOnList(in);
        }

        @Override
        public ReservationViewOnList[] newArray(int size) {
            return new ReservationViewOnList[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(time);
    }
}
