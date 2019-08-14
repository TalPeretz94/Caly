package com.example.caly.caly;

public class User {

    private String name;
    private String address;
    private String phone;
    private String imageUrl;
    boolean filledUpDetails;



    private String status;

    public User() {
        this.name = "";
        this.phone = "";
        this.filledUpDetails=false;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFilledUpDetails() {
        return filledUpDetails;
    }

    public void setFilledUpDetails(boolean filledUpDetails) {
        this.filledUpDetails = filledUpDetails;
    }
}
