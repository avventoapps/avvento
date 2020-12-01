package org.avvento.apps.onlineradio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {
    private static String URL = "https://radio.avventohome.org/radio/8000/avvento.mp3";
    public static final String NOTIFICATION_CHANNEL_ID = "1";
    private final static String default_notification_channel_id = "AVVENTO_RADIO";
    public static MainActivity instance;
    private Button streamBtn;
    // true if playing, false if stopped
    private boolean playing = false;
    private NotificationManager notificationManager;
    private Intent buttonIntent;

    public boolean getPlaying() {
        return playing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        streamBtn = findViewById(R.id.audioStreamBtn);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        initialise();
    }

    private void initialise() {
        boolean networkOn = isNetworkAvailable();
        streamBtn.setEnabled(networkOn);
        if (!networkOn) {
            streamBtn.setText(getString(R.string.connect_to_internet));
        } else if (playing) {
            streamBtn.setText(getString(R.string.stop_streaming));
        } else {
            streamBtn.setText(getString(R.string.start_streaming));
        }
    }

    private void start() {
        streamBtn.setText(getString(R.string.stop_streaming));
        startService(new Intent(this, Stream.class));
        playing = true;
    }

    private void stop() {
        streamBtn.setText(getString(R.string.start_streaming));
        stopService(new Intent(this, Stream.class));
        playing = false;
    }

    public void playTrigger() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        buttonIntent = new Intent(this, NotificationReceiver.class);
        buttonIntent.putExtra("notificationId", NOTIFICATION_CHANNEL_ID);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id);
        mBuilder.setContentTitle(getString(R.string.avvento_media));
        mBuilder.setContentText(getString(R.string.slogan));
        mBuilder.setContentIntent(pendingIntent);
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);

        if (!playing) {
            start();
            mBuilder.addAction(R.drawable.ic_launcher_foreground, getString(R.string.stop_streaming), btPendingIntent);
        } else {
            stop();
            mBuilder.addAction(R.drawable.ic_launcher_foreground, getString(R.string.start_streaming), btPendingIntent);
        }

        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);

        mBuilder.setDeleteIntent(getDeleteIntent());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.cancelAll();
        notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), mBuilder.build());
    }

    public void playAudio(View view) {
        playTrigger();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.schedule) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://avventohome.org/avventoradio-schedules")));
            return true;
        } else if(id == R.id.ads) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://avventohome.org/avvento-radio-ads")));
            return true;
        } else if(id == R.id.broadcast) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://avventohome.org/previous-broadcasts")));
            return true;
        } else if(id == R.id.whatsapp) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/256779931314")));
            return true;
        } else if(id == R.id.whatsapp_group) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/I6meIZSpogs43FbpAYvsLJ")));
            return true;
        } else if(id == R.id.radio) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://media.avventohome.org")));
            return true;
        } else if(id == R.id.facebook) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/AvventoProductions")));
            return true;
        } else if(id == R.id.youtube) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/avventoProductions")));
            return true;
        } else if(id == R.id.mixcloud) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mixcloud.com/avventoProductions")));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialise();
    }

    protected PendingIntent getDeleteIntent() {
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        intent.setAction("notification_cancelled");
        return PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}