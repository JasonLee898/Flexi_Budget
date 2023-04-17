package com.example.myapplication;

public class User {
    private String registration_name, registration_email, date, registration_phone_number;


    public User(String stringName, String stringEmail, String stringDate, String stringPhone){

    }
    public String getRegistration_name() {
        return registration_name;
    }

    public void setRegistration_name(String registration_name) {
        this.registration_name = registration_name;
    }

    public String getRegistration_email() {
        return registration_email;
    }

    public void setRegistration_email(String registration_email) {
        this.registration_email = registration_email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRegistration_phone_number() {
        return registration_phone_number;
    }

    public void setRegistration_phone_number(String registration_phone_number) {
        this.registration_phone_number = registration_phone_number;
    }
}
