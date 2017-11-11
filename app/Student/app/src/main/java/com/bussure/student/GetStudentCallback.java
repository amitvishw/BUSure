package com.bussure.student;

public interface GetStudentCallback
{
    void done(Student student, College college, Stop stop);
    void error(String message);
}
