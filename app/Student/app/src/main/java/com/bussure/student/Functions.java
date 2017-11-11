package com.bussure.student;


import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Functions
{
    Functions()
    {

    }
    public static String convertInputStreamToString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String result,line;
        result="";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    /**
     * @param params ArrayList of NameValuePair to be encoded into an URL
     * @return String of NameValuePair encoded using UTF-8 URLEncoder
     * @throws UnsupportedEncodingException
     */
    public static String getQuery(ArrayList<NameValuePair> params) throws UnsupportedEncodingException
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
