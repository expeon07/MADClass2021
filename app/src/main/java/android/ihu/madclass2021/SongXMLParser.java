package android.ihu.madclass2021;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class SongXMLParser {
    public HashMap<String, String> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        HashMap<String, String> song = new HashMap<>();
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    song = new HashMap<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("title")){
                        song.put("title", parser.nextText());
                    } else if (name.equals("song")){
                        song.put("song", parser.nextText());
                    } else if (name.equals("url")){
                        song.put("url", parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }

        return song;
    }
}
