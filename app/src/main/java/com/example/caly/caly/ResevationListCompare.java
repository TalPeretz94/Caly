package com.example.caly.caly;

import java.util.Date;

public class ResevationListCompare {
    private Date date;
    private String myTime;

    public ResevationListCompare(Date date, String myTime) {
        this.date = date;
        this.myTime = myTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMyTime() {
        return myTime;
    }

    public void setMyTime(String myTime) {
        this.myTime = myTime;
    }
}
