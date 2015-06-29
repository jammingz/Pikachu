package com.example.jamin.pikachu;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamin on 6/18/15.
 */
public class ApacheXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public List<RThread> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser); // returns a list of rthreads
        } finally {
            in.close();
        }
    }

/*   **Do not need method. We are not writing to server

    public static String toXmlString(Message message) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        String namespace = "ns0:";
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", namespace + "feed");
            serializer.attribute("", "xmlns:ns0", "http://somewhere.com/");
            serializer.startTag("", namespace + "message");
            serializer.startTag("", namespace + "user");
            serializer.text(message.user);
            serializer.endTag("", namespace + "user");
            serializer.startTag("", namespace + "timestamp");
            serializer.text(message.timestamp);
            serializer.endTag("", namespace + "timestamp");
            serializer.startTag("", namespace + "content");
            serializer.attribute("", "type", "text");
            serializer.text(message.content);
            serializer.endTag("", namespace + "content");
            serializer.endTag("", namespace + "message");
            serializer.endTag("", namespace + "feed");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
*/

    private List<RThread> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List rthreads = new ArrayList(); // List of rthreads

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the message tag
            if (name.equals("thread")) {
                rthreads.add(readRThread(parser)); // adds a rthread into the list of rthreads
            } else {
                skip(parser);
            }
        }
        return rthreads;
    }



    // Parses the contents of an message. If it encounters a user, timestamp, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private RThread readRThread(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "thread");
        String user = null;
        String timestamp = null;
        String title = null;
        int score = 0;
        int id = 0;

        id = readID(parser);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("user")) {
                user = readUser(parser);
            } else if (name.equals("time")) {
                timestamp = readTimestamp(parser);
            } else if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("score")) {
                score = readScore(parser);
            } else {
                skip(parser);
            }
        }
        return new RThread(user, timestamp, title, score, id);
    }

    // Processes user tags in the feed.
    private String readUser(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "user");
        String user = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "user");
        return user;
    }

    // Processes link tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes timestamp tags in the feed.
    private String readTimestamp(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "time");
        String timestamp = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "time");
        return timestamp;
    }

    // For the tags user and timestamp, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Reads the thread ID attribute string and parses it into an Integer
    private int readID(XmlPullParser parser) throws IOException, XmlPullParserException {
        return Integer.valueOf(parser.getAttributeValue(ns,"id"));
    }

    // Reads the score attribute string and parses it into an Integer
    private int readScore(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "score");
        String score = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "score");
        return Integer.valueOf(score);
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}