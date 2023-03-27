package com.example.phimua;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class AudioRecorder {

    private String audioFileName = null;
    private String activity;
    private Context context;
    private SimpleDateFormat simpleDateFormat;
    private MediaRecorder mediaRecorder;
    private AudioRecorder audioRecorder = null;
    private String audioDirPath;
    private static final String TAG = AudioRecorder.class.getSimpleName();
    private static final int BYTES_IN_ONE_MB = 1048576; // 1MB = 1024 KB * 1024 Bytes

    public AudioRecorder(Context context){
        this.context = context;
        mediaRecorder = new MediaRecorder();
        /*
        AudioRecord audioRecordHigh = new AudioRecord(MediaRecorder.AudioSource.MIC,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
         */
        activity = "";
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        audioDirPath = context.getExternalFilesDir(null) + "/audio";
        createAudioDataFolder();
    }

    public void createAudioDataFolder() {
        try {
            File audioFolderDir = new File(audioDirPath);

            if (!audioFolderDir.exists()) {
                audioFolderDir.mkdirs();
                Log.d(TAG, "Audio data folder directory : " + audioDirPath); ///storage/emulated/0/Android/data/com.example.phimua/files/audio
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAudioRecordProcess(){

        getAvailableDeviceMemory();
        startRecording();
        // TODO: do something to UI if not enough memory is available
/*
        if(getAvailableDeviceMemory() > 50){
            configureMediaRecorderSetting();
            startRecording();
            Log.d(TAG, "Recording start");
        } else{
            // don't record
            // outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gpp";
            //outputFile = context.getFilesDir().getAbsolutePath(); // store to phone memory
            Log.d(TAG, "Not enough memory available to record audio");
 */
    }

    public void configureMediaRecorderSetting(){

        try {
            if(mediaRecorder != null){
                // TODO: tweak these values as needed
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncodingBitRate(23050);
                mediaRecorder.setAudioEncoder(AudioEncoder.AMR_WB);
                mediaRecorder.setAudioSamplingRate(16000);
                mediaRecorder.setAudioChannels(1); // mono recording
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startRecording(){
        try {
            if(mediaRecorder != null){
                String currentDateTime = simpleDateFormat.format(new Date());
                audioFileName = currentDateTime +".3gpp";
                String outputFilePath = audioDirPath + File.separator + audioFileName;
                Log.d(TAG, "Start recording Audio File : " + outputFilePath);
                configureMediaRecorderSetting();
                mediaRecorder.setOutputFile(outputFilePath);
                mediaRecorder.prepare();
                mediaRecorder.start();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
            /*
             * if start() is called before prepare() || prepare() is called after start() or setOutputFormat()
             * then IllegalStateException might occur
             * */
        } catch (IOException e) {
            e.printStackTrace();
            // prepare() fails
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            if(mediaRecorder != null){
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                audioFileName = null;
                activity = "";
                Log.d(TAG, "Audio File saved successfully !");
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
            // it is called before start()
        } catch (RuntimeException e) {
            e.printStackTrace();
            // no valid audio/video data has been received
        }
    }

    public long getAvailableDeviceMemory() {

        long availableMegaBytes = 0;
        long availableBytes = 0;

        try {
            String path = "/storage/emulated/0/"; // phone memory
            //String path = "/storage/sdcard1/"; // sd card memory
            //String path = Environment.getDataDirectory().getPath(); // /data
            //String path = Environment.getExternalStorageDirectory().getPath(); // /storage/emulated/0
            StatFs statFs = new StatFs(audioDirPath);
            //Log.d(TAG, "Storage Path : " + path);

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

}
