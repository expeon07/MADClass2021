package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


public class MainActivity extends AppCompatActivity {

    static final String TOKEN_VERIFICATION_URL = "http://mad.mywork.gr/authenticate.php?t=";
    public static String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Test", "onCreate successful.");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (token.equals("")) {
            token = "XYZ";
        }

        Log.d("onStart", "Token: " + token);

        // contact and download the contents of the Web page located
        String token_authentication_url = TOKEN_VERIFICATION_URL + token;
        AuthenticationTask authenticationTask = new AuthenticationTask();

        try {
            authenticationTask.execute(token_authentication_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class AuthenticationTask extends AsyncTask<String, Void, InputStream> {
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

                Log.d("AuthenticationTask", "onPostExecute result: " + result);
                parser.setInput(result, null);

                XMLParser xmlParser = new XMLParser();
                response = xmlParser.parseXML(parser);

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            String status = response.get("status");
            String msg = response.get("msg");

            Log.d("onPostExecute", "Response status: " + status);
            Log.d("onPostExecute", "Response message: " + msg);

            switch (status) {
                case "0-OK":
                    Intent menu_intent = new Intent(MainActivity.this,
                            MenuActivity.class);
                    startActivity(menu_intent);

                    MenuActivity.menu_message = msg;

                    Toast.makeText(getApplicationContext(),
                            "Authentication successful",
                            Toast.LENGTH_LONG).show();
                    break;

                case "0-FAIL":
                    Intent login_intent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    startActivity(login_intent);
                    break;
            }
        }
    }

}