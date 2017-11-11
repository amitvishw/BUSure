package com.bussure.student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    College selectedCollege;
    String option="2";
    Student student;
    EditText etName, etCollege, etStop, etEmail, etPassword;
    Button bRegister, bLoginLink;
    String name, college, stop, email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName      = (EditText) findViewById(R.id.etName);
        etCollege   = (EditText) findViewById(R.id.etCollegeName);
        etStop      = (EditText) findViewById(R.id.etStop);
        etEmail     = (EditText) findViewById(R.id.etEmail);
        etPassword  = (EditText) findViewById(R.id.etPassword);
        bRegister   = (Button) findViewById(R.id.bRegister);
        bLoginLink  = (Button) findViewById(R.id.bLogInLink);
    }
    public void selectCollege(View view)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest=new ServerRequest(Register.this,"Fetching college list...");
                serverRequest.fetchCollegesInBackground(new GetCollegeListCallback() {
                    @Override
                    public void done(final College[] colleges) {

                        int l = colleges.length;
                        final String a[] = new String[l];
                        System.out.println("---------Done Was Called---------");
                        for (int i=0;i<l;i++)
                        {
                            a[i]=colleges[i].cName;
                        }
                        AlertDialog.Builder layout = new AlertDialog.Builder(Register.this,R.style.AlertDialogStyle);
                        layout.setItems(a, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                etCollege.setText(a[which]);
                                setCollege(colleges[which]);
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
                    ServerRequest serverRequest = new ServerRequest(Register.this, "Fetching stop list...");
                    serverRequest.fetchStopDataInBackground(selectedCollege, option, new GetFetchStopCallBack() {
                        @Override
                        public void done(final Stop[] stop) {
                            System.out.println("---------Done Was Called---------");
                            final String stopNames[]=new String[stop.length];
                            for(int i=0;i<stop.length;i++)
                            {
                                stopNames[i]=stop[i].stopName;
                            }
                            final AlertDialog.Builder layout = new AlertDialog.Builder(Register.this,R.style.AlertDialogStyle);
                            layout.setItems(stopNames, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    etStop.setText(stopNames[which]);
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
    public void onClickRegister(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        name        = etName.getText().toString();
        college     = etCollege.getText().toString();
        stop        = etStop.getText().toString();
        email       = etEmail.getText().toString();
        password    = etPassword.getText().toString();

        if (name.length() == 0 || college.length() == 0 || stop.length() == 0 || email.length() == 0 || password.length() == 0)
        {
            showMessage("Missing Fields", "All fields are required.");
        }
        else
        {
            student = new Student(name,college,stop,email, password);
            registerUser();
        }
    }
    public void registerUser()
    {
        if(student!=null && college!=null)
        {
            ServerRequest serverRequest=new ServerRequest(Register.this,"Creating Account Please Wait...");
            serverRequest.registerStudentInBackground(student, selectedCollege, new GetStudentCallback() {
                @Override
                public void done(Student student,College college,Stop stop) {
                    showMessage("Thank you","Your Account Created Successfully.");
                }

                @Override
                public void error(String message) {
                    showMessage("Error!", message);
                }
            });
        }
        else
        {
            showMessage("Missing Fields", "All fields are required.");
        }
    }
    public void setCollege(College college)
    {
        selectedCollege=college;
        etStop.setText("");
        System.out.println("Selected College"+selectedCollege.cEmailID+" "+selectedCollege.cName);
    }
    public void onClickLoginLink(View view)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showMessage(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Register.this,R.style.AlertDialogStyle);
                dialogBuilder.setMessage(message);
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.setTitle(title);
                dialogBuilder.show();
            }
        });
    }
}
