package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

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

    // String token = "";
    String token = "";
    static final String TOKEN_VERIFICATION_URL = "http://mad.mywork.gr/authenticate.php?t=";
    static final String TOKEN_GENERATION_URL = "http://mad.mywork.gr/generate_token.php?e=";
    String user_email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Test", "onCreate successful.");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // contact and download the contents of the Web page located
        String token_authentication_url = TOKEN_VERIFICATION_URL + token;
        AuthenticationTask authenticationTask = new AuthenticationTask();

        try {
            authenticationTask.execute(token_authentication_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void generateToken() {
        setContentView(R.layout.login_activity);

        ImageButton submit_email = findViewById(R.id.submit_email);
        submit_email.setOnClickListener(v -> {
            EditText email =  findViewById(R.id.email_address);
            user_email = email.getText().toString();

            // contact email verification Web page,
            String token_generation_url = TOKEN_GENERATION_URL + user_email;
            AuthenticationTask authenticationTask = new AuthenticationTask();
            try {
                authenticationTask.execute(token_generation_url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // located at http://mad.mywork.gr/generate_token.php?e=your_email
            //String status = "1-OK"; //"";

        });

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

                response = parseXML(parser);

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            String status = response.get("status");
            String msg = response.get("msg");

            Log.d("onPostExecute", "Response status: " + status);
            Log.d("onPostExecute", "Response message: " + msg);

            status = "0-OK";

            switch (status) {
                case "0-OK":
                    setContentView(R.layout.menu_activity);

                    TextView menu_greeting = findViewById(R.id.menu_greeting);
                    menu_greeting.setText(msg);
                    break;

                case "0-FAIL":
                    generateToken();
                    break;

                case "1-OK":
                    // TODO token generated successfully
                    // TODO Rerun the application and set new token
                    setContentView(R.layout.menu_activity);
                    break;

                case "1-FAIL":
                    // print error message(make visible):
                    String error_msg = getResources().getString(R.string.email_error, user_email);

                    TextView login_error = findViewById(R.id.email_error);
                    login_error.setText(error_msg);
                    login_error.setVisibility(View.VISIBLE);

                    // Toast: Authentication Failed
                    Toast.makeText(getApplicationContext(),
                            "Authentication Failed",
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }


    private HashMap<String, String> parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        HashMap<String, String> response = new HashMap<>();
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    response = new HashMap<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("status")){
                        response.put("status", parser.nextText());
                    } else if (name.equals("msg")){
                        response.put("msg", parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }

        return response;
    }
}