package org.avvento.apps.onlineradio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AvventoMedia {
    private MediaPlayer radio;
    private static String URL = "https://radio.avventohome.org/radio/8000/avvento.mp3";

    public AvventoMedia() {
        try {
            radio = new MediaPlayer();
            radio.setAudioStreamType(AudioManager.STREAM_MUSIC);
            radio.setDataSource(URL);
            radio.prepare();
        } catch (IOException e) {
            Log.e("AvventoRadio Error!", e.getMessage());
        }
    }

    public MediaPlayer getRadio() {
        return radio;
    }

}
