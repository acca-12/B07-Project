package com.example.b07_project;

public class StudentAccount extends Account{
    public StudentAccount(String email, String password, String uID){
        this.email = email;
        this.password = password;
        this.uID = uID; //Needed to retrieve data from firebase
        this.isAdmin = false; //Self-explanatory
    }
}
