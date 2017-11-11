package com.bussure.college;


import java.io.Serializable;

public class College implements Serializable
{
    String collegeName,city,state,phoneNumber,emailID,password;
    int pinCode;

    public College(String collegeName, String city, String state, int pinCode,String phoneNumber, String emailID, String password)
    {
        this.collegeName = collegeName;
        this.city        = city;
        this.state       = state;
        this.pinCode     = pinCode;
        this.phoneNumber = phoneNumber;
        this.emailID     = emailID;
        this.password    = password;
    }
    public College(String emailID, String password)
    {
        this.emailID     = emailID;
        this.password    = password;
        this.collegeName = null;
        this.city        = null;
        this.state       = null;
        this.pinCode     = -1;
        this.phoneNumber = null;
    }
}
