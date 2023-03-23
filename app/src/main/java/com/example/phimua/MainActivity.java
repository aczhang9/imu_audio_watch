package com.example.phimua;

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

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";

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

    }

    public void OnStartButtonClicked(View v){
        if (!checkPermissonfromDevice())
            requestPermission();

        // TODO: integrate audio and IMU recording
        // TODO: determine how potentially large amounts of data will be saved

        // start audio recording
        //audioRecordServiceIntent = new Intent(this, AudioRecordService.class);
        //startService(audioRecordServiceIntent);

        // start sensor recording
        sensorRecordServiceIntent = new Intent(this, SensorRecordService.class);
        startService(sensorRecordServiceIntent);

        start_recording.setEnabled(false);

    }

    public void OnStopButtonClicked(View v){

        // stop audio recording
        //stopService(audioRecordServiceIntent);

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

}