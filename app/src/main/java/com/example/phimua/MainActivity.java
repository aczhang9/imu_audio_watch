package com.example.phimua;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

//package com.example.regression;
// Link to this app:
// https://medium.com/analytics-vidhya/running-ml-models-in-android-using-tensorflow-lite-e549209287f0
// convert and load model:
// https://margaretmz.medium.com/e2e-tfkeras-tflite-android-273acde6588
// run a single inference:
//https://firebase.google.com/docs/ml/android/use-custom-models

import android.os.PowerManager;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    private static final int BYTES_IN_ONE_MB = 1048576;
    PowerManager.WakeLock wakeLock;
    static TextView warning;
    Button start_recording;
    Button close_app;
    Button stop_recording;
    final int REQUEST_PERMISSION_CODE =0;
    boolean bound;
    static boolean stopThread = false;
    Intent audioRecordServiceIntent;
    Intent sensorRecordServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_recording = findViewById(R.id.start_recording);
        close_app = findViewById(R.id.close_app);
        stop_recording = findViewById(R.id.stop_recording);
        warning = findViewById(R.id.hw);

    }

    public void OnStartButtonClicked(View v){
        if (!checkPermissonfromDevice())
            requestPermission();

        if (getAvailableDeviceMemory()<50){
            showToast("Not enough memory!");
            warning.setTextColor(Color.RED);
            warning.setText("NOT RECORDING");
            return;
        }
        else {
            // start audio recording
            audioRecordServiceIntent = new Intent(this, AudioRecordService.class);
            startService(audioRecordServiceIntent);

            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyApp::MyWakelockTag");
            wakeLock.acquire(); // make sure there is wakeLock.release()
            // start sensor recording
            sensorRecordServiceIntent = new Intent(this, SensorRecordService.class);
            startService(sensorRecordServiceIntent);

            start_recording.setEnabled(false);
            //start button should be disabled when UI returns from service notification
            // use broadcast receiver https://stackoverflow.com/questions/41827335/on-notification-button-click-intent-starting-new-activity-instead-of-resuming
            // fixed with android:launchMode="singleTask" in manifest file
        }
    }

    public void OnStopButtonClicked(View v){
        // stop audio recording
        wakeLock.release();
        stopService(audioRecordServiceIntent);

        // stop sensor recording
        stopService(sensorRecordServiceIntent);

        start_recording.setEnabled(true);
    }

    public void OnCloseAppButtonClicked(View v){
        finish();
        System.exit(0);
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissonfromDevice() {
        int write_external_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return (write_external_storage == PackageManager.PERMISSION_GRANTED) && (record_audio == PackageManager.PERMISSION_GRANTED);
    }

    public long getAvailableDeviceMemory() {

        long availableMegaBytes = 0;
        long availableBytes = 0;

        try {
            String path = "/storage/emulated/0/"; // phone memory
            StatFs statFs = new StatFs(path);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBytes = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
            } else {
                availableBytes = (long)statFs.getBlockSizeLong() * (long)statFs.getAvailableBlocksLong();
            }

            availableMegaBytes = availableBytes / BYTES_IN_ONE_MB;
            Log.d(TAG, "Available Memory : " + availableMegaBytes + " MB");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableMegaBytes;
    }
    private void showToast(@NonNull final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}