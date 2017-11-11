package com.bussure.student;

import android.content.Context;
import android.content.SharedPreferences;

class StopLocalStore
{
    private static final String SP_STOP="stopDetails";
    private int length=-1;
    private SharedPreferences stopLocalDatabase;
    StopLocalStore(Context context)
    {
        stopLocalDatabase=context.getSharedPreferences(SP_STOP,0);
    }
    void storeDefaultStop(Stop stop)
    {
        System.out.println("-------------storeDefaultStop:"+stop.stopName+" "+stop.bus1+" "+stop.bus2+" "+stop.bus3);
        SharedPreferences.Editor spEditor=stopLocalDatabase.edit();
        spEditor.putString("Default", stop.stopName + "==" + Integer.toString(stop.bus1) + "==" + Integer.toString(stop.bus2) + "==" + Integer.toString(stop.bus3));
        spEditor.apply();
    }
    Stop getDefaultStop()
    {
        String _defaultStop = stopLocalDatabase.getString("Default", "");
        String defaultStop[]= _defaultStop.split("==");
        String stopName     = defaultStop[0];
        int bus1            = Integer.parseInt(defaultStop[1]);
        int bus2            = Integer.parseInt(defaultStop[2]);
        int bus3            = Integer.parseInt(defaultStop[3]);
        Stop stop           = new Stop(stopName,bus1,bus2,bus3);
        System.out.println("--------------getDefaultStop:" +stop.stopName+" "+stop.bus1+" "+stop.bus2+" "+stop.bus3);
        return  stop;
    }

    String[] getStopData()
    {
        System.out.println("--------getStopData was called in StopLocalStore Class--------");
        length =stopLocalDatabase.getInt("length",-1);
        if(length==-1)
        {
           return null;
        }
        else
        {
            int size=length+1;
            String[] stop=new String[size];
            for(int i=0;i<size;i++)
            {
                stop[i]=stopLocalDatabase.getString(Integer.toString(i),"");
              //  System.out.println("-------Stop in localStore-----"+stop[i]);
            }
            return  stop;
        }
    }
    void  clearStopData()
    {
        SharedPreferences.Editor spEditor=stopLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    void storeFetchStopData(final Stop[] stop)
    {
        for(int i=0;i<stop.length;i++)
        {
            storeStopData(stop[i]);
        }
    }
    void storeStopData(Stop stop)
    {
        SharedPreferences.Editor spEditor=stopLocalDatabase.edit();
        length=stopLocalDatabase.getInt("length",-1);
        spEditor.remove("length");
        length++;
        spEditor.putInt("length", length);
        spEditor.putString(Integer.toString(length), stop.stopName + "==" + Integer.toString(stop.bus1) + "==" + Integer.toString(stop.bus2) + "==" + Integer.toString(stop.bus3));
        spEditor.apply();
    }

}
