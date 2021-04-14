package android.ihu.madclass2021;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    static final String TOKEN_VERIFICATION_URL = "http://mad.mywork.gr/authenticate.php?t=XYZ";
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
        // contact and download the contents of the Web page located
        // at http://mad.mywork.gr/authenticate.php?t=XYZ
        String response = "";
        authenticationTask.execute(TOKEN_VERIFICATION_URL, response); // .get();

        Log.i("Response", response);
        String status = "";
        String message = "";

        verifyToken(status, message);

    }


    private void verifyToken(String status, String message) {
        if (status.equals("0-OK")) {
            // print message on screen
        } else if (status.equals("0-FAIL")) {
            // call obtainToken method
            obtainToken();
        }
    }

    private void obtainToken() {
        setContentView(R.layout.login_activity);

        // listen to submit button
        // get email

        // contact another Web page,
        // located at http://mad.mywork.gr/generate_token.php?e=your_email
        String user_email = "";
        String status = "";
    }

    private void verifyEmail(String status, String user_email) {
        if (status.equals("1-OK")) {
            // make everything invisible and
            // print on the screen what it is sent inside the msg tag

            // Rerun the application.
            // You should be immediately transferred to MenuActivity
            setContentView(R.layout.menu_activity);

        } else if (status.equals("1-FAIL")) {
            // print error message(make visible):
            // "Cannot identify email user_email"

            // Toast: Authentication Failed
        }
     }
}