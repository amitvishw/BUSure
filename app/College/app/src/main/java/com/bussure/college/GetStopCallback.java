package com.bussure.college;
public interface GetStopCallback
{
    void done(Stop stop);
    void error(String message);
}
