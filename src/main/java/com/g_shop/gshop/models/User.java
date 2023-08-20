package com.g_shop.gshop.models;

import java.util.Map;

public class User {
    
    private String uID;
    private String name;
    private String phoneNumber;
    private String gender;
    private String dateOfBirth;

    public User(String uID, String name, String phoneNumber, String gender, String dateOfBirth) {
        this.uID = uID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

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

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gedner) {
        this.gender = gedner;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public static User fromJson(Map<String, Object> data) {
        String uID = (String)data.get("uID");
        String name = (String)data.get("name");
        String phoneNumber = (String)data.get("phoneNumber");
        String gender = (String)data.get("gender");
        String dateOfBirth = (String)data.get("dateOfBirth");

        return new User(uID, name, phoneNumber, gender, dateOfBirth);
    }


}
