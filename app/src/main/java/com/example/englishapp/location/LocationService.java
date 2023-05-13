package com.example.englishapp.location;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.os.Build;
import android.util.Log;

public class LocationService extends JobService {

    private static final String TAG = "LocationService";

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.i(TAG, "started");

        try {
            scheduleRefresh();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return false;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "stopped");

        return false;
    }

    private void scheduleRefresh() {
        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(666,
                        new ComponentName(getPackageName(),
                                LocationService.class.getName()));

        /* For Android N and Upper Versions */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobBuilder
                    .setMinimumLatency(30 * 100) //YOUR_TIME_INTERVAL
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }
    }


}
