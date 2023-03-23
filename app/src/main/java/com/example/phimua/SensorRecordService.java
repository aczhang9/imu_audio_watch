package com.example.phimua;

import static com.example.phimua.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;


// options:
// 1. create a class for sensor recording and call on class in AudioRecordService
// 2. create service for sensor recording i.e. here but how to connect this back to main activity?

public class SensorRecordService extends Service {
    private static final String TAG = SensorRecordService.class.getSimpleName();;
    private SensorRecorder sensorRecorder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        Log.d(TAG, "SensorRecordService");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("PhIMU-A service")
                        .setContentText("recording IMU")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .build();

        // Notification ID cannot be 0.
        startForeground(1, notification);
        sensorRecorder = new SensorRecorder(this);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        sensorRecorder.stopSensors();
    }
}

