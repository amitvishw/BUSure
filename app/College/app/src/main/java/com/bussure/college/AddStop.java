package com.bussure.college;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddStop extends AppCompatActivity
{

    ImageButton bBackAddStop;
    EditText etStopName,etBusNo1,etBusNo2,etBusNo3;
    String stopName,busNo1,busNo2,busNo3;
    StopLocalStore stopLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstop);
        bBackAddStop    = (ImageButton)findViewById(R.id.bBackAddStop);
        etStopName      = (EditText)findViewById(R.id.etStopName);
        etBusNo1        = (EditText)findViewById(R.id.etBus1);
        etBusNo2        = (EditText)findViewById(R.id.etBus2);
        etBusNo3        = (EditText)findViewById(R.id.etBus3);
    }
    public void onClickBackInAddStop(View view)
    {
        finish();
        startMainActivityActivity();
    }
    public void onClickAddStop(View view)
    {
        stopName        = etStopName.getText().toString();
        busNo1          = etBusNo1.getText().toString();
        busNo2          = etBusNo2.getText().toString();
        busNo3          = etBusNo3.getText().toString();
        if(stopName.length()==0||(busNo1.length()==0&&busNo2.length()==0&&busNo3.length()==0))
        {
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(AddStop.this,R.style.AlertDialogStyle);
            dialogBuilder.setMessage("Stop name and at least one bus number is required.");
            dialogBuilder.setTitle("Missing Fields!");
            dialogBuilder.setPositiveButton("Ok", null);
            dialogBuilder.show();
        }
        else
        {
            if(busNo1.length()==0)
                busNo1="0";
            if(busNo2.length()==0)
                busNo2="0";
            if(busNo3.length()==0)
                busNo3="0";
            Stop stop                           = new Stop(stopName,Integer.parseInt(busNo1),Integer.parseInt(busNo2),Integer.parseInt(busNo3));
            CollegeLocalStore collegeLocalStore = new CollegeLocalStore(getApplicationContext());
            College college                     = collegeLocalStore.getLoggedInCollege();
            addStop(stop,college);
        }
    }
    private void addStop(Stop stop,College college)
    {
        System.out.println("--------addStop was called in AddStop Activity--------");
        ServerRequest serverRequest = new ServerRequest(this, "Adding stop...");
        serverRequest.storeStopDataInBackground(stop,college, new GetStopCallback() {
                    @Override
                    public void done(Stop stop) {
                        System.out.println("--------Done was called in AddStop Activity--------");
                        stopLocalStore  = new StopLocalStore(getApplicationContext());
                        stopLocalStore.storeStopData(stop);
                        showSuccessMessage();
                    }

                    @Override
                    public void error(String message) {
                        System.out.println("--------Error was called in AddStop Activity---------");
                        showErrorMessage(message);
                    }
                }
        );
    }
    private void showErrorMessage(String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddStop.this,R.style.AlertDialogStyle);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK",null);
        dialogBuilder.setTitle("Something Went Wrong!");
        dialogBuilder.show();
    }
    private void showSuccessMessage()
    {
        System.out.println("--------showSuccessMessage was called in AddStop Activity--------");
        final AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(AddStop.this);
        dialogBuilder.setMessage("Stop has been Added successfully.");
        dialogBuilder.setTitle("Added Successfully");
        dialogBuilder.setPositiveButton("Add Another", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etStopName.setText("");
                etBusNo1.setText("");
                etBusNo2.setText("");
                etBusNo3.setText("");
            }
        });
        dialogBuilder.setNegativeButton("Back To Home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startMainActivityActivity();
            }
        });
        dialogBuilder.show();
    }
    private void startMainActivityActivity()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
