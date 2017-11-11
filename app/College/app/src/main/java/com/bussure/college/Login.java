package com.bussure.college;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;


public class Login extends AppCompatActivity
{
    LinearLayout logInLayout;
    EditText etEmailID,etPassword;
    Button bLogin,bRegisterLink;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmailID     = (EditText)findViewById(R.id.etEmailLogIn);
        etPassword    = (EditText)findViewById(R.id.etPasswordLogin);
        bLogin        = (Button)findViewById(R.id.bLogIn);
        bRegisterLink = (Button)findViewById(R.id.bRegisterLink);
        logInLayout   = (LinearLayout)findViewById(R.id.loginLayout);
    }
    public void onClickLogin(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        String emailID  = etEmailID.getText().toString();
        String password = etPassword.getText().toString();
        if(emailID.length()==0||password.length()==0)
        {
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Login.this,R.style.AlertDialogStyle);
            dialogBuilder.setMessage("All fields are required.");
            dialogBuilder.setPositiveButton("OK", null);
            dialogBuilder.setTitle("Missing Fields");
            dialogBuilder.show();
        }
        else {
            if (isValidEmailAddress(emailID))
            {
                College college = new College(emailID,password);
                //Intent intent=new Intent(getApplicationContext(),Loading.class);
                // intent.putExtra("college", college);
                //intent.putExtra("from","login");
                // startActivity(intent);
                //finish();
                authenticate(college);
            }
            else
            {
                AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Login.this,R.style.AlertDialogStyle);
                dialogBuilder.setMessage("Enter a valid email address.");
                dialogBuilder.setPositiveButton("OK",null);
                dialogBuilder.setTitle("Invalid Email");
                dialogBuilder.show();
            }
        }
    }

    public static boolean isValidEmailAddress(String email)
    {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    private void authenticate(final College college)
    {
        final ServerRequest serverRequest=new ServerRequest(this, "Logging in...");
        serverRequest.fetchCollegeDataInBackground(college, new GetCollegeCallback() {
                    @Override
                    public void done(College callBackCollege,int code) {
                        System.out.println("--------Done was called in Login Loading Activity--------");
                        if(code==2)
                        {
                            Intent intent = new Intent(getApplicationContext(), Confirmation.class);
                            intent.putExtra("college",college);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            //downloadImage(serverRequest, callBackCollege);
                            CollegeLocalStore collegeLocalStore = new CollegeLocalStore(getApplicationContext());
                            collegeLocalStore.storeCollegeData(callBackCollege);
                            collegeLocalStore.setCollegeLoggedIn(true);
                            serverRequest.fetchStopDataInBackground(callBackCollege, new GetFetchStopCallBack() {
                                @Override
                                public void done(Stop[] stop) {
                                    StopLocalStore stopLocalStore = new StopLocalStore(getApplicationContext());
                                    stopLocalStore.storeFetchStopData(stop);
                                    Toast.makeText(getApplicationContext(), "Successfully Logged In", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void error(String message) {
                                    Toast.makeText(getApplicationContext(), "Successfully LoggedIn but " + message, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }
                    }
                    @Override
                    public void error(String message) {
                        System.out.println("--------Error was called in Login Activity---------");
                        showErrorMessage(message);
                    }
                }
        );
    }
    public void onClickRegisterLink(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }
    /*private void authenticate(College college)
    {
        final ServerRequest serverRequest=new ServerRequest(this);
        serverRequest.fetchCollegeDataInBackground(college, new GetCollegeCallback() {
                    @Override
                    public void done(College college) {
                        System.out.println("--------Done was called in Login Activity--------");
                        downloadImage(serverRequest, college);
                        CollegeLocalStore collegeLocalStore = new CollegeLocalStore(getApplicationContext());
                        collegeLocalStore.storeCollegeData(college);
                        collegeLocalStore.setCollegeLoggedIn(true);
                        serverRequest.fetchStopDataInBackground(college, new GetFetchStopCallBack() {
                            @Override
                            public void done(Stop[] stop) {
                                StopLocalStore stopLocalStore = new StopLocalStore(getApplicationContext());
                                stopLocalStore.storeFetchStopData(stop);
                                Toast.makeText(getApplicationContext(), "Successfully LoggedIn", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void error(String message) {
                                Toast.makeText(getApplicationContext(), "Successfully LoggedIn but " + message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }

                    @Override
                    public void error(String message) {
                        System.out.println("--------Error was called in Login Activity---------");
                        showErrorMessage(message);
                    }
                }
        );
    }*/
    public void downloadImage(ServerRequest serverRequest,College college)
    {
        serverRequest.downloadImageInBackground(college, new GetImageCallBack() {
            @Override
            public void done(Bitmap bitmap) {
                try {
                    FileOutputStream fOut;
                    CollegeLocalStore collegeLocalStore = new CollegeLocalStore(getApplicationContext());
                    String path                         = Environment.getExternalStorageDirectory().toString();
                    File image                          = new File(path, "collegeLogo.PNG");
                    if (image.exists()) {
                        System.out.println("Image Already Exist");
                        image.delete();
                        File newImage = new File(path, "collegeLogo.PNG");
                        fOut          = new FileOutputStream(newImage);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    } else {
                        fOut          = new FileOutputStream(image);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    }
                    collegeLocalStore.storeCollegeLogoURI(path + "/collegeLogo.PNG");
                    System.out.println("------------Done was called in downloadImageInBackground------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void error(String message) {
                System.out.println("------------Error was called in downloadImageInBackground------------");
            }
        });
    }

    public void onClickForgotPassword(View view)
    {

    }

    private void showErrorMessage(String message)
    {
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Login.this,R.style.AlertDialogStyle);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK",null);
        dialogBuilder.setTitle("Something Went Wrong!");
        dialogBuilder.show();
    }

}
