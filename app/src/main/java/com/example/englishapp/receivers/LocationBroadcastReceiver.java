package com.example.englishapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.example.englishapp.services.ForegroundLocationService;

public class LocationBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent serviceIntent = new Intent(context, ForegroundLocationService.class);

                context.startForegroundService(serviceIntent);
            }

            Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();

        }
    }
}
