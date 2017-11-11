package com.bussure.student;

public interface GetCollegeListCallback
{
    void done(College[] colleges);
    void error(String message);
}
