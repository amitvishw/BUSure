package com.bussure.college;
public interface GetConfirmCallback
{
    void done(College college);
    void error(String message);
}
