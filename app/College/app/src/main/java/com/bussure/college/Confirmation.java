package com.bussure.college;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Confirmation extends AppCompatActivity
{

    EditText etConfirmCode;
    Button bConfirm;
    TextView tvResend;
    College college;
    ServerRequest serverRequest;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        etConfirmCode  = (EditText)findViewById(R.id.etCode);
        tvResend       = (TextView)findViewById(R.id.tvResend);
        bConfirm       = (Button)findViewById(R.id.bConfirm);
        intent         = getIntent();
        college        = (College)intent.getSerializableExtra("college");
        serverRequest  = new ServerRequest(getApplicationContext(),"");
    }
    public void onClickResend(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        final ProgressDialog progressDialog   = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending Please wait....");
        progressDialog.show();
        serverRequest.sendEmailInBackground(college, new GetConfirmCallback() {
            @Override
            public void done(College college) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Email Has Been Sent In You Email ID.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(String message) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),message+" Please Try After Some Time",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void onClickConfirm(View view)
    {
        try
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String sCode = etConfirmCode.getText().toString();
        if(sCode.length()==0)
        {
            //do something
        }
        else
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait....");
            progressDialog.show();
            int code=Integer.parseInt(sCode);
            serverRequest.confirmEmailInBackground(code, college, new GetConfirmCallback() {
                @Override
                public void done(College college) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Email Has Been Confirmed.",Toast.LENGTH_LONG).show();
                    Intent loginIntent=new Intent(getApplicationContext(),Login.class);
                    startActivity(loginIntent);
                    finish();
                }

                @Override
                public void error(String message) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                }
            });
        }
        //Confirm Code
    }
    public void startMainActivity()
    {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}
