package com.bussure.college;

interface GetCollegeCallback
{
    void done(College college, int code);
    void error(String message);
}
