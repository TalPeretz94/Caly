package com.example.caly.caly;


import java.util.Date;

public class Reservation {

    String mInitialize;
    String mCustomerName;
    int mHour;
    int mMinute;
    String mUserId;
    String mDate;
    int mDay;
    int mMonth;
    int mYear;
    long mDateInMilli;
    String mCustomerPhone;
    Date compareDate;


    public Reservation (String mCustomerName,int hour, int minute, String userId, String date, int year, int month, int day, long dateInMilli, String customerPhone) {
        this.mCustomerName = mCustomerName;
        this.mDate = date;
        this.mHour = hour;
        this.mMinute = minute;
        this.mUserId = userId;
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mDateInMilli = dateInMilli;
        this.mCustomerPhone = customerPhone;
    }

    public Date getCompareDate() {
        return compareDate;
    }

    public void setCompareDate(Date compareDate) {
        this.compareDate = compareDate;
    }

    public Reservation(String timeOfRes) {
        this.mInitialize = timeOfRes;
    }

    public String getTimeOfRes() {
        return mInitialize;
    }

    public void setTimeOfRes(String timeOfRes) {
        this.mInitialize = timeOfRes;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmInitialize() {
        return mInitialize;
    }

    public String getmCustomerName() {
        return mCustomerName;
    }

    public int getmHour() {
        return mHour;
    }

    public int getmMinute() {
        return mMinute;
    }

    public String getmUserId() {
        return mUserId;
    }

    public long getmDateInMilli() {
        return mDateInMilli;
    }

    public String getTime(){
        String t = String.format("%02d:%02d", getmHour(),getmMinute());
        return t;
    }

    public String getmCustomerPhone() {
        return mCustomerPhone;
    }

    public void setmCustomerPhone(String mCustomerPhone) {
        this.mCustomerPhone = mCustomerPhone;
    }
}
