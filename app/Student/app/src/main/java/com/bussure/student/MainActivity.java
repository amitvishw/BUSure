package com.bussure.student;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvName,tvCollegeName,tvMyStop;
    ListView lvStopList;
    String myStop;
    ProgressDialog progressDialog;
    String options[]={"Edit Profile","Logout"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvName        = (TextView)findViewById(R.id.tvName);
        tvCollegeName = (TextView)findViewById(R.id.tvCollegeName);
        tvMyStop      = (TextView)findViewById(R.id.tvMyStop);
        lvStopList    = (ListView)findViewById(R.id.lvStopList);
        progressDialog = new ProgressDialog(MainActivity.this,R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Just a moment...");
        StudentLocalStore studentLocalStore=new StudentLocalStore(MainActivity.this);
        if(studentLocalStore.isStudentLoggedIn())
        {
            setDetails();
            setStopDetails();
        }
        else 
        {
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
            finish();
        }
    }

    public void onClickSetting(View view)
    {
        Toast.makeText(MainActivity.this, "Show A Pop menu", Toast.LENGTH_LONG).show();
        AlertDialog.Builder layout = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);
        layout.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case 0:
                        Intent intent=new Intent(getApplicationContext(),UpdateProfile.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_LONG).show();
                        StudentLocalStore studentLocalStore=new StudentLocalStore(getApplicationContext());
                        StopLocalStore stopLocalStore=new StopLocalStore(getApplicationContext());
                        stopLocalStore.clearStopData();
                        studentLocalStore.clearStudentData();
                        studentLocalStore.setStudentLoggedIn(false);
                        Intent i=new Intent(getApplicationContext(),Login.class);
                        startActivity(i);
                        finish();
                }
            }
        });
        layout.setNegativeButton("Cancel", null);
        layout.show();
    }
    public void setDetails()
    {
        StudentLocalStore studentLocalStore=new StudentLocalStore(MainActivity.this);
        Student student = studentLocalStore.getLoggedInStudent();
        College college = studentLocalStore.getLoggedInCollege();
        tvName.setText(student.name);
        tvCollegeName.setText(college.cName);
        setDefaultStopDetails();
    }
    public void onClickTellAFriend(View view)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String des="\nDownload BUSure https://www.lala.la";
                intent.putExtra(Intent.EXTRA_TEXT,myStop+des);
                final Intent chooser= Intent.createChooser(intent,"Share via..");
                if(intent.resolveActivity(getPackageManager())!=null)
                {
                    startActivity(chooser);
                }
            }
        }).start();
    }
    public void setStopDetails()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog();
                StopLocalStore stopLocalStore  = new StopLocalStore(getApplicationContext());
                final String[] stops = stopLocalStore.getStopData();
                if(stops==null)
                {
                    showMessage("","No stop available.");
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListAdapter listAdapter = new ListViewAdapter(getApplicationContext(), stops);
                            lvStopList.setAdapter(listAdapter);
                        }
                    });
                }
                dismissProgressDialog();
            }
        }).start();
    }
    public void setDefaultStopDetails()
    {
        StopLocalStore stopLocalStore      = new StopLocalStore(MainActivity.this);
        Stop stop                          = stopLocalStore.getDefaultStop();
        if(stop.bus1!=-1&&stop.bus2==-1&&stop.bus3==-1)
        {
            myStop="Buses with no. "+stop.bus1+" is going to "+stop.stopName+" today.";
        }
        else
        {
            if(stop.bus1!=-1&&stop.bus2!=-1&&stop.bus3==-1)
            {
                myStop="Buses with no. "+stop.bus1+" & "+stop.bus2+" are going to "+stop.stopName+" today.";
            }
            else
            {
                if(stop.bus1 != -1 && stop.bus2 != -1)
                {
                    myStop="Buses with no. "+stop.bus1+", "+stop.bus2+" & "+stop.bus3+" are going to "+stop.stopName+" today.";
                }
                else
                {
                    if (stop.bus1==-1&&stop.bus2==-1&&stop.bus3==-1)
                    {
                        myStop="We have found that there is no bus is going to "+stop.stopName+"try to refresh or contact to the bus manager.";
                    }
                    else
                    {
                        myStop="!!!Error!!!!";
                    }
                }
            }
        }
        tvMyStop.setText(myStop);
    }
    public void showProgressDialog()
    {
        System.out.println("---------------showProgressDialog");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
    }
    public void dismissProgressDialog()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }
    private void showMessage(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogStyle);
                dialogBuilder.setMessage(message);
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.setTitle(title);
                dialogBuilder.show();
            }
        });
    }



    ///////////////////////////////////////////
    public void onClickUpdate(View view)
    {
        progressDialog.show();
        new UpdateAsyncTask().execute();
    }
    class UpdateAsyncTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            StudentLocalStore studentLocalStore=new StudentLocalStore(MainActivity.this);
            final StopLocalStore    stopLocalStore=new StopLocalStore(MainActivity.this);
            ServerRequest serverRequest=new ServerRequest();
            College college = studentLocalStore.getLoggedInCollege();
            Stop stop=stopLocalStore.getDefaultStop();
            serverRequest.updateStopDataInBackground(college, stop, new GetUpdateStopCallback() {
                @Override
                public void error(String message) {
                   showToast(message);
                }

                @Override
                public void done(Stop[] stops, Stop defaultStop) {
                    stopLocalStore.clearStopData();
                    stopLocalStore.storeDefaultStop(defaultStop);
                    stopLocalStore.storeFetchStopData(stops);
                    setDefaultStopDetails();
                    setStopDetails();
                    showToast("Updated");
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    public void showToast(final String msg)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }
}
