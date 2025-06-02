package com.duzceuni.denemeapplication.entity;

public class Users {

    private String id;
    private String firstname;
    private String lastname;
    private String gender;
    private String birthOfDate;
    private String email;
    private String phoneNumber;
    private String registeredAt;

    private boolean isBanned;
    public Users() {}
    public Users(String id, String firstname, String lastname, String gender, String birthOfDate, String email, String phoneNumber) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.birthOfDate = birthOfDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Users(boolean isBanned, String registeredAt, String phoneNumber, String email, String birthOfDate, String gender, String lastname, String firstname, String id) {
        this.isBanned = isBanned;
        this.registeredAt = registeredAt;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthOfDate = birthOfDate;
        this.gender = gender;
        this.lastname = lastname;
        this.firstname = firstname;
        this.id = id;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean isBanned) {
        isBanned = isBanned;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthOfDate() {
        return birthOfDate;
    }

    public void setBirthOfDate(String birthOfDate) {
        this.birthOfDate = birthOfDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", gender='" + gender + '\'' +
                ", birthOfDate='" + birthOfDate + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registeredAt ='" + registeredAt + '\'' +
                ", isBanned = '" + isBanned +
                '}';
    }
}
