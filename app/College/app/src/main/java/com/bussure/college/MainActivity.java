package com.bussure.college;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity
{
    Button bLogout,bShowBUses,bAddStopMain;
    TextView tvName,tvAddress,tvPhone,tvEmail;
    CollegeLocalStore collegeLocalStore;
    StopLocalStore stopLocalStore;
    ImageView ivLogo;
    private static final int RESULT_LOAD_IMAGE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("-------------MainActivity Started-----------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvName            = (TextView)findViewById(R.id.tvCollegeName);
        tvAddress         = (TextView)findViewById(R.id.tvAddress);
        tvPhone           = (TextView)findViewById(R.id.tvPhone);
       // tvEmail           = (TextView)findViewById(R.id.tvEmail);
        bLogout           = (Button)findViewById(R.id.bLogout);
        bShowBUses        = (Button)findViewById(R.id.bShowBuses);
        bAddStopMain      = (Button)findViewById(R.id.baddStopMain);
        ivLogo            = (ImageView)findViewById(R.id.ivLogo);
        collegeLocalStore = new CollegeLocalStore(getApplicationContext());
        stopLocalStore    = new StopLocalStore(getApplicationContext());
        System.out.println("--------------isCollegeLoggedIn----------------" + collegeLocalStore.isCollegeLoggedIn());
        if(collegeLocalStore.isCollegeLoggedIn())
        {
            setCollegeDetails();
            if(collegeLocalStore.getCollegeLogoURI()==null)
            {
                //ivLogo.setImageResource(R.drawable.logo);
                //ivLogo.setAlpha((float) 0.4);
            }
            else
            {
                ivLogo.setImageURI(Uri.fromFile(new File(collegeLocalStore.getCollegeLogoURI())));
            }
        }
        else {
            startLoginActivity();
        }
    }
    public void selectImage(View view)
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        System.out.println("-------onActivityResult was called in MainActivity-------");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            Uri selectedImage = data.getData();
            System.out.println(selectedImage.getPath());
            Toast.makeText(getApplicationContext(),selectedImage.toString(), Toast.LENGTH_SHORT).show();
            try
            {
                ivLogo.setImageURI(selectedImage);
                ivLogo.setAlpha((float) 0.5);
                uploadImage();
            }catch (OutOfMemoryError e)
            {
                Toast.makeText(getApplicationContext(), "Image is too large please select smaller size image!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void uploadImage()
    {
        System.out.println("-------uploadImage was called in MainActivity-------");
        try {
            Bitmap bitmap=((BitmapDrawable)ivLogo.getDrawable()).getBitmap();
            ServerRequest serverRequest = new ServerRequest(getApplicationContext(),"");
            College college             = collegeLocalStore.getLoggedInCollege();
            serverRequest.uploadImageInBackground(college, bitmap, new GetImageCallBack() {
                @Override
                public void done(Bitmap bitmap) {
                    System.out.println("-------------DONE WAS CALLED IN UPLOAD IMAGE-----------------");
                    ivLogo.setAlpha((float) 1);
                    Toast.makeText(getApplicationContext(), "Image successfully uploaded.", Toast.LENGTH_SHORT).show();
                    downloadImage();
                }

                @Override
                public void error(String message) {
                    System.out.println("-------------ERROR WAS CALLED IN UPLOAD IMAGE-----------------");
                    AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(MainActivity.this);
                    dialogBuilder.setTitle("Error!");
                    dialogBuilder.setMessage("Some error occurred while uploading image.\nNote:Try smaller size image.");
                    dialogBuilder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            uploadImage();
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel",null);
                    dialogBuilder.show();
                }
            });
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Please Select an Image",Toast.LENGTH_SHORT).show();
        }
    }
    public void downloadImage()
    {
        College college             = collegeLocalStore.getLoggedInCollege();
        ServerRequest serverRequest = new ServerRequest(getApplicationContext(),"");
        serverRequest.downloadImageInBackground(college, new GetImageCallBack() {
            @Override
            public void done(Bitmap bitmap)
            {
                try {
                    String path = Environment.getExternalStorageDirectory().toString();
                    FileOutputStream fOut;
                    File image = new File(path, "collegeLogo.PNG");
                    if(image.exists())
                    {
                        System.out.println("Image Already Exist");
                        image.delete();
                        File newImage = new File(path, "collegeLogo.PNG");
                        fOut = new FileOutputStream(newImage);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    }
                    else
                    {
                        fOut = new FileOutputStream(image);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    }
                    collegeLocalStore.storeCollegeLogoURI(path+"/collegeLogo.PNG");
                    System.out.println("------------Done was called in downloadImage Main Activity------------");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String message) {
                System.out.println("------------Error was called in downloadImage Main Activity------------");
            }
        });
    }
    public void onClickAddStopMain(View view)
    {
        finish();
        startAddStopActivity();
    }
    private void startLoginActivity()
    {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
    private  void startAddStopActivity()
    {
        Intent intent = new Intent(getApplicationContext(), AddStop.class);
        startActivity(intent);
    }
    private void setCollegeDetails()
    {
        College college = collegeLocalStore.getLoggedInCollege();
        tvName.setText(college.collegeName);
        tvPhone.setText(college.phoneNumber);
        //tvEmail.setText(college.emailID);
        tvAddress.setText(college.city+" "+college.state+" "+Integer.toString(college.pinCode));
    }
    public void onClickLogout(View view)
    {
        logout();
    }
    public void onClickShowStops(View view)
    {
        Intent intent=new Intent(getApplicationContext(),ShowStops.class);
        startActivity(intent);
    }
    private void logout()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogStyle);
        dialogBuilder.setMessage("Are you sure you want to logout ?");
        dialogBuilder.setTitle("Log out");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                collegeLocalStore.clearCollegeData();
                stopLocalStore.clearStopData();
                collegeLocalStore.setCollegeLoggedIn(false);
                System.out.println("--------------LoggedOut was called----------------");
                startLoginActivity();
            }
        });
        dialogBuilder.setNegativeButton("No",null);
        dialogBuilder.show();
    }
}
