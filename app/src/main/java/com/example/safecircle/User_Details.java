package com.example.safecircle;

import com.google.firebase.database.PropertyName;

public class User_Details {
    public String Username;
    public String Password;
    public String Address;
    public String Phone_number;
    public String Emergency_contact;

    public User_Details(){
    }
    public User_Details(String Username,String Password, String Address, String Phone_number, String Emergency_contact){
        this.Username = Username;
        this.Password = Password;
        this.Address = Address;
        this.Phone_number = Phone_number;
        this.Emergency_contact = Emergency_contact;

    }
    @PropertyName("Username")
    public String getUsername(){
        return Username;
    }
    @PropertyName("Password")
    public String getPassword(){
        return Password;
    }
    @PropertyName("Address")
    public String getAddress(){ return Address;}
    @PropertyName("Phone_number")
    public String getPhone_number(){ return Phone_number;}
    @PropertyName("Emergency_contact")
    public String getEmergency_contact(){ return Emergency_contact; }

    @PropertyName("Username")
    public void setUsername(String Username){
        this.Username=Username;
    }

    @PropertyName("Password")
    public void setPassword(String Password){
        this.Password=Password;
    }

    @PropertyName("Address")
    public void setAddress(String Address){
        this.Address = Address;
    }

    @PropertyName("Phone_number")
    public void setPhone_number(String Phone_number) {
        this.Phone_number = Phone_number;
    }

    @PropertyName("Emergency_contact")
    public void setEmergency_contact(String Emergency_contact) {
        this.Emergency_contact = Emergency_contact;
    }

}
