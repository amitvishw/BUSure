package com.bussure.college;

import android.content.Context;
import android.content.SharedPreferences;

class StopLocalStore
{
    private static final String SP_STOP = "stopDetails";
    private int length                  = -1;
    private SharedPreferences stopLocalDatabase;
    StopLocalStore(Context context)
    {
        stopLocalDatabase = context.getSharedPreferences(SP_STOP,0);
    }
    void storeStopData(Stop stop)
    {
        SharedPreferences.Editor spEditor;
        spEditor = stopLocalDatabase.edit();
        length   = stopLocalDatabase.getInt("length",-1);
        spEditor.remove("length");
        length++;
        spEditor.putInt("length",length);
        spEditor.putString(Integer.toString(length), stop.stopName + "==" + Integer.toString(stop.bus1) + "==" + Integer.toString(stop.bus2) + "==" + Integer.toString(stop.bus3));
        spEditor.apply();
    }
    String[] getStopData()
    {
        System.out.println("--------getStopData was called in StopLocalStore Class--------");
        length = stopLocalDatabase.getInt("length",-1);
        if(length==-1)
        {
            return null;
        }
        else
        {
            int size      = length+1;
            String[] stop = new String[size];
            for(int i=0;i<size;i++)
            {
                stop[i] = stopLocalDatabase.getString(Integer.toString(i),"");
                System.out.println("-------Stop in localStore-----"+stop[i]);
            }
            return stop;
        }
    }
    void  clearStopData()
    {
        SharedPreferences.Editor spEditor;
        spEditor = stopLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    void updateStopData(Stop stop, int position)
    {
        System.out.println("--------updateStopData was called in StopLocalStore Class--------");
        SharedPreferences.Editor spEditor;
        spEditor = stopLocalDatabase.edit();
        spEditor.remove(Integer.toString(position));
        spEditor.putString(Integer.toString(position), stop.stopName + "==" + Integer.toString(stop.bus1) + "==" + Integer.toString(stop.bus2) + "==" + Integer.toString(stop.bus3));
        spEditor.apply();
        System.out.println("Updated Stop Data" + stopLocalDatabase.getString(Integer.toString(position), ""));
    }
    void storeFetchStopData(Stop[] stop)
    {
        for (Stop aStop : stop)
        {
            storeStopData(aStop);
        }
    }
}
