package com.example.caly.caly;

import java.util.ArrayList;

class ReservationCollection {
    public String date="";
    public String name="";
    public String subject="";
    public String description="";


    public static ArrayList<ReservationCollection> date_collection_arr;
    public ReservationCollection(String date, String name, String subject, String description){

        this.date=date;
        this.name=name;
        this.subject=subject;
        this.description= description;

    }
}
