package com.example.englishapp.domain.repositories;

import static android.content.Context.ALARM_SERVICE;
import static com.example.englishapp.data.database.Constants.KEY_SHOW_NOTIFICATION_WORD;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.englishapp.data.database.DataBaseLearningWords;
import com.example.englishapp.domain.interfaces.CompleteListener;
import com.example.englishapp.domain.receivers.AlarmReceiver;

public class AlarmRepository {

    public void cancelAlarm(Context context, CompleteListener listener) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent alarm = new Intent(context, AlarmReceiver.class);
        alarm.putExtra(KEY_SHOW_NOTIFICATION_WORD, true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, alarm, PendingIntent.FLAG_MUTABLE);

        // cancel previous
        alarmManager.cancel(pendingIntent);

        new DataBaseLearningWords().deleteLearningWords(context, new CompleteListener() {
            @Override
            public void OnSuccess() {
                listener.OnSuccess();
            }

            @Override
            public void OnFailure() {
                listener.OnFailure();
            }
        });
    }

}
