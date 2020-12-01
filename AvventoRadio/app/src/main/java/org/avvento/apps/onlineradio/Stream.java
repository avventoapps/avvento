package org.avvento.apps.onlineradio;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class Stream extends Service {
    private static String URL = "https://radio.avventohome.org/radio/8000/avvento.mp3";
    private static final String LOGCAT = null;
    private MediaPlayer radio;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Log.d(LOGCAT, "tuning into AvventoRadio!");
            radio = new MediaPlayer();
            radio.setAudioStreamType(AudioManager.STREAM_MUSIC);
            radio.setDataSource(URL);
            radio.prepare();
        } catch (IOException e) {
            Log.e("AvventoRadio Error!", e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (!radio.isPlaying()) {
            radio.start();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (radio.isPlaying()) {
            radio.stop();
        }
    }

    @Override
    public IBinder onBind(Intent objIndent) {
        return null;
    }

}
