package com.bussure.student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateProfile extends AppCompatActivity {

    Stop selectedStop;
    College selectedCollege;
    Student selectedStudent;
    String option="2";
    EditText etName, etCollege, etStop, etEmail, etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        etName      = (EditText) findViewById(R.id.etUpdateName);
        etCollege   = (EditText) findViewById(R.id.etUpdateCollegeName);
        etStop      = (EditText) findViewById(R.id.etUpdateStop);
        etEmail     = (EditText) findViewById(R.id.etUpdateEmail);
        etPassword  = (EditText) findViewById(R.id.etUpdatePassword);
        setDetails();
    }
    public void setDetails()
    {
        StopLocalStore stopLocalStore       = new StopLocalStore(UpdateProfile.this);
        StudentLocalStore studentLocalStore = new StudentLocalStore(UpdateProfile.this);
        selectedStudent = studentLocalStore.getLoggedInStudent();
        selectedCollege = studentLocalStore.getLoggedInCollege();
        selectedStop                        = stopLocalStore.getDefaultStop();
        etName.setText(selectedStudent.name);
        etCollege.setText(selectedCollege.cName);
        etEmail.setText(selectedStudent.emailID);
        etStop.setText(selectedStop.stopName);

    }
    public void onClickBack(View view)
    {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void onClickUpdate(View view)
    {
        if(selectedStop!=null&&selectedCollege!=null&&selectedStudent!=null) {
            /*ServerRequest serverRequest=new ServerRequest();
            serverRequest.updateProfileInBackground(selectedStudent, selectedCollege, selectedStop, new GetStudentCallback() {
                @Override
                public void done(Student student, College college, Stop stop) {
                    System.out.println(student.name+" "+student.college+" "+student.emailID+" "+student.stop);
                    System.out.println(stop.stopName+" "+stop.bus1);
                    System.out.println(college.cEmailID+" "+college.cName);
                }

                @Override
                public void error(String message) {
                    showMessage("Error!",message);
                }
            });
        }
        else
        {
            showMessage("Missing Fields", "All fields are required.");
        }*/
        }}
    public void selectCollege(View view)
    {
        ServerRequest serverRequest = new ServerRequest(UpdateProfile.this, "Fetching college list...");
        serverRequest.fetchCollegesInBackground(new GetCollegeListCallback() {
            @Override
            public void done(final College[] colleges) {

                int l = colleges.length;
                final String a[] = new String[l];
                for (int i = 0; i < l; i++) {
                    a[i] = colleges[i].cName;
                }
                AlertDialog.Builder layout = new AlertDialog.Builder(UpdateProfile.this);
                layout.setItems(a, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etCollege.setText(a[which]);
                        setCollege(colleges[which]);
                        etStop.setText("");
                    }
                });
                layout.setPositiveButton("Couldn't Find?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "May Be Your College Does Not Registered Yet!", Toast.LENGTH_SHORT).show();
                    }
                });
                layout.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Please Select Your College!", Toast.LENGTH_SHORT).show();
                    }
                });
                layout.show();
            }

            @Override
            public void error(String message) {
                System.out.println("---------Error Was Called-------");
                showMessage("Error!", message);
            }
        });
    }
    public void selectStop(View view)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectedCollege == null) {
                    showMessage("Error!", "Please Select College First.");
                } else {
                    ServerRequest serverRequest = new ServerRequest(UpdateProfile.this, "Fetching stop list...");
                    serverRequest.fetchStopDataInBackground(selectedCollege, option, new GetFetchStopCallBack() {
                        @Override
                        public void done(final Stop[] stop) {
                            System.out.println("---------Done Was Called---------");
                            final String stopNames[] = new String[stop.length];
                            for (int i = 0; i < stop.length; i++) {
                                stopNames[i] = stop[i].stopName;
                            }
                            final AlertDialog.Builder layout = new AlertDialog.Builder(UpdateProfile.this);
                            layout.setItems(stopNames, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    etStop.setText(stopNames[which]);
                                    setStop(stop[which]);
                                }
                            });
                            layout.setPositiveButton("Couldn't Find?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Will Be SomeThing Here!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            layout.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Please Select Your Stop.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            layout.show();
                        }

                        @Override
                        public void error(String message) {
                            System.out.println("---------Error Was Called-------");
                            showMessage("Error!", message);
                        }
                    });
                }
            }
        });
    }
    public void setCollege(College college)
    {
        selectedCollege=college;
    }
    public void setStop(Stop stop)
    {
        selectedStop=stop;
    }
    private void showMessage(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UpdateProfile.this);
                dialogBuilder.setMessage(message);
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.setTitle(title);
                dialogBuilder.show();
            }
        });
    }
}
