package com.example.englishapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Wallpaper extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "WALL";

    public void setWallpaper(String search, Context context) throws Exception {
        Toast.makeText(context, "BEGIN WALL", Toast.LENGTH_SHORT).show();

        String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
        String charset = "UTF-8";

        Toast.makeText(context, URLEncoder.encode(search, charset), Toast.LENGTH_SHORT).show();

        URL url = new URL(google + URLEncoder.encode(search, charset));

        Toast.makeText(context, "URL1 " + url.toString(), Toast.LENGTH_SHORT).show();

        Toast.makeText(context, "URL2 " + url.openStream(), Toast.LENGTH_SHORT).show();

        Reader reader = new InputStreamReader(url.openStream(), charset);

        Toast.makeText(context, "Reader - " + reader.toString(), Toast.LENGTH_SHORT).show();

        GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);

        Toast.makeText(context, "RESULTS " + results.toString(), Toast.LENGTH_SHORT).show();

        // Show title and URL of 1st result.
        System.out.println("MY -- " + results.getResponseData().getResults().get(0).getTitle());
        System.out.println(results.getResponseData().getResults().get(0).getUrl());

        Log.e(TAG, "URL = " +  results.getResponseData().getResults().get(0).getUrl());

    }

    public void setWallpaperNew(String search, Context context) {
        String key="AIzaSyDdBPCVzYyCmtFtZSSihqOSUsPZglM5x3E";

        try {

            URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+key+ "&cx=013036536707430787589:_pqjad5hr1a&q="+ search + "&alt=json");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Accept", "application/json");

            Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();

            Toast.makeText(context, "STREAM - " + new InputStreamReader(
                    (conn.getInputStream())), Toast.LENGTH_SHORT).show();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            Toast.makeText(context, "GOOD14", Toast.LENGTH_SHORT).show();


            String output;
            while ((output = br.readLine()) != null) {

                if(output.contains("\"link\": \"")){
                    String link=output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
                    System.out.println(link);       //Will print the google search links
                    Toast.makeText(context, link, Toast.LENGTH_SHORT).show();
                }
            }
            conn.disconnect();

        } catch (Exception e) {
            Toast.makeText(context, "WARNING", Toast.LENGTH_SHORT).show();
        }
    }

    public void setWallpaperNew2(String search, Context context) {
        try{
            String key="AIzaSyDdBPCVzYyCmtFtZSSihqOSUsPZglM5x3E";
            URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=Godfather");
            URLConnection connection = url.openConnection();

            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONObject json = new JSONObject(builder.toString());
            String imageUrl = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).getString("url");

        } catch(Exception e){
        e.printStackTrace();
    }
    }

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




            //

//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
//
//            InputStream inputStream = connection.getInputStream();
//
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//
//            return bitmap;

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
