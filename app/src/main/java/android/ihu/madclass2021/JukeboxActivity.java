package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
                requestSongTask.execute(request_song_url);

                // change tv_status "Requesting a song from CTower"
                TextView tv_status = findViewById(R.id.tv_status);
                tv_status.setText("Requesting song from CTower");

                // disable all buttons
                ImageButton play_button = findViewById(R.id.btn_play);
                ImageButton pause_button = findViewById(R.id.btn_pause);

                play_button.setEnabled(false);
                pause_button.setEnabled(false);

            } catch (Exception e) {
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

            Log.d("onPostExecute","inputStream: " + inputStream);

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream result) {
            Log.d("onPostExecute","Result: " + result);

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

            switch (status) {
                case "0-FAIL":


                    break;

                case "2-OK":

                    break;
            }

        }
    }
}