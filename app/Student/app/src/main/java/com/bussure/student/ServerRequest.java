package com.bussure.student;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    Context context;
    private static final String SERVER_ADD="http://192.168.43.216/";
    public ServerRequest(Context context, String message)
    {
        this.context=context;
        progressDialog = new ProgressDialog(context,R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
    }
    public ServerRequest()
    {

    }

    public void fetchCollegesInBackground(GetCollegeListCallback getCollegeListCallback)
    {
        progressDialog.show();
        new getCollegeListAsyncTack(getCollegeListCallback).execute();
    }
    public class getCollegeListAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        GetCollegeListCallback getCollegeListCallback;
        JSONObject jsonObject;
        public  getCollegeListAsyncTack(GetCollegeListCallback getCollegeListCallback)
        {
            this.getCollegeListCallback = getCollegeListCallback;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            try
            {
                URL url                         = new URL(SERVER_ADD+"Student/getCollegeList.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
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
                getCollegeListCallback.error("Unable to connect to the server right now. Please check you network connection.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("college");
                        College[] colleges=new College[jsonArray.length()];
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jo=jsonArray.getJSONObject(i);
                            College college=new College(jo.getString("name"),jo.getString("email"));
                            colleges[i]=college;
                        }
                        getCollegeListCallback.done(colleges);
                    }
                    else
                        getCollegeListCallback.error(jsonObject.getString("message"));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void fetchStopDataInBackground(College college, String option, GetFetchStopCallBack getFetchStopCallBack)
    {
        progressDialog.show();
        new FetchStopDataAsyncTack(college,option,getFetchStopCallBack).execute();
    }

    public class FetchStopDataAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        String option;
        GetFetchStopCallBack getFetchStopCallBack;
        JSONObject jsonObject;
        public  FetchStopDataAsyncTack(College college, String option, GetFetchStopCallBack getFetchStopCallBack)
        {
            this.getFetchStopCallBack    = getFetchStopCallBack;
            this.college                 = college;
            this.option                  = option;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("cEmailID", college.cEmailID));
            param.add(new BasicNameValuePair("option", option));
            try
            {
                URL url                         = new URL(SERVER_ADD+"Student/getStopsList.php");
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
                getFetchStopCallBack.error("Unable to connect to the server right now. Please check you network connection.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("stops");
                        Stop[] stopArray=new Stop[jsonArray.length()];
                        if(option.equals("1"))
                        {
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject singleStop=jsonArray.getJSONObject(i);
                                Stop stopObject=new Stop(singleStop.getString("stop"),
                                        singleStop.getInt("bus1"),singleStop.getInt("bus2"),
                                        singleStop.getInt("bus3"));
                                stopArray[i]=stopObject;
                            }
                        }
                        else
                        {
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject singleStop=jsonArray.getJSONObject(i);
                                Stop stopObject=new Stop(singleStop.getString("stop"));
                                stopArray[i]=stopObject;
                            }
                        }
                        progressDialog.dismiss();
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
            progressDialog.dismiss();
            super.onPostExecute(jsonObject);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void registerStudentInBackground(Student student,College college,GetStudentCallback getStudentCallback)
    {
        progressDialog.show();
        new registerStudentAsyncTack(student,college,getStudentCallback).execute();
    }

    public class registerStudentAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        Student student;
        GetStudentCallback getStudentCallback;
        JSONObject jsonObject;
        public  registerStudentAsyncTack(Student student,College college,GetStudentCallback getStudentCallback)
        {
            this.getStudentCallback      = getStudentCallback;
            this.college                 = college;
            this.student                 = student;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("name",student.name));
            param.add(new BasicNameValuePair("college",student.college));
            param.add(new BasicNameValuePair("stop",student.stop));
            param.add(new BasicNameValuePair("cEmailID",college.cEmailID));
            param.add(new BasicNameValuePair("password",student.password));
            param.add(new BasicNameValuePair("emailID",student.emailID));
            try
            {
                URL url                         = new URL(SERVER_ADD+"Student/registerStudent.php");
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
                getStudentCallback.error("Unable to connect to the server right now. Please check you network connection.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        getStudentCallback.done(null,null,null);
                    }
                    else
                        getStudentCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    getStudentCallback.error("Something Went Wrong.");
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void loginStudentInBackground(Student student,GetStudentCallback getStudentCallback)
    {
        progressDialog.show();
        new loginStudentAsyncTack(student,getStudentCallback).execute();
    }

    public class loginStudentAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        Student student;
        GetStudentCallback getStudentCallback;
        JSONObject jsonObject;
        public  loginStudentAsyncTack(Student student,GetStudentCallback getStudentCallback)
        {
            this.getStudentCallback      = getStudentCallback;
            this.student                 = student;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("password",student.password));
            param.add(new BasicNameValuePair("email",student.emailID));
            try
            {
                URL url                         = new URL(SERVER_ADD+"Student/logInStudent.php");
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
                getStudentCallback.error("Unable to connect to the server right now. Please check you network connection.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {

                        JSONObject studentJSON   = jsonObject.getJSONObject("student");
                        JSONObject stopJSON      = jsonObject.getJSONObject("stop");
                        String name              = studentJSON.getString("name");
                        String college           = studentJSON.getString("college");
                        String emailID           = studentJSON.getString("email");
                        String cEmail            = studentJSON.getString("cemail");

                        String stop              = stopJSON.getString("stop");
                        int bus1                 = stopJSON.getInt("bus1");
                        int bus2                 = stopJSON.getInt("bus2");
                        int bus3                 = stopJSON.getInt("bus3");
                        Student studentResponse  = new Student(name,college,stop,emailID,null);
                        College collegeResponse  = new College(college,cEmail);
                        Stop stopResponse        =new Stop(stop,bus1,bus2,bus3);
                        getStudentCallback.done(studentResponse,collegeResponse,stopResponse);
                    }
                    else
                        getStudentCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    getStudentCallback.error("Something Went Wrong.");
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void updateStopDataInBackground(College college,Stop stop,GetUpdateStopCallback getUpdateStopCallback)
    {
        new UpdateStopDataAsyncTack(college,stop,getUpdateStopCallback).execute();
    }

    public class UpdateStopDataAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        Stop stop;
        GetUpdateStopCallback getUpdateStopCallback;
        JSONObject jsonObject;
        public  UpdateStopDataAsyncTack(College college,Stop stop,GetUpdateStopCallback getUpdateStopCallback)
        {
            this.getUpdateStopCallback   = getUpdateStopCallback;
            this.college                 = college;
            this.stop                    = stop;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("cEmailID", college.cEmailID));
            param.add(new BasicNameValuePair("stop", stop.stopName));
            try
            {
                URL url                         = new URL(SERVER_ADD+"Student/updateStopStudent.php");
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
                getUpdateStopCallback.error("Unable to connect to the server right now. Please check you network connection.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("stops");
                        JSONObject defaultStop=jsonObject.getJSONObject("dStop");
                        Stop dStop=new Stop(defaultStop.getString("stop"),
                                defaultStop.getInt("bus1"),defaultStop.getInt("bus2"),
                                defaultStop.getInt("bus3"));

                        Stop[] stopArray=new Stop[jsonArray.length()];
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject singleStop=jsonArray.getJSONObject(i);
                            Stop stopObject=new Stop(singleStop.getString("stop"),
                                        singleStop.getInt("bus1"),singleStop.getInt("bus2"),
                                        singleStop.getInt("bus3"));
                            stopArray[i]=stopObject;
                        }
                        getUpdateStopCallback.done(stopArray,dStop);
                    }
                    else
                        getUpdateStopCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void updateProfileInBackground(Student student,College college,Stop stop,GetStudentCallback getStudentCallback)
    {
        new UpdateProfileInBackgroundAsyncTack(student,college,stop,getStudentCallback).execute();
    }

    public class UpdateProfileInBackgroundAsyncTack extends AsyncTask<Void,Void,JSONObject>
    {
        College college;
        Student student;
        Stop stop;
        GetStudentCallback getStudentCallback;
        JSONObject jsonObject;
        public  UpdateProfileInBackgroundAsyncTack(Student student,College college,Stop stop,GetStudentCallback getStudentCallback)
        {
            this.getStudentCallback      = getStudentCallback;
            this.college                 = college;
            this.student                 = student;
        }

        @Override
        protected JSONObject doInBackground(Void... params)
        {
            ArrayList<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("name",student.name));
            param.add(new BasicNameValuePair("college",student.college));
            param.add(new BasicNameValuePair("stop",stop.stopName));
            param.add(new BasicNameValuePair("cEmailID",college.cEmailID));
            param.add(new BasicNameValuePair("emailID",student.emailID));
            try
            {
                URL url                         = new URL("");
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
                getStudentCallback.error("Unable to connect to the server right now. Please check you network connection.");
            }
            else
            {
                try
                {
                    if (jsonObject.getInt("success") == 1)
                    {
                        JSONObject studentJSON   = jsonObject.getJSONObject("student");
                        JSONObject stopJSON      = jsonObject.getJSONObject("stop");
                        String name              = studentJSON.getString("name");
                        String college           = studentJSON.getString("college");
                        String emailID           = studentJSON.getString("email");
                        String cEmail            = studentJSON.getString("cemail");

                        String stop              = stopJSON.getString("stop");
                        int bus1                 = stopJSON.getInt("bus1");
                        int bus2                 = stopJSON.getInt("bus2");
                        int bus3                 = stopJSON.getInt("bus3");
                        Student studentResponse  = new Student(name,college,stop,emailID,null);
                        College collegeResponse  = new College(college,cEmail);
                        Stop stopResponse        =new Stop(stop,bus1,bus2,bus3);
                        getStudentCallback.done(studentResponse,collegeResponse,stopResponse);
                    }
                    else
                        getStudentCallback.error(jsonObject.getString("message"));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    getStudentCallback.error("Something Went Wrong.");
                }
            }
            super.onPostExecute(jsonObject);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
