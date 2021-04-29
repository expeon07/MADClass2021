package android.ihu.madclass2021;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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

        Log.d("onPostExecute", "Response status: " + response.get("status"));
        Log.d("onPostExecute", "Response message: " + response.get("msg"));

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

