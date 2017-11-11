package com.bussure.student;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity
{

    Student student;
    EditText etEmail,etPassword;
    Button bLogin,bRegisterLink;
    String option="1";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail         = (EditText)findViewById(R.id.etEmailLogIn);
        etPassword      = (EditText)findViewById(R.id.etPasswordLogin);
        bLogin          = (Button)findViewById(R.id.bLogIn);
        bRegisterLink   = (Button)findViewById(R.id.bRegisterLink);
        progressDialog  = new ProgressDialog(Login.this,R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Just a moment...");
    }
    public void onClickLogin(View view)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                try
                {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                String emailID  = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if(emailID.length()==0||password.length()==0)
                {
                    showMessage("Missing Fields", "All fields are required.");
                }
                else
                {
                    student=new Student(emailID,password);
                    authenticate();
                }
            }
        });
    }
    public void authenticate()
    {
        if(student!=null)
        {
            ServerRequest serverRequest=new ServerRequest(Login.this,"Logging in please wait...");
            serverRequest.loginStudentInBackground(student, new GetStudentCallback() {
                @Override
                public void done(final Student callBackStudent, College callBackCollege, final Stop callBackStop) {
                    ServerRequest serverRequest=new ServerRequest(Login.this,"Fetching data please wait...");
                    StudentLocalStore studentLocalStore = new StudentLocalStore(getApplicationContext());
                    studentLocalStore.storeStudentData(callBackStudent, callBackCollege, callBackStop);
                    studentLocalStore.setStudentLoggedIn(true);
                    serverRequest.fetchStopDataInBackground(callBackCollege,option,new GetFetchStopCallBack() {
                        @Override
                        public void done(final Stop[] stop) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    showProgressDialog();
                                    StopLocalStore stopLocalStore = new StopLocalStore(getApplicationContext());
                                    stopLocalStore.clearStopData();
                                    stopLocalStore.storeDefaultStop(callBackStop);
                                    stopLocalStore.storeFetchStopData(stop);
                                    dismissProgressDialog();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).start();
                        }

                        @Override
                        public void error(String message) {
                            Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        }
                    });
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
    public void onClickRegisterLink(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
        finish();
    }
    private void showMessage(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this,R.style.AlertDialogStyle);
                dialogBuilder.setMessage(message);
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.setTitle(title);
                dialogBuilder.show();
            }
        });
    }
    public void onClickForgotPassword(View view)
    {

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
}
