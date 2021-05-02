package android.ihu.madclass2021;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class XMLParser {

    public HashMap<String, String> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
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
