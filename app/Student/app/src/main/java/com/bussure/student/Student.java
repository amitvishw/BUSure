package com.bussure.student;
public class Student
{
    String name,college,stop,emailID,password;

    public Student(String name, String college, String stop, String emailID, String password)
    {
        this.name = name;
        this.college = college;
        this.stop = stop;
        this.emailID = emailID;
        this.password = password;
    }

    public Student(String emailID, String password) {
        this.emailID = emailID;
        this.password = password;
    }
}
