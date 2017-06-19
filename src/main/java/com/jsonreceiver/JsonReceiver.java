package com.jsonreceiver;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kolege on 19.06.2017.
 */

public class JsonReceiver {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    // constructor
    public JsonReceiver() {

    }


    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params   request parameters.
     * @throws IOException propagated from POST.
     */
    public static String post(String endpoint, Map<String, String> params){

        Log.v("TAG", "endpoint: '" + endpoint + "' params: " + params.toString());
        String message="";
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid filePlace: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        //Log.v(TAG, "Posting '" + body + "' to " + filePlace);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            //Log.e("URL", "> " + filePlace);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response

            int status = conn.getResponseCode();
            message=conn.getResponseMessage();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //Log.e("resp", "> " + response.toString());
            return response.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return message;
    }


    public static JSONObject getJSONFromUrl(String url1) {


        //Log.e("JSONParser:32", "Making request to: " + filePlace);
        // Making HTTP request
        try {
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(10000 /* milliseconds */);
            //conn.setConnectTimeout(15000 /* milliseconds */);
            //conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            //Log.e("alp", json.toString());
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String

        return jObj;
    }
}