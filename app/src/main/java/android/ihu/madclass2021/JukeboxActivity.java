package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class JukeboxActivity extends AppCompatActivity {

    static final String JUKEBOX_URL= "http://mad.mywork.gr/get_song.php?t=";
    String token = MainActivity.token;

    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jukebox);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // disable play and pause buttons
        deactivatePlay();
        deactivatePause();

        // execute asynctask on request button click
        ImageButton request_button = findViewById(R.id.btn_request);
        request_button.setOnClickListener(v -> {
            String request_song_url = JUKEBOX_URL + token;
            JukeboxActivity.JukeboxTask requestSongTask = new JukeboxActivity.JukeboxTask();

            try {
                Log.d("Jukebox onStart", "button click");
                requestSongTask.execute(request_song_url);

            } catch (Exception e) {
                Log.d("Jukebox onStart error", "error on button");
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }


    public class JukeboxTask extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... urls) {
            // disable all buttons
            deactivateRequest();
            deactivatePause();
            deactivatePlay();

            InputStream inputStream = null;

            try {
                URL url = new URL(urls[0]);

                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(false);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    try {
                        int status = urlConnection.getResponseCode();

                        if (status != HttpURLConnection.HTTP_OK) {
                            inputStream = urlConnection.getErrorStream();
                        } else {
                            inputStream = urlConnection.getInputStream();
                        }

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException ioex) {
                    Log.e("PlaceholderFragment", "Error", ioex);
                }
            } catch (MalformedURLException muex) {
                Log.e("PlaceholderFragment", "Error", muex);
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream result) {
            Log.d("onPostExecute","Result: " + result);

            // change tv_status "Requesting a song from CTower"
            TextView tv_status = findViewById(R.id.tv_status);
            tv_status.setText(R.string.requesting);

            HashMap<String, String> response = new HashMap<>();

            XmlPullParserFactory pullParserFactory;
            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                Log.d("JukeboxTask","onPostExecute result: " + result);
                parser.setInput(result, null);

                XMLParser xmlParser = new XMLParser();
                response = xmlParser.parseXML(parser);

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            String status = response.get("status");

            Log.d("Jukebox onPostExecute", "status: " + status);

            switch (status) {
                case "0-FAIL":
                    // TODO transfer to LoginActivity
                    break;

                case "2-OK":
                    String song_title = response.get("title");
                    String song_artist = response.get("artist");
                    String song_url = response.get("url");

                    // call SongPlayer
                    try {
                        SongPlayer(song_title, song_artist, song_url);
                    } catch (IOException | XmlPullParserException e) {
                        e.printStackTrace();
                    }
            }


        }
    }


    public void SongPlayer(String title, String artist, String url) throws IOException, XmlPullParserException {

        Uri myUri = Uri.parse(url);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        ImageButton play_button = findViewById(R.id.btn_play);
        ImageButton pause_button = findViewById(R.id.btn_pause);
        ImageButton request_button = findViewById(R.id.btn_request);

        mediaPlayer.setDataSource(getApplicationContext(), myUri);
        mediaPlayer.prepare();
        mediaPlayer.start();

        // enable btn_pause
        deactivatePlay();
        activatePause();
        activateRequest();

        TextView song_artist = findViewById(R.id.song_artist);
        TextView song_title = findViewById(R.id.song_title);
        TextView song_url = findViewById(R.id.song_url);
        TextView tv_status = findViewById(R.id.tv_status);

        song_artist.setText(artist);
        song_title.setText(title);
        song_url.setText(url);
        tv_status.setText(R.string.playing);

        // click pause
        pause_button.setOnClickListener(v -> {
            mediaPlayer.pause();
            deactivatePause();
            activatePlay();
            tv_status.setText(R.string.stopped);
        });

        // click play
        play_button.setOnClickListener(v -> {
            mediaPlayer.start();
            deactivatePlay();
            activatePause();
            tv_status.setText(R.string.playing);
        });

        // execute asynctask on request button click
        request_button.setOnClickListener(v -> {
            mediaPlayer.stop();
            tv_status.setText(R.string.requesting);

            String request_song_url = JUKEBOX_URL + token;
            JukeboxActivity.JukeboxTask requestSongTask = new JukeboxActivity.JukeboxTask();

            try {
                Log.d("Jukebox onStart", "button click");
                requestSongTask.execute(request_song_url);

            } catch (Exception e) {
                Log.d("Jukebox onStart error", "error on button");
                e.printStackTrace();
            }
        });

    }

    private void activatePlay() {
        ImageButton play_button = findViewById(R.id.btn_play);
        play_button.setEnabled(true);
        play_button.setImageResource(R.drawable.play_button);
    }

    private void deactivatePlay() {
        ImageButton play_button = findViewById(R.id.btn_play);
        play_button.setEnabled(false);
        play_button.setImageResource(R.drawable.play_grey);
    }

    private void activatePause() {
        ImageButton pause_button = findViewById(R.id.btn_pause);
        pause_button.setEnabled(true);
        pause_button.setImageResource(R.drawable.pause_button);
    }

    private void deactivatePause() {
        ImageButton pause_button = findViewById(R.id.btn_pause);
        pause_button.setEnabled(false);
        pause_button.setImageResource(R.drawable.pause_grey);
    }

    private void activateRequest() {
        ImageButton request_button = findViewById(R.id.btn_request);
        request_button.setEnabled(true);
        request_button.setImageResource(R.drawable.streaming);
    }

    private void deactivateRequest() {
        ImageButton request_button = findViewById(R.id.btn_request);
        request_button.setEnabled(false);
        request_button.setImageResource(R.drawable.streaming_grey);
    }
}
