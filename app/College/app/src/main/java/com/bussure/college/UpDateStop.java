package com.bussure.college;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UpDateStop extends AppCompatActivity
{
    EditText etBus1,etBus2,etBus3;
    TextView tvStopName;
    Button bupDateStop;
    Stop stop;
    String busNo1,busNo2,busNo3;
    int position;
    StopLocalStore stopLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatestop);
        System.out.println("-------------UpdateStop Started-----------------");
        tvStopName      = (TextView)findViewById(R.id.tvStopNameUpdate);
        etBus1          = (EditText)findViewById(R.id.etBus1Update);
        etBus2          = (EditText)findViewById(R.id.etBus2Update);
        etBus3          = (EditText)findViewById(R.id.etBus3Update);
        bupDateStop     = (Button)findViewById(R.id.bUpdateStop);
        Intent intent   = getIntent();
        stop            =(Stop)intent.getSerializableExtra("stopObject");
        position =intent.getIntExtra("position",0);
        tvStopName.setText(stop.stopName);
        etBus1.setText(Integer.toString(stop.bus1));
        etBus2.setText(Integer.toString(stop.bus2));
        etBus3.setText(Integer.toString(stop.bus3));
        etBus1.requestFocus();
        System.out.println("Values:" + stop.stopName + "+" + stop.bus1 + "+" + stop.bus2 + "+" + stop.bus3 + "+" + position);
    }
    public void onClickUpdate(View view)
    {

        busNo1 = etBus1.getText().toString();
        busNo2 = etBus2.getText().toString();
        busNo3 = etBus3.getText().toString();
        if((busNo1.length()==0||busNo2.length()==0||busNo3.length()==0))
        {
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(UpDateStop.this,R.style.AlertDialogStyle);
            dialogBuilder.setMessage("At least one Bus number is required.");
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
            Stop updatedStop                    = new Stop(stop.stopName,Integer.parseInt(busNo1),Integer.parseInt(busNo2),Integer.parseInt(busNo3));
            CollegeLocalStore collegeLocalStore = new CollegeLocalStore(getApplicationContext());
            College college                     = collegeLocalStore.getLoggedInCollege();
            updateStop(updatedStop,college, position);
        }
    }
    private void updateStop(Stop updatedStop,College college, final int position)
    {
        System.out.println("-------------updateStop was Called in UpDateStop-----------------");
        ServerRequest serverRequest = new ServerRequest(this, "Updating stop...");
        serverRequest.updateStopDataInBackground(updatedStop, college, new GetStopCallback() {
            @Override
            public void done(Stop stop) {
                System.out.println("--------Done was called in UpDateStop Activity--------"+position);
                stopLocalStore = new StopLocalStore(getApplicationContext());
                stopLocalStore.updateStopData(stop, position);
                showSuccessMessage();
            }

            @Override
            public void error(String message) {
                System.out.println("--------Error was called in UpDateStop Activity---------");
                showErrorMessage(message);
            }
        });
    }
    private void showErrorMessage(String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UpDateStop.this,R.style.AlertDialogStyle);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.setTitle("Something Went Wrong!");
        dialogBuilder.show();
    }
    private void showSuccessMessage()
    {
        System.out.println("--------showSuccessMessage was called in AddStop Activity--------");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UpDateStop.this,R.style.AlertDialogStyle);
        dialogBuilder.setMessage("Stop has been Updated successfully to the server.");
        dialogBuilder.setTitle("Updated Successfully");
        dialogBuilder.setPositiveButton("Ok",null);
        dialogBuilder.setNegativeButton("Show Stops", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getApplicationContext(),ShowStops.class);
                startActivity(intent);
                finish();
            }
        });
        dialogBuilder.show();
    }
    public void onClickBackInUpdateStop(View view)
    {
        finish();
    }
}
