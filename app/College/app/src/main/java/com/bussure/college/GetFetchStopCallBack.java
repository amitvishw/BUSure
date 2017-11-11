package com.bussure.college;

public interface GetFetchStopCallBack
{
    void done(Stop[] stop);
    void error(String message);
}
