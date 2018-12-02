package com.google.developer.colorvalue.utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class ClearNotificationService extends IntentService {

    private static final String TAG = ClearNotificationService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ClearNotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "ClearNotification called");

        Context context = ClearNotificationService.this;

        NotificationControllerUtility.clearAllNotifications(context);

    }
}
