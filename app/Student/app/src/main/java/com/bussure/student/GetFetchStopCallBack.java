package com.bussure.student;

public interface GetFetchStopCallBack
{
    void done(Stop[] stop);
    void error(String message);
}
