package com.bussure.college;

import java.io.Serializable;

public class Stop implements Serializable
{
    String stopName;
    int bus1,bus2,bus3;
    public Stop(String stopName, int bus1, int bus2, int bus3)
    {
        this.stopName = stopName;
        this.bus1     = bus1;
        this.bus2     = bus2;
        this.bus3     = bus3;
    }
}
