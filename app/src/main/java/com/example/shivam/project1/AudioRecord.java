package com.example.shivam.project1;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AudioRecord extends AppCompatActivity {

    private MediaRecorder recorder;
    private String fullpath;

    public void startRecording() {
        recorder = new MediaRecorder();
        Log.v("ds", "hello");
        Date currentTime = Calendar.getInstance().getTime();
        Log.v("ds1234", getTimestamp());
        fullpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getTimestamp() + ".mp3";
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setOutputFile(fullpath);
        Log.v("ds", fullpath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("ds", e.toString());
        }

        recorder.start();
    }

    public String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(new Date());
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

}
