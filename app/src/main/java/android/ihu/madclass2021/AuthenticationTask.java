package android.ihu.madclass2021;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;


public class AuthenticationTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "Authentication";

    protected String doInBackground(String... url) {
        /*
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0]);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

        } catch (IOException e) {
            Log.d(TAG, "Cannot connect to server");
        }
         */
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
