package com.bussure.college;

import android.content.Context;
import android.content.SharedPreferences;

class CollegeLocalStore
{
    private static final String SP_COLLEGE="collegeDetails";
    private SharedPreferences collegeLocalDatabase;
    CollegeLocalStore(Context context)
    {
        collegeLocalDatabase = context.getSharedPreferences(SP_COLLEGE,0);
    }
    void storeCollegeData(College college)
    {
        SharedPreferences.Editor spEditor = collegeLocalDatabase.edit();
        spEditor.putString("collegeName", college.collegeName);
        spEditor.putString("city", college.city);
        spEditor.putString("state", college.state);
        spEditor.putString("phoneNumber", college.phoneNumber);
        spEditor.putString("emailID", college.emailID);
        spEditor.putString("password",college.password);
        spEditor.putInt("pinCode", college.pinCode);
        spEditor.apply();
    }
    void storeCollegeLogoURI(String uri)
    {
        SharedPreferences.Editor spEditor = collegeLocalDatabase.edit();
        spEditor.putString("collegeLogoURI",uri);
        spEditor.apply();
    }
    String getCollegeLogoURI()
    {
        return collegeLocalDatabase.getString("collegeLogoURI",null);
    }
    College getLoggedInCollege()
    {
        String collegeName  = collegeLocalDatabase.getString("collegeName", "");
        String city         = collegeLocalDatabase.getString("city","");
        String state        = collegeLocalDatabase.getString("state","");
        int pinCode         = collegeLocalDatabase.getInt("pinCode", -1);
        String phoneNumber  = collegeLocalDatabase.getString("phoneNumber", "");
        String emailID      = collegeLocalDatabase.getString("emailID", "");
        String password     = collegeLocalDatabase.getString("password","");

        return new College(collegeName,city,state,pinCode,phoneNumber,emailID,password);
    }
    void setCollegeLoggedIn(boolean loggedIn)
    {
        SharedPreferences.Editor spEditor=collegeLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.apply();
    }
    void  clearCollegeData()
    {
        SharedPreferences.Editor spEditor=collegeLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }
    boolean isCollegeLoggedIn()
    {
        return collegeLocalDatabase.getBoolean("loggedIn", false);
    }
}
