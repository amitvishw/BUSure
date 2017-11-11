package com.bussure.college;
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

public class Register extends AppCompatActivity
{

    EditText etCollegeName,etCityName,etState,etPinCode,etPhoneNumber,etEmailID,etPassword;
    String collegeName,city,state,pinCodeString,phoneNumber,emailID,password;
    Button bRegister,bLoginLink;
    int pinCode;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etCollegeName = (EditText)findViewById(R.id.etCollegeName);
        etCityName    = (EditText)findViewById(R.id.etCity);
        etState       = (EditText)findViewById(R.id.etState);
        etPinCode     = (EditText)findViewById(R.id.etPinCode);
        etPhoneNumber = (EditText)findViewById(R.id.etPhone);
        etEmailID     = (EditText)findViewById(R.id.etEmail);
        etPassword    = (EditText)findViewById(R.id.etPassword);
        bRegister     = (Button)findViewById(R.id.bRegister);
        bLoginLink    = (Button)findViewById(R.id.bLogInLink);
    }
    public void onClickRegister(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        collegeName   = etCollegeName.getText().toString();
        city          = etCityName.getText().toString();
        state         = etState.getText().toString();
        pinCodeString = etPinCode.getText().toString();
        phoneNumber   = etPhoneNumber.getText().toString();
        emailID       = etEmailID.getText().toString();
        password      = etPassword.getText().toString();
        if(collegeName.length()==0||city.length()==0||state.length()==0||pinCodeString.length()==0||phoneNumber.length()==0||emailID.length()==0||password.length()==0)
        {
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Register.this,R.style.AlertDialogStyle);
            dialogBuilder.setMessage("All fields are required.");
            dialogBuilder.setPositiveButton("OK",null);
            dialogBuilder.setTitle("Missing Fields");
            dialogBuilder.show();
        }
        else
        {
            if(isValidEmailAddress(emailID))
            {
                pinCode         = Integer.parseInt(pinCodeString);
                College college = new College(collegeName,city,state,pinCode,phoneNumber,emailID,password);
                //Intent intent=new Intent(getApplicationContext(),Loading.class);
                //intent.putExtra("from","Register");
                //intent.putExtra("college",college);
                // startActivity(intent);
                // finish();
                registerUser(college);
            }
            else
            {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Register.this,R.style.AlertDialogStyle);
                dialogBuilder.setMessage("Enter a valid email address.");
                dialogBuilder.setPositiveButton("OK",null);
                dialogBuilder.setTitle("Invalid Email");
                dialogBuilder.show();
            }
        }
    }
    public void onClickLoginLink(View view)
    {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }


    public void registerUser(College college) {
        ServerRequest serverRequest = new ServerRequest(this, "Signing up...");
        serverRequest.storeCollegeDataInBackground(college, new GetCollegeCallback() {
            @Override
            public void done(College college,int code) {
                System.out.println("-------Done was called in Register Activity--------");
                Toast.makeText(getApplicationContext(), "Your Account is Successfully Created", Toast.LENGTH_LONG).show();
                finish();
                startLoginActivity();
            }

            @Override
            public void error(String message)
            {
                System.out.println("-------Error was called in Register Activity--------");
                showErrorMessage(message);
            }
        });
    }
    private void showErrorMessage(String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Register.this,R.style.AlertDialogStyle);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK",null);
        dialogBuilder.setTitle("Something Went Wrong!");
        dialogBuilder.show();
    }
    private void startLoginActivity()
    {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }
    public static boolean isValidEmailAddress(String email)
    {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}