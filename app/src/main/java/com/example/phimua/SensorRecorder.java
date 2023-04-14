package com.example.phimua;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensorRecorder implements SensorEventListener {
    private static final String TAG = SensorRecorder.class.getSimpleName();
    private Context context;
    private SimpleDateFormat simpleDateFormat;
    private String imuDirPath;
    private String imuFileName = null;
    private String outputFilePath = null;
    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyroscope;
    public float[] gyro = new float[3];
    public float[] acc = new float[3];
    CSVWriter writer;
    FileWriter mFileWriter;

    public SensorRecorder(Context context) {
        this.context = context;
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        imuDirPath = context.getExternalFilesDir(null) + "/IMU";
        createIMUDataFolder();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // GEN 4 WATCH SETTINGS
        //sensorManager.registerListener(this, accelerometer, 150000); // 60Hz accelerometer sampling
        //sensorManager.registerListener(this, gyroscope, 150000); // I think there is tradeoff between accelerometer and gyroscope sampling rates

        // GEN 5 WATCH SETTINGS
        sensorManager.registerListener(this, accelerometer, 40000); // 60Hz accelerometer sampling
        sensorManager.registerListener(this, gyroscope, 40000); // I think there is tradeoff between accelerometer and gyroscope sampling rates

        Log.d(TAG, "Sensors created");
    }

    public void createIMUDataFolder() {
        try {
            File imuFolderDir = new File(imuDirPath);

            if (!imuFolderDir.exists()) {
                imuFolderDir.mkdirs();
                Log.d(TAG, "IMU data folder directory : " + imuDirPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String currentDateTime = simpleDateFormat.format(new Date());
        imuFileName = currentDateTime +".csv";
        outputFilePath = imuDirPath + File.separator + imuFileName;
        File f = new File(outputFilePath);
        try {
            mFileWriter = new FileWriter(outputFilePath, true);
            writer = new CSVWriter(mFileWriter);

            Log.d(TAG, "output file path : " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        switch (sensor.getType()) {
            case (Sensor.TYPE_ACCELEROMETER):
                acc = sensorEvent.values;
                break;
            case (Sensor.TYPE_GYROSCOPE):
                gyro = sensorEvent.values;
                break;
        }
        // simpleDateFormat.format(new Date()),
        String[] data = {simpleDateFormat.format(new Date()), String.format("%.3f", acc[0]), String.format("%.3f", acc[1]), String.format("%.3f", acc[2]),
                String.format("%.3f", gyro[0]), String.format("%.3f", gyro[1]), String.format("%.3f", gyro[2])};
        writer.writeNext(data);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void stopSensors(){
        Log.d(TAG, "Sensors stopped");
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);
        try {
            //String[] stop_time = {simpleDateFormat.format(new Date())};
            //writer.writeNext(stop_time);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
