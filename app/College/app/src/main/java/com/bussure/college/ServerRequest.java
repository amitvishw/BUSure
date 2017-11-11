package com.bussure.college;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ServerRequest
{
    ProgressDialog progressDialog;
    private static final String SERVER_ADD="http://192.168.43.216/";

    public ServerRequest(Context context, String Messagee)
    {
        progressDialog = new ProgressDialog(context,R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(Messagee);
    }
    ///////////////////////////////////////////////////CREATE ACCOUNT/////////////////////////////////////////////////////////////
    public void storeCollegeDataInBackground(College college, GetCollegeCallback getCollegeCallback)
    {
        progressDialog.show();
        new StoreCollegeDataAsyncTack(college,getCollegeCallback).execute();
    }

    public class StoreCollegeDataAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        GetCollegeCallback getCollegeCallback;
        JSONObject jsonObject;
        public  StoreCollegeDataAsyncTack(College college,GetCollegeCallback getCollegeCallback)
        {
            this.college            = college;
            this.getCollegeCallback = getCollegeCallback;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("name",college.collegeName));
            param.add(new BasicNameValuePair("city",college.city));
            param.add(new BasicNameValuePair("state",college.state));
            param.add(new BasicNameValuePair("pinCode",college.pinCode+""));
            param.add(new BasicNameValuePair("phone",college.phoneNumber));
            param.add(new BasicNameValuePair("emailID", college.emailID));
            param.add(new BasicNameValuePair("password", college.password));
            try
            {
                URL url                         = new URL(SERVER_ADD+"College/register.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                System.out.println("-------------Response From Server-------------"+result);
                jsonObject                      = new JSONObject(new JSONTokener(result));

                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            progressDialog.dismiss();
            if(jsonObject==null)
            {
                getCollegeCallback.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                        getCollegeCallback.done(null,0);
                    else
                        getCollegeCallback.error(jsonObject.getString("message"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    /////////////////////////////////////////////////////LOG IN//////////////////////////////////////////////////////////////////
    public void fetchCollegeDataInBackground(College college, GetCollegeCallback getCollegeCallback)
    {
        progressDialog.show();
        new FetchCollegeDataAsyncTack(college,getCollegeCallback).execute();
    }

    class FetchCollegeDataAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        GetCollegeCallback getCollegeCallback;
        JSONObject jsonObject;
        public FetchCollegeDataAsyncTack(College college,GetCollegeCallback getCollegeCallback)
        {
            this.college            = college;
            this.getCollegeCallback = getCollegeCallback;
        }
        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("emailID", college.emailID));
            param.add(new BasicNameValuePair("password", college.password));
            try
            {
                URL url                         = new URL(SERVER_ADD+"/College/login.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                jsonObject                      = new JSONObject(new JSONTokener(result));
                System.out.println("-------------Response From Server-------------"+jsonObject.toString());
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            progressDialog.dismiss();
            if(jsonObject==null)
            {
                getCollegeCallback.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        College college=new College(jsonObject.getString("name"),jsonObject.getString("city"),
                                                    jsonObject.getString("state"),jsonObject.getInt("pincode"),
                                                    jsonObject.getString("phone"),jsonObject.getString("emailID"),
                                                    jsonObject.getString("password"));
                        getCollegeCallback.done(college,1);
                    }
                    else
                    {
                        if(jsonObject.getInt("success")==2)
                        {
                            getCollegeCallback.done(null,2);
                        }
                        else
                        {
                            System.out.println("--------Called From Here-------");
                            getCollegeCallback.error(jsonObject.getString("message"));
                        }
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }

    }
    /////////////////////////////////////////////ADD STOP TO THE SERVER///////////////////////////////////////////////////////////
    public void storeStopDataInBackground(Stop stop,College college,GetStopCallback getStopCallback)
    {
        progressDialog.show();
        new StoreStopDataAsyncTack(stop,college,getStopCallback).execute();
    }

    public class StoreStopDataAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        Stop stop;
        GetStopCallback getStopCallback;
        JSONObject jsonObject;
        public  StoreStopDataAsyncTack(Stop stop,College college,GetStopCallback getStopCallback)
        {
            this.stop               = stop;
            this.getStopCallback    = getStopCallback;
            this.college            = college;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("emailID",college.emailID));
            param.add(new BasicNameValuePair("stop",stop.stopName));
            param.add(new BasicNameValuePair("bus1",stop.bus1+""));
            param.add(new BasicNameValuePair("bus2",stop.bus2+""));
            param.add(new BasicNameValuePair("bus3",stop.bus3+""));
            try
            {
                URL url                         = new URL(SERVER_ADD+"College/addStop.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                System.out.println(getQuery(param));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                jsonObject                      = new JSONObject(new JSONTokener(result));
                System.out.println("-------------Response From Server-------------"+jsonObject.toString());
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            progressDialog.dismiss();
            if(jsonObject==null)
            {
                getStopCallback.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                        getStopCallback.done(stop);
                    else
                        getStopCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    /////////////////////////////////////////////////UPDATE STOP//////////////////////////////////////////////////////////////////
    public void updateStopDataInBackground(Stop stop,College college,GetStopCallback getStopCallback)
    {
        progressDialog.show();
        new UpdateStopDataAsyncTack(stop,college,getStopCallback).execute();
    }

    public class UpdateStopDataAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        Stop stop;
        GetStopCallback getStopCallback;
        JSONObject jsonObject;
        public  UpdateStopDataAsyncTack(Stop stop,College college,GetStopCallback getStopCallback)
        {
            this.stop               = stop;
            this.getStopCallback    = getStopCallback;
            this.college            = college;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("emailID",college.emailID));
            param.add(new BasicNameValuePair("stop",stop.stopName));
            param.add(new BasicNameValuePair("bus1",stop.bus1+""));
            param.add(new BasicNameValuePair("bus2",stop.bus2+""));
            param.add(new BasicNameValuePair("bus3",stop.bus3+""));
            try
            {
                URL url                         = new URL(SERVER_ADD+"College/updateStop.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                jsonObject                      = new JSONObject(new JSONTokener(result));
                System.out.println("-------------Response From Server-------------"+jsonObject.toString());
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {   progressDialog.dismiss();
            if(jsonObject==null)
            {
                getStopCallback.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                        getStopCallback.done(stop);
                    else
                        getStopCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    ////////////////////////////////////////////FETCH STOP ON LOGIN///////////////////////////////////////////////////////////////
    public void fetchStopDataInBackground(College college,GetFetchStopCallBack getFetchStopCallBack)
    {
        new FetchStopDataAsyncTack(college,getFetchStopCallBack).execute();
    }

    public class FetchStopDataAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        GetFetchStopCallBack getFetchStopCallBack;
        JSONObject jsonObject;
        public  FetchStopDataAsyncTack(College college,GetFetchStopCallBack getFetchStopCallBack)
        {
            this.getFetchStopCallBack    = getFetchStopCallBack;
            this.college                 = college;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("emailID", college.emailID));
            try
            {
                URL url                         = new URL(SERVER_ADD+"College/getStops.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                System.out.println("-------------Response From Server-------------"+result);
                jsonObject                      = new JSONObject(new JSONTokener(result));
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            if(jsonObject==null)
            {
                getFetchStopCallBack.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("stops");
                        Stop[] stopArray=new Stop[jsonArray.length()];
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject singleStop=jsonArray.getJSONObject(i);
                            Stop stopObject=new Stop(singleStop.getString("stop"),singleStop.getInt("bus1"),singleStop.getInt("bus2"),singleStop.getInt("bus3"));
                            stopArray[i]=stopObject;
                        }
                        getFetchStopCallBack.done(stopArray);
                    }
                    else
                        getFetchStopCallBack.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    //////////////////////////////////////////////UPLOAD IMAGE///////////////////////////////////////////////////////////////////
    public void uploadImageInBackground(College college,Bitmap image,GetImageCallBack getImageCallBack)
    {
        new UploadImageAsyncTack(college,image,getImageCallBack).execute();
    }

    public class UploadImageAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        GetImageCallBack getImageCallBack;
        JSONObject jsonObject;
        Bitmap image;
        public  UploadImageAsyncTack(College college,Bitmap image,GetImageCallBack getImageCallBack)
        {
            this.getImageCallBack  = getImageCallBack;
            this.college           = college;
            this.image             = image;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            try
            {

                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG,60,byteArrayOutputStream);
                String encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

                ArrayList<NameValuePair> param=new ArrayList<>();
                param.add(new BasicNameValuePair("name",college.emailID));
                param.add(new BasicNameValuePair("image",encodedImage));

                URL url                         = new URL("http://www.bit2hex.xyz/uploadImage.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(90000);
                urlConnection.setConnectTimeout(90000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                jsonObject                      = new JSONObject(new JSONTokener(result));
                System.out.println("-------------Response From Server-------------"+jsonObject.toString());
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            if(jsonObject==null)
            {
                getImageCallBack.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        getImageCallBack.done(null);
                    }
                    else
                        getImageCallBack.error("Something went wrong.");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    //////////////////////////////////////////DOWNLOAD IMAGE/////////////////////////////////////////////////////////////////////

    public void downloadImageInBackground(College college,GetImageCallBack getImageCallBack)
    {
        new DownloadImageAsyncTack(college,getImageCallBack).execute();
    }
    class DownloadImageAsyncTack extends AsyncTask<Void,Void,Bitmap>
    {
        College college;
        GetImageCallBack getImageCallBack;
        Bitmap image=null;
        public DownloadImageAsyncTack(College college, GetImageCallBack getImageCallBack)
        {
            this.college = college;
            this.getImageCallBack = getImageCallBack;
        }

        @Override
        protected Bitmap doInBackground(Void... params)
        {
            try
            {
                String imageName =college.emailID;
                imageName=imageName.replace('.','_');
                imageName=imageName.replace('@','_');
                String urlToImage="http://www.bit2hex.xyz/images/"+imageName+".PNG";
                URL url                         = new URL(urlToImage);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(40000);
                urlConnection.setConnectTimeout(40000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                image=BitmapFactory.decodeStream(urlConnection.getInputStream(),null,null);
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                image=null;
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image)
        {
            super.onPostExecute(image);
            if(image==null)
            {
                getImageCallBack.error("Image Bitmap is null.");
            }
            else
            {
                getImageCallBack.done(image);
            }
        }
    }
    /////////////////////////////////////////////MAIL CONFIRMATION/////////////////////////////////////////////////////////////
    public void sendEmailInBackground(College college,GetConfirmCallback getConfirmCallback)
    {
        new SendEmailAsyncTack(college,getConfirmCallback).execute();
    }
    public class SendEmailAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        GetConfirmCallback getConfirmCallback;
        JSONObject jsonObject;
        public  SendEmailAsyncTack(College college,GetConfirmCallback getConfirmCallback)
        {
            this.getConfirmCallback      = getConfirmCallback;
            this.college                 = college;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("emailID", college.emailID));
            try
            {
                URL url                         = new URL("http://www.bit2hex.xyz/sendEmail.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                jsonObject                      = new JSONObject(new JSONTokener(result));
                System.out.println("-------------Response From Server-------------"+jsonObject.toString());
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            if(jsonObject==null)
            {
                getConfirmCallback.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        getConfirmCallback.done(null);
                    }
                    else
                        getConfirmCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    //////////////////////////////////////////EMAIL CONFIRMATION////////////////////////////////////////////////////////
    public void confirmEmailInBackground(int code,College college,GetConfirmCallback getConfirmCallback)
    {
        new ConfirmEmailAsyncTack(code,college,getConfirmCallback).execute();
    }
    public class ConfirmEmailAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        int code;
        GetConfirmCallback getConfirmCallback;
        JSONObject jsonObject;
        public  ConfirmEmailAsyncTack(int code,College college,GetConfirmCallback getConfirmCallback)
        {
            this.code                    = code;
            this.getConfirmCallback      = getConfirmCallback;
            this.college                 = college;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("emailID", college.emailID));
            param.add(new BasicNameValuePair("code",Integer.toString(code)));
            try
            {
                URL url                         = new URL("http://www.bit2hex.xyz/confirmEmail.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream os                 = urlConnection.getOutputStream();
                BufferedWriter writer           = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(param));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                InputStream in                  = new BufferedInputStream(urlConnection.getInputStream());
                String result                   = convertInputStreamToString(in);
                jsonObject                      = new JSONObject(new JSONTokener(result));
                System.out.println("-------------Response From Server-------------"+jsonObject.toString());
                urlConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject)
        {
            if(jsonObject==null)
            {
                getConfirmCallback.error("Unable to connect to the server right now.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        getConfirmCallback.done(null);
                    }
                    else
                        getConfirmCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    //////////////////////////////////////////UTIL FUNCTIONS/////////////////////////////////////////////////////////////////////
    private static String convertInputStreamToString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String result,line;
        result="";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
    private static String getQuery(ArrayList<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first        = true;
        for(NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
