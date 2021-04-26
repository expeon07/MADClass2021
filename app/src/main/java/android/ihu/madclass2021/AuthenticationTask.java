package android.ihu.madclass2021;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AuthenticationTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        String response = "";

        try {
            URL url = new URL(urls[0]);

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(false);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                try {
                    InputStream inputStream;
                    int status = urlConnection.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK) {
                        inputStream = urlConnection.getErrorStream();
                    } else {
                        inputStream = urlConnection.getInputStream();
                    }

                    response = readStream(inputStream);

                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException ioex) {
                response = ioex.toString();
                Log.e("PlaceholderFragment", "Error", ioex);
            }
        } catch (MalformedURLException muex) {
            response = muex.toString();
            Log.e("PlaceholderFragment", "Error", muex);
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);

    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
