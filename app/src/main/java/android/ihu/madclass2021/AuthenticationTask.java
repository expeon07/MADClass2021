package android.ihu.madclass2021;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AuthenticationTask extends AsyncTask<String, Void, InputStream> {

    @Override
    protected InputStream doInBackground(String... urls) {
        // String response = "";
        InputStream inputStream = null;

        try {
            URL url = new URL(urls[0]);

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(false);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                try {
                    // InputStream inputStream;
                    int status = urlConnection.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK) {
                        inputStream = urlConnection.getErrorStream();
                    } else {
                        inputStream = urlConnection.getInputStream();
                    }

                    // response = readStream(inputStream);

                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException ioex) {
                // response = ioex.toString();
                Log.e("PlaceholderFragment", "Error", ioex);
            }
        } catch (MalformedURLException muex) {
            // response = muex.toString();
            Log.e("PlaceholderFragment", "Error", muex);
        }
        return inputStream;
    }

    @Override
    protected void onPostExecute(InputStream result) {
        Log.d("onPostExecute","Result: " + result);

        String str_response = readStream(result);
        Log.d("onPostExecute","String Result: " + str_response);

        // mainActivity.getResponse(result);
        List<XMLParser.Response> responses;

        XMLParser xmlParser = new XMLParser();

        try {
            responses = xmlParser.parse(result);
            System.out.println(responses);
            Log.d("onPostExecute","Response: " + responses);

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }


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
