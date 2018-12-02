package com.google.developer.colorvalue.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;

import com.google.developer.colorvalue.R;

public class NotificationControllerUtility {

    private static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    private static final int DISMISS_NOTIFICATION_ID = 28;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();
    }

    public static NotificationCompat.Action ignoreReminderAction(Context context) {
        Intent ignoreReminderIntent = new Intent(context, ClearNotificationService.class);
        ignoreReminderIntent.setAction(ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                DISMISS_NOTIFICATION_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignorReminderAction = new NotificationCompat.Action(
                R.drawable.ic_delete,
                "No Thanks",
                ignoreReminderPendingIntent);

        return ignorReminderAction;

    }
}


