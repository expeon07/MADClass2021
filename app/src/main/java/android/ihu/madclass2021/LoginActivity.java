package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

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

public class LoginActivity extends AppCompatActivity {

    private String user_email = "";
    static final String TOKEN_GENERATION_URL = "http://mad.mywork.gr/generate_token.php?e=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ImageButton submit_email = findViewById(R.id.submit_email);
        submit_email.setOnClickListener(v -> {
            EditText email = findViewById(R.id.email_address);
            user_email = email.getText().toString();

            // contact email verification Web page
            String token_generation_url = TOKEN_GENERATION_URL + user_email;
            LoginActivity.TokenGenerationTask tokenGenerationTask = new TokenGenerationTask();
            try {
                tokenGenerationTask.execute(token_generation_url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public class TokenGenerationTask extends AsyncTask<String, Void, InputStream> {

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
            Log.d("onPostExecute", "Result: " + result);

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

            Log.d("onPostExecute", "Response status: " + status);
            Log.d("onPostExecute", "Response message: " + msg);

            switch (status) {
                case "1-OK":
                    // make everything invisible
                    TextView email_textView = findViewById(R.id.email_textView);
                    email_textView.setVisibility(View.INVISIBLE);

                    EditText email_editText = findViewById(R.id.email_address);
                    email_editText.setVisibility(View.INVISIBLE);

                    ImageButton submit_email = findViewById(R.id.submit_email);
                    submit_email.setVisibility(View.INVISIBLE);

                    TextView error = findViewById(R.id.email_error);
                    error.setVisibility(View.INVISIBLE);

                    // display token generation successful message
                    TextView token_gen_msg = findViewById(R.id.token_gen_msg);
                    token_gen_msg.setText(msg);

                    Toast.makeText(getApplicationContext(),
                            "Authentication successful",
                            Toast.LENGTH_SHORT).show();

                    assert msg != null;
                    String[] token_result = msg.split(" ");
                    String token = token_result[token_result.length - 1];
                    Log.d("onPostExecute", "Generated token: " + token);

                    // Rerun the application and with the new token
                    Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(
                            getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);

                    Log.d("onPostExecute", "App restarted.");
                    Log.d("onPostExecute", "Token after restart: " + token);

                    MainActivity.token = token;
                    Log.d("onPostExecute", "Main Activity Token after restart: " +
                            MainActivity.token);

                    break;

                case "1-FAIL":
                    // print error message(make visible):
                    String error_msg = getResources().getString(R.string.email_error, user_email);

                    TextView login_error = findViewById(R.id.email_error);
                    login_error.setText(error_msg);
                    login_error.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(),
                            "Authentication failed",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}