package com.example.englishapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WallManager extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "WallManager";

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap;
        try {
//            URL url = new URL(strings[0]);

        //
            String key="AIzaSyDdBPCVzYyCmtFtZSSihqOSUsPZglM5x3E";

            URL url = new URL(
                    "https://www.googleapis.com/customsearch/v1?key="+
                            key+"&cx=42a504d9a5afa4755&q="+
                            strings[0]+"&alt=json"+"&searchType=image"
            );
            Log.i(TAG, "URL1 - " + url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Log.i(TAG, "CONNECTION - " + conn.toString());

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Accept", "application/json");

            Log.i(TAG, "URL-3 -- " + conn.getInputStream());


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            Log.i(TAG, "BR - " + br);

            String output;
            while ((output = br.readLine()) != null) {

                if(output.contains("\"link\": \"")){
                    String link=output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
                    Log.i(TAG, "IMAGE - " + link);     //Will print the google search links

                    url = new URL(link);

                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();

                        InputStream inputStream = connection.getInputStream();

                        return BitmapFactory.decodeStream(inputStream);

                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
