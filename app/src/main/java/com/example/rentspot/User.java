package com.example.rentspot;

public class User {
    public String fName, lName, username, address, email, password, phoneNumber;

    public User(){

    }

    public User(String fName, String lName, String username, String address, String email,
                String password, String phoneNumber){
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.address = address;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}