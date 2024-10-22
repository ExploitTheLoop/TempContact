package com.animetone.incognitocontact.phone;

public class Phone {
    private String number;
    private String countryName;
    private String source;

    // Constructor
    public Phone(String number, String countryName, String source) {
        this.number = number;
        this.countryName = countryName;
        this.source = source;
    }

    // Getters and Setters
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}