package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    String token = "";
    static final String TOKEN_VERIFICATION_URL = "http://mad.mywork.gr/authenticate.php?t=";
    static final String EMAIL_VERIFICATION_URL = "http://mad.mywork.gr/generate_token.php?e=";

    AuthenticationTask authenticationTask = new AuthenticationTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Test", "onCreate successful.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO contact and download the contents of the Web page located
        // at http://mad.mywork.gr/authenticate.php?t=XYZ
        String response = "";
        authenticationTask.execute(TOKEN_VERIFICATION_URL + token, response); // .get();

        Log.i("Response", response);
        String status = "0-FAIL"; //"";
        String message = "hiyaaaa"; //"";

        verifyToken(status, message);

    }


    private void verifyToken(String status, String message) {
        if (status.equals("0-OK")) {
            setContentView(R.layout.menu_activity);

            TextView menu_greeting = findViewById(R.id.menu_greeting);
            menu_greeting.setText(message);

        } else if (status.equals("0-FAIL")) {
            obtainToken();
        }
    }

    private void obtainToken() {
        setContentView(R.layout.login_activity);

        ImageButton submit_email = findViewById(R.id.submit_email);
        submit_email.setOnClickListener(v -> {
            EditText email =  findViewById(R.id.email_address);
            String user_email = email.getText().toString();

            // TODO contact another Web page,
            // located at http://mad.mywork.gr/generate_token.php?e=your_email
            String status = "1-OK"; //"";

            verifyEmail(status, user_email);
        });

    }

    private void verifyEmail(String status, String user_email) {
        if (status.equals("1-OK")) {
            // TODO Rerun the application and set new token
            //setContentView(R.layout.menu_activity);

        } else if (status.equals("1-FAIL")) {
            // print error message(make visible):
            String error_msg = getResources().getString(R.string.email_error, user_email);

            TextView login_error = findViewById(R.id.email_error);
            login_error.setText(error_msg);
            login_error.setVisibility(View.VISIBLE);

            // Toast: Authentication Failed
            Toast.makeText(getApplicationContext(),
                    "Authentication Failed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}