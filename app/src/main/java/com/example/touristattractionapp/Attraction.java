package com.example.touristattractionapp;

import java.io.Serializable;

public class Attraction implements Serializable {

    // Properties
    private String name;
    private String address;
    private String phone;
    private String website;
    private String description;
    private double pricing;
    private boolean favorite;
    private String image;

    // Constructor
    public Attraction(String name, String address, String phone, String website, String description, double pricing, boolean favorite, String image) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.description = description;
        this.pricing = pricing;
        this.favorite = favorite;
        this.image = image;
    }

    public Attraction(String name, boolean favorite) {
        this.name = name;
        this.favorite = favorite;
    }

    // Methods
    @Override
    public String toString() {
        return "Attraction{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                ", description='" + description + '\'' +
                ", pricing=" + pricing +
                ", favorite=" + favorite +
                ", image='" + image + '\'' +
                '}';
    }

    // Getters and Setters
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPricing() {
        return pricing;
    }

    public void setPricing(double pricing) {
        this.pricing = pricing;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
