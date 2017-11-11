package com.bussure.student;

import android.content.Context;
import android.content.SharedPreferences;

class StudentLocalStore
{
    private static final String SP_STUDENT="studentDetails";
    private SharedPreferences studentLocalDatabase;
    StudentLocalStore(Context context)
    {
        studentLocalDatabase=context.getSharedPreferences(SP_STUDENT,0);
    }
    void storeStudentData(final Student student, final College college, final Stop stop)
    {
        SharedPreferences.Editor spEditor=studentLocalDatabase.edit();
        spEditor.putString("name", student.name);
        spEditor.putString("email",student.emailID);
        spEditor.putString("stop", student.stop);
        spEditor.putString("cName", college.cName);
        spEditor.putString("cEmailID", college.cEmailID);
        spEditor.putInt("bus1", stop.bus1);
        spEditor.putInt("bus2", stop.bus2);
        spEditor.putInt("bus3", stop.bus3);
        spEditor.apply();
    }
    Student getLoggedInStudent()
    {
        String name     =studentLocalDatabase.getString("name", "");
        String email    =studentLocalDatabase.getString("email","");
        String stop     =studentLocalDatabase.getString("stop","");
        String cName    =studentLocalDatabase.getString("cName","");
        Student student =new Student(name,cName,stop,email,null);
        return student;
    }
    public Stop getLoggedInStudentStop()
    {
        String name=studentLocalDatabase.getString("stop","");
        int bus1=studentLocalDatabase.getInt("bus1", -1);
        int bus2=studentLocalDatabase.getInt("bus2",-1);
        int bus3=studentLocalDatabase.getInt("bus3",-1);
        return new Stop(name,bus1,bus2,bus3);
    }
    College getLoggedInCollege()
    {
        String cName    =studentLocalDatabase.getString("cName","");
        String eEmail   =studentLocalDatabase.getString("cEmailID","");
        return new College(cName,eEmail);
    }

    void setStudentLoggedIn(final boolean loggedIn)
    {
        SharedPreferences.Editor spEditor=studentLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.apply();
    }
    void  clearStudentData()
    {
        SharedPreferences.Editor spEditor=studentLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }
    boolean isStudentLoggedIn()
    {
        return studentLocalDatabase.getBoolean("loggedIn", false);
    }
}
