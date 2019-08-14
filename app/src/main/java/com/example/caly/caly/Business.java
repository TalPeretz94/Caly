package com.example.caly.caly;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Business {

    String name;
    String category;
    String timeOfReservation;
    String mIdOfUser;
    String openingTime, closingTime;
    String phone, address;
    boolean filledUpDetails;
    String imageUrl;



    public Business() {

    }

    public Business(String name, String category, String timeOfReservation, String openingTime, String closingTime, String address, String phone) {

        this.name = name;
        this.category = category;
        this.timeOfReservation = timeOfReservation;
        this.filledUpDetails = false;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.phone = phone;
        this.address = address;


    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimeOfReservation() {
        return timeOfReservation;
    }

    public void setTimeOfReservation(String timeOfReservation) {
        this.timeOfReservation = timeOfReservation;
    }
    public String getmIdOfUser() {
        return mIdOfUser;
    }

    public void setmIdOfUser(String mIdOfUser) {
        this.mIdOfUser = mIdOfUser;
    }

    public boolean isFilledUpDetails() {
        return filledUpDetails;
    }

    public void setFilledUpDetails(boolean filledUpDetails) {
        this.filledUpDetails = filledUpDetails;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
