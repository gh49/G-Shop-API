package com.g_shop.gshop.models;

public class User {
    
    private String uID;
    private String name;
    private String phoneNumber;
    private String gedner;
    private String dateOfBirth;

    public String getUID() {
        return this.uID;
    }

    public void setUID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGedner() {
        return this.gedner;
    }

    public void setGedner(String gedner) {
        this.gedner = gedner;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


}
