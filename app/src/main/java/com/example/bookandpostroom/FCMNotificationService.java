package com.example.bookandpostroom;

import com.google.firebase.messaging.FirebaseMessagingService;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class FCMNotificationService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "default_channel";
    private static final String CHANNEL_NAME = "Default Channel";
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        updatetoken(token);
    }

    private void updatetoken(String token) {
        // Implement your token update logic here
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle data payload
        if (remoteMessage.getData().size() > 0) {
            handleDataMessage(remoteMessage.getData());
        }

        // Handle notification payload
        if (remoteMessage.getNotification() != null) {
            handleNotificationMessage(remoteMessage.getNotification());
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        String title = data.get("title");
        String message = data.get("message");
        if (title == null) title = "New Message";
        if (message == null) message = "You have a new notification";

        showNotification(title, message);
    }

    private void handleNotificationMessage(RemoteMessage.Notification notification) {
        String title = notification.getTitle();
        String message = notification.getBody();
        showNotification(title, message);
    }

    private void showNotification(String title, String message) {
        SharedPreferences sh = getSharedPreferences("spnotify", MODE_PRIVATE);
        String notificationState = sh.getString("notify", "on");

        if (notificationState.equals("off")) return;

        // Create unique notification ID based on timestamp
        int notificationId = (int) System.currentTimeMillis();

        // Create PendingIntent based on whether app is running
        PendingIntent pendingIntent;
        if (isAppInForeground()) {
            // App is open - create an empty intent that does nothing
            pendingIntent = PendingIntent.getActivity(this, notificationId,
                    new Intent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            // App is closed - create intent to open the app
            Intent resultIntent;
            if ("request".equals(title)) {
                resultIntent = new Intent(this, Activity5.class);
                resultIntent.putExtra(Activity5.notificationMessage, "request");
            } else if ("accepted".equals(title)) {
                resultIntent = new Intent(this, MainActivity.class);
                resultIntent.putExtra(studentinfoandrooms.notificationMessage, "accepted");
            } else {
                resultIntent = new Intent(this, MainActivity.class);
            }

            // Add flags to handle proper activity stack
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent = PendingIntent.getActivity(this, notificationId,
                    resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.applogoreal)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        // Play sound and vibrate
        playNotificationSound();
        vibrate();

        // Show notification
        mNotificationManager.notify(notificationId, builder.build());
    }

    /**
     * Check if the app is currently in the foreground
     */
    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        final String packageName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Default notification channel");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 300, 300, 300});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private void playNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                r.setLooping(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrate() {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                long[] pattern = {0, 100, 300, 300, 300};
                v.vibrate(pattern, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//package com.example.bookandpostroom;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Vibrator;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.Map;
//
//public class FCMNotificationService extends FirebaseMessagingService {
//    private static final String CHANNEL_ID = "default_channel";
//    private static final String CHANNEL_NAME = "Default Channel";
//    private NotificationManager mNotificationManager;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        createNotificationChannel();
//    }
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        super.onNewToken(token);
//        updatetoken(token);
//    }
//
//    private void updatetoken(String token) {
//        // Implement your token update logic here
//    }
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        // Handle data payload
//        if (remoteMessage.getData().size() > 0) {
//            handleDataMessage(remoteMessage.getData());
//        }
//
//        // Handle notification payload
//        if (remoteMessage.getNotification() != null) {
//            handleNotificationMessage(remoteMessage.getNotification());
//        }
//    }
//
//    private void handleDataMessage(Map<String, String> data) {
//        String title = data.get("title");
//        String message = data.get("message");
//        if (title == null) title = "New Message";
//        if (message == null) message = "You have a new notification";
//
//        showNotification(title, message);
//    }
//
//    private void handleNotificationMessage(RemoteMessage.Notification notification) {
//        String title = notification.getTitle();
//        String message = notification.getBody();
//        showNotification(title, message);
//    }
//
//    private void showNotification(String title, String message) {
//        SharedPreferences sh = getSharedPreferences("spnotify", MODE_PRIVATE);
//        String notificationState = sh.getString("notify", "on");
//
//        if (notificationState.equals("off")) return;
//
//        // Create appropriate intent based on notification type
//        Intent resultIntent;
//        if ("request".equals(title)) {
//            resultIntent = new Intent(this, Activity5.class);
//            resultIntent.putExtra(Activity5.notificationMessage, "request");
//        } else if ("accepted".equals(title)) {
//            resultIntent = new Intent(this, MainActivity.class);
//            resultIntent.putExtra(studentinfoandrooms.notificationMessage, "accepted");
//        } else {
//            resultIntent = new Intent(this, MainActivity.class);
//        }
//
//        // Add flags to handle proper activity stack
//        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        // Create unique notification ID based on timestamp
//        int notificationId = (int) System.currentTimeMillis();
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId,
//                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.applogoreal)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
//
//        // Play sound and vibrate
//        playNotificationSound();
//        vibrate();
//
//        // Show notification
//        mNotificationManager.notify(notificationId, builder.build());
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            channel.setDescription("Default notification channel");
//            channel.enableLights(true);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100, 300, 300, 300});
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            mNotificationManager.createNotificationChannel(channel);
//        }
//    }
//
//    private void playNotificationSound() {
//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                r.setLooping(false);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void vibrate() {
//        try {
//            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            if (v != null && v.hasVibrator()) {
//                long[] pattern = {0, 100, 300, 300, 300};
//                v.vibrate(pattern, -1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}