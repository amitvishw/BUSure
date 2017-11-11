package com.bussure.college;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowStops extends AppCompatActivity
{

    ListView lvStopList;
    ImageButton bBack,bAddStop;
    Button bUpdate;
    StopLocalStore stopLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("-------------ShowStops Started-----------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showstops);
        lvStopList=(ListView)findViewById(R.id.lvStopList);
        bBack           = (ImageButton)findViewById(R.id.bBackFromList);
        bUpdate         = (Button)findViewById(R.id.bUpdateStop);
        bAddStop        = (ImageButton)findViewById(R.id.bAddStop);
        setStopDetails();
    }
    public void setStopDetails()
    {
        stopLocalStore  = new StopLocalStore(this);
        String [] stops = stopLocalStore.getStopData();
        if(stops==null)
        {
            showErrorMessage();
        }
        else
        {
            ListAdapter listAdapter = new ListViewAdapter(this, stops);
            lvStopList.setAdapter(listAdapter);
            lvStopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView tvStopName = (TextView) view.findViewById(R.id.tvStopName);
                    TextView tvBus1     = (TextView)view.findViewById(R.id.tvBusNo1);
                    TextView tvBus2     = (TextView)view.findViewById(R.id.tvBusNo2);
                    TextView tvBus3     = (TextView)view.findViewById(R.id.tvBusNo3);
                    String stopName     = tvStopName.getText().toString();
                    String bus1         = tvBus1.getText().toString();
                    String bus2         = tvBus2.getText().toString();
                    String bus3         = tvBus3.getText().toString();
                    Stop stop           = new Stop(stopName,Integer.parseInt(bus1),Integer.parseInt(bus2), Integer.parseInt(bus3));
                    Toast.makeText(getApplicationContext(), stopName + " " + Integer.toString(position), Toast.LENGTH_SHORT).show();
                    Intent intent       = new Intent(getApplicationContext(), UpDateStop.class);
                    intent.putExtra("stopObject", stop);
                    intent.putExtra("position",position);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
    public void onClickBack(View view)
    {
        finish();
    }
    public void onClickAddStopButton(View view)
    {
        finish();
        startAddStopActivity();
    }
    private void showErrorMessage()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ShowStops.this);
        dialogBuilder.setMessage("No stop available yet");
        dialogBuilder.setPositiveButton("Add One", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startAddStopActivity();
            }
        });
        dialogBuilder.setNegativeButton("Not Now",null);
        dialogBuilder.setTitle("No Stop");
        dialogBuilder.show();
    }
    private  void startAddStopActivity()
    {
        finish();
        Intent intent=new Intent(getApplicationContext(),AddStop.class);
        startActivity(intent);
    }
}
