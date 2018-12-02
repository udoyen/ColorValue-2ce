package com.google.developer.colorvalue.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.developer.colorvalue.MainActivity;
import com.google.developer.colorvalue.R;
import com.google.developer.colorvalue.utilities.NotificationControllerUtility;

public class NotificationJobService extends JobService {

    public static final int NOTIFICATION_ID = 18;

    public static final String NOTIFICATION_CHANNEL = "color_cards";
    public static final String NOTIFICATION_CHANNEL_NAME = "colors";

    private AsyncTask mNotificationTask;

    @SuppressLint({"ObsoleteSdkInt", "StaticFieldLeak"})
    @Override
    public boolean onStartJob(final JobParameters params) {
        // TODO notification

        mNotificationTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                PackageManager packageManager = getPackageManager();
                // Create the intent to run
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                Intent intent = packageManager.getLaunchIntentForPackage("com.google.developer.colorvalue");
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Create Notification that will run the Pending Intent
                NotificationManager notificationManager = (NotificationManager)
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(
                            NOTIFICATION_CHANNEL,
                            NOTIFICATION_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH);
                    assert notificationManager != null;
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL)
                                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                                .setSmallIcon(R.drawable.ic_dialog_info)
                                .setContentTitle(getResources().getString(R.string.time_to_practice))
                                .setContentText(getResources().getString(R.string.it_is_time_to_practice))
                                .setDefaults(Notification.DEFAULT_VIBRATE)
                                .addAction(NotificationControllerUtility.ignoreReminderAction(getApplicationContext()))
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                }

                assert notificationManager != null;
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

                return null;
            }

            @Override
            protected void onPreExecute() {
                jobFinished(params, false);
            }
        };

        mNotificationTask.execute();
        return true;


    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mNotificationTask != null) mNotificationTask.cancel(true);
        return true;
    }
}