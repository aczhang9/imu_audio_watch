package com.example.phimua;

import static com.example.phimua.App.CHANNEL_ID;
import static com.example.phimua.MainActivity.stopThread;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

public class AudioRecordService extends Service {

    private AudioManager audioManager;
    private AudioRecorder audioRecorder;
    private static final String TAG = AudioRecordService.class.getSimpleName();;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        audioRecorder = new AudioRecorder(this);
        /*
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setMode(audioManager.MODE_NORMAL);
        audioManager.setBluetoothScoOn(true);
        audioManager.startBluetoothSco();
        audioManager.setSpeakerphoneOn(false);
         */
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        Log.d(TAG, "inside AudioRecordService onStartCommand()");
        stopThread = false;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("PhIMU-A service")
                        .setContentText("recording audio and IMU data")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .build();

        // Notification ID cannot be 0.
        startForeground(1, notification);

        if(audioRecorder != null){
            audioRecorder.startAudioRecordProcess();
        }else{
            Log.d(TAG, "audioRecorder is null");
        }
        return START_NOT_STICKY;

        //return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onUnBind(Intent arg0) {
        return null;
    }

    public void onStop() {
        Log.d(TAG, "onStop()");
    }

    public void onPause() {
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        audioRecorder.stopRecording();
        audioRecorder = null;
        /*
        audioManager.stopBluetoothSco();
        audioManager.setMode(audioManager.MODE_NORMAL);
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(true);
         */
    }

    @Override
    public void onLowMemory() {

    }

    private boolean isAudioServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
