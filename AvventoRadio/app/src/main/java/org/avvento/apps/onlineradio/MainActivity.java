package org.avvento.apps.onlineradio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vinay.ticker.lib.TickerView;

import org.avvento.apps.onlineradio.events.StreamingEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements Serializable {
    private Button streamBtn;
    private EventBus bus = EventBus.getDefault();
    private AvventoMedia radio;
    private Info info;

    private void initialise() {
        if (info == null) {
            streamBtn.setText(getString(R.string.connect_to_internet));
            streamBtn.setEnabled(false);
        } else {
            streamBtn.setEnabled(true);
            radio = new AvventoMedia();
            if (radio.getRadio().isPlaying()) {
                streamBtn.setText(getString(R.string.pause_streaming));
            } else {
                streamBtn.setText(getString(R.string.start_streaming));
            }
        }
    }

    public void playAudio(View view) {
        bus.post(new StreamingEvent(radio));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private Info getInfo() {
        //allow running all on the main thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }
        try {
            java.net.URL url = new URL("https://raw.githubusercontent.com/avventoapps/avvento/master/AvventoRadio/info");
            return new Gson().fromJson(new Scanner(url.openStream()).useDelimiter("\\Z").next(), Info.class);
        } catch(Exception ex) {
            Log.e("AvventoRadio Error!", ex.getMessage());
            return null;
        }
    }

    private void showInfo() {
        ((TextView) findViewById(R.id.info)).setText(info.getInfo());
        ((TextView) findViewById(R.id.warning)).setText(info.getWarning());
        final TickerView tickerView = findViewById(R.id.tickerView);
        // Add multiple views to be shown in the ticker
        for (int i = 0; i < info.getTicker().length; i++) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            tv.setText(info.getTicker()[i]);
            tv.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            tv.setPadding(10, 5, 10, 5);
            tickerView.addChildView(tv);
        }

        // Call the showTickers() to show them on the screen
        tickerView.showTickers();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onStreaming(StreamingEvent streamingEvent){
        if(streamingEvent.getAvventoMedia().getRadio().isPlaying()) {
            streamingEvent.getAvventoMedia().getRadio().pause();
            streamBtn.setText(getString(R.string.start_streaming));
        } else {
            streamingEvent.getAvventoMedia().getRadio().start();
            streamBtn.setText(getString(R.string.pause_streaming));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        streamBtn = findViewById(R.id.audioStreamBtn);
        info = getInfo();
        initialise();
        showInfo();
        bus.register(this);


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialise();
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
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
        } else if(id == R.id.exit) {
            finish();
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}