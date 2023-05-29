package com.example.englishapp.messaging;

import static com.example.englishapp.database.Constants.BASE_URL;
import static com.example.englishapp.database.Constants.REMOTE_MSG_DATA;
import static com.example.englishapp.database.Constants.REMOTE_MSG_TITLE;
import static com.example.englishapp.database.Constants.REMOTE_MSG_USER_SENDER;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.englishapp.database.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend  {

    private static final String TAG = "SendNotification";
    String userFcmToken;
    String title;
    String body;
    String senderUID;
    Context mContext;

    private RequestQueue requestQueue;

    public FCMSend(String userFcmToken, String title, String body, String senderUID, Context mContext) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.senderUID = senderUID;
        this.mContext = mContext;
    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mContext);
        JSONObject mainObj = new JSONObject();
        try {

            Log.i(TAG, "UserUID - " + senderUID);

            mainObj.put("to", userFcmToken);
            JSONObject notificationObject = new JSONObject();
            notificationObject.put(REMOTE_MSG_TITLE, title);
            notificationObject.put(REMOTE_MSG_DATA, body);
            notificationObject.put(REMOTE_MSG_USER_SENDER, senderUID);

            mainObj.put("data", notificationObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, mainObj, response -> {

                // code run is got response
                Log.i(TAG, "Successfully sent notification");

                Log.i(TAG, response.toString());

            }, error -> {
                // code run is got error

                Log.i(TAG, "Error sent notification");

            }) {
                @Override
                public Map<String, String> getHeaders() {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + Constants.SERVER_KEY);
                    return header;

                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
        }

    }
}