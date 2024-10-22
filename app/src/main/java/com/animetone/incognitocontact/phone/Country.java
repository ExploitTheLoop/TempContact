package com.animetone.incognitocontact.phone;

public class Country {
    private String country;
    private String source;
    private int count;

    // Getters and setters
    public String getName() {
        return country;
    }

    public void setName(String name) {
        this.country = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

