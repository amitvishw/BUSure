package com.bussure.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class ShowStops extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showstops);
    }
    public void onClickBack(View view)
    {
        finish();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
    public void onClickUpdate(View view)
    {
        Toast.makeText(ShowStops.this,"Update All List", Toast.LENGTH_LONG).show();
    }

}
