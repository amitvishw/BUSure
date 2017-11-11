package com.bussure.college;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class Loading extends AppCompatActivity {

    WebView webView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        webView   =(WebView)findViewById(R.id.webView);
        textView  =(TextView)findViewById(R.id.tvMessage);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadUrl("file:///android_asset/html/loading_3.GIF");
        Intent intent=getIntent();
        String from=intent.getStringExtra("from");
        if(from.equals("login"))
        {
            textView.setText("Logging In Please Wait...");
            College college=(College)intent.getSerializableExtra("college");
            authenticate(college);
        }
        else
        {
            textView.setText("Registering Please Wait...");
            College college=(College) intent.getSerializableExtra("college");
            registerUser(college);
        }
    }
    public void registerUser(final College college) {
        ServerRequest serverRequest = new ServerRequest(this,"");
        serverRequest.storeCollegeDataInBackground(college, new GetCollegeCallback() {
            @Override
            public void done(College callBackCollege,int code) {
                System.out.println("-------Done was called in Register Activity--------");
                sendEmail(college);
            }

            @Override
            public void error(String message)
            {
                System.out.println("-------Error was called in Register Activity--------");
                AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Loading.this);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setMessage(message);
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), Register.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialogBuilder.setTitle("Something Went Wrong!");
                dialogBuilder.show();
            }
        });
    }
    public void sendEmail(final College college)
    {
        ServerRequest serverRequest=new ServerRequest(this,"");
        serverRequest.sendEmailInBackground(college, new GetConfirmCallback() {
            @Override
            public void done(College callBackCollege) {
                System.out.println("-------Done was called in Loading SendEmail Activity--------");
                Toast.makeText(getApplicationContext(), "Your Account is Successfully Created", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Confirmation.class);
                intent.putExtra("college",college);
                startActivity(intent);
                finish();
            }

            @Override
            public void error(String message) {
                System.out.println("-------Error was called in Loading SendEmail Activity--------");
                AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Loading.this);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setMessage(message);
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), Register.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialogBuilder.setTitle("Something Went Wrong!");
                dialogBuilder.show();
            }
        });
    }


    private void authenticate(final College college)
    {
        final ServerRequest serverRequest=new ServerRequest(this,"");
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
                            textView.setText("Fetching Data Please Wait");
                            downloadImage(serverRequest, callBackCollege);
                            CollegeLocalStore collegeLocalStore = new CollegeLocalStore(getApplicationContext());
                            collegeLocalStore.storeCollegeData(callBackCollege);
                            collegeLocalStore.setCollegeLoggedIn(true);
                            serverRequest.fetchStopDataInBackground(callBackCollege, new GetFetchStopCallBack() {
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
                    }
                    @Override
                    public void error(String message) {
                        System.out.println("--------Error was called in Login Loading LActivity---------");
                        showErrorMessage(message);
                    }
                }
        );
    }
    public void downloadImage(ServerRequest serverRequest,College college)
    {
        serverRequest.downloadImageInBackground(college, new GetImageCallBack() {
            @Override
            public void done(Bitmap bitmap) {
                try {
                    CollegeLocalStore collegeLocalStore = new CollegeLocalStore(getApplicationContext());
                    String path = Environment.getExternalStorageDirectory().toString();
                    FileOutputStream fOut;
                    File image = new File(path, "collegeLogo.PNG");
                    if (image.exists()) {
                        System.out.println("Image Already Exist");
                        image.delete();
                        File newImage = new File(path, "collegeLogo.PNG");
                        fOut = new FileOutputStream(newImage);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    } else {
                        fOut = new FileOutputStream(image);
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
    private void showErrorMessage(String message)
    {
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Loading.this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        dialogBuilder.setTitle("Something Went Wrong!");
        dialogBuilder.show();
    }
}
