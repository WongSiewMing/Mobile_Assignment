package com.example.ywh.locality.Model;

import java.util.Date;
import java.util.List;

public class User {

    private String firstName;
    private String lastName;
    private Date birthday;
    private String gender;
    private String email;
    private Address address;
    private String profilePic;
    private List<String> familyFriends;

    public User(){
    }

    public User(String firstName, String lastName, String email, String gender, Date birthday) {
        this.firstName= firstName;
        this.lastName= lastName;
        this.email = email;
        this.gender = gender;
        this.birthday= birthday;
        this.profilePic = "default";
    }

    public Address getAddress(){
        return address;
    }

    public void setAddress(){
        this.address = address;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {

        return birthday;
    }

    public void setBirthday(Date birthday) {

        this.birthday = birthday; }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public List<String> getFamilyFriends() {
        return familyFriends;
    }

    public void setFamilyFriends(List<String> familyFriends) {
        this.familyFriends = familyFriends;
    }
}

