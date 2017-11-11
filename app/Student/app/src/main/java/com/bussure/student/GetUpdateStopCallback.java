package com.bussure.student;

public interface GetUpdateStopCallback {
    void done(Stop[] stops, Stop defaultStop);
    void error(String message);
}
