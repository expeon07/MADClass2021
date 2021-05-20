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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jukebox);
    }

    @Override
    protected void onStart() {
        super.onStart();

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


    public class JukeboxTask extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... urls) {

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
            tv_status.setText("Requesting song from CTower");

            // disable all buttons
            ImageButton play_button = findViewById(R.id.btn_play);
            ImageButton pause_button = findViewById(R.id.btn_pause);
            play_button.setEnabled(false);
            pause_button.setEnabled(false);

            HashMap<String, String> response = new HashMap<>();

            XmlPullParserFactory pullParserFactory;
            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(result, null);

                XMLParser xmlParser = new XMLParser();
                response = xmlParser.parseXML(parser);

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            String status = response.get("status");
            String msg = response.get("msg");

            Log.d("Jukebox onPostExecute", "status" + status);

            switch (status) {
                case "0-FAIL":
                    // TODO transfer to LoginActivity
                    break;

                case "2-OK":
                    // call SongPlayer
                    try {
                        SongPlayer(msg);
                    } catch (IOException | XmlPullParserException e) {
                        e.printStackTrace();
                    }
            }
        }
    }


    public void SongPlayer(String song_details) throws IOException, XmlPullParserException {

        Log.d("SongPlayer", "Song Details: " + song_details);
        XmlPullParserFactory pullParserFactory;
        pullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser songParser = pullParserFactory.newPullParser();

        songParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

        // convert String msg to InputStream

        //use ByteArrayInputStream to get the bytes of the String and convert them to InputStream.
        InputStream msg_inputStream = new ByteArrayInputStream(song_details.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(msg_inputStream));
//                        String msg_inputstream = bufferedReader.readLine();

//                        while (msg_inputstream != null) {
//                            System.out.println(msg_inputstream);
//                            msg_inputstream = bufferedReader.readLine();
//                        }

        songParser.setInput(msg_inputStream, null);

        SongXMLParser songXmlParser = new SongXMLParser();
        HashMap<String, String> parsed_song_details = songXmlParser.parseXML(songParser);

        String title = parsed_song_details.get("title");
        String artist = parsed_song_details.get("artist");
        String song_url_string = parsed_song_details.get("url");
        Log.d("SongPlayer", "Song URL: " + song_url_string);

        Uri myUri = Uri.parse(song_url_string);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        // TODO enable btn_pause

        mediaPlayer.setDataSource(getApplicationContext(), myUri);
        mediaPlayer.prepare();
        mediaPlayer.start();

        TextView song_title = findViewById(R.id.song_title);
        TextView song_artist = findViewById(R.id.song_artist);
        TextView tv_status = findViewById(R.id.tv_status);

        song_title.setText(title);
        song_artist.setText(artist);
        tv_status.setText("Playing");

    }
}