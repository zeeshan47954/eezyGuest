package com.example.bookandpostroom;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ReviewWorker extends Worker {

    private static final String PREFS_NAME = "ReviewPrefs";
    private static final String KEY_NOTIFICATION_SHOWN = "notification_shown_";

    public ReviewWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        // Create a unique key for this specific booking/payment
        // You can pass a booking ID through inputData when creating the work request
        String bookingId = getInputData().getString("booking_id");
        if (bookingId == null) {
            bookingId = "default"; // fallback if no ID provided
        }

        // Check if notification has already been shown for this booking
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean alreadyShown = prefs.getBoolean(KEY_NOTIFICATION_SHOWN + bookingId, false);

        if (alreadyShown) {
            // Notification already shown, don't show again
            return Result.success();
        }

        // Check notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, skip posting notification
                return Result.failure();
            }
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "review_channel")
                .setSmallIcon(R.drawable.reviewphoto)
                .setContentTitle("Help others")
                .setContentText("Tap to leave a quick review for the room\nIgnore if already reviewed")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, ReviewActivity.class),
                                PendingIntent.FLAG_IMMUTABLE
                        )
                );

        // Post the notification
        NotificationManagerCompat.from(context).notify(1, builder.build());

        // Mark notification as shown
        prefs.edit().putBoolean(KEY_NOTIFICATION_SHOWN + bookingId, true).apply();

        return Result.success();
    }
}