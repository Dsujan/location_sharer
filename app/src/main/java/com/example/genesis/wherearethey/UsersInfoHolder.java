package com.example.genesis.wherearethey;

public class UsersInfoHolder {

    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String latitudeLocation;
    private final String longitudeLocation;
    private final String profileImgUrl;
    private final String isShared;

    public UsersInfoHolder(String firstName, String lastName, String phoneNumber, String latitudeLocation, String longitudeLocation, String profileImgUrl, String isShared) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.latitudeLocation = latitudeLocation;
        this.longitudeLocation = longitudeLocation;
        this.profileImgUrl = profileImgUrl;
        this.isShared = isShared;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLatitudeLocation() {
        return latitudeLocation;
    }

    public String getLongitudeLocation() {
        return longitudeLocation;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public String getIsShared() {
        return isShared;
    }
}
