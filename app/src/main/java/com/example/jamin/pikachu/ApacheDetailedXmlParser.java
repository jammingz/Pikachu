package com.example.jamin.pikachu;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamin on 7/6/15.
 */
public class ApacheDetailedXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser); // returns a tree of comments into an arraylist but with the first index being the body of the message
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List results = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        Log.i("Executing", "readFeed()");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the message tag
            if (name.equals("body")) {
                results.add(readBody(parser)); // adds the body string into arraylist
            } else if (name.equals("comments")) {
                results.add(readComments(parser, results));
            } else {
                skip(parser);
            }
        }
        return results;
    }

    // Returns the content of the body as a String
    private String readBody(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i("Executing", "readBody()");
        parser.require(XmlPullParser.START_TAG, ns, "body");
        String body = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "body");
        return body;
    }

    // Parses through the tree of comments and inserts it into the arraylist
    private CommentTree readComments(XmlPullParser parser, List lst) throws XmlPullParserException, IOException {
        Log.i("Executing", "readComments()");
        /*
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        */
        ArrayList<ArrayList> referenceList = new ArrayList<ArrayList>();
        CommentNode root = new CommentNode("root",0,"root",null,0); // new CommentNode(null); // this is the root node


        CommentTree commentTree = new CommentTree(root);
        // Initialize first start tag
        int depth = 1;
        int lastDepth = 1;
        /*
        if (parser.next() != XmlPullParser.START_TAG) {
            Log.i("Parser.readComments()","Error");
            return null; // if the first tag is not a start tag, we messed up. abort mission
        }
        */

        CommentNode curNode = root; // Lets begin the iterations.
        String results = "<" + parser.getName() + ">";
        int curIndex = 0;

        // For testing purpose. we'll see what happens when we get text and put it in the Child Node's body message

        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    curNode = curNode.getParent(); // Go up a depth in the tree.
                    if (depth == 0) {
                        break; // We dont add the end tag for comments. We exit loop
                    }
                    break;
                case XmlPullParser.START_TAG:
                    // If this is a start tag. we need to determine if it is a comment tag
                    if (!parser.getName().trim().equals("comment")) {
                        skip(parser); // Should never apprach here. If we get here, we need to throw error
                        Log.e("ERROR","started with non-comment tag");
                        break;
                    }

                    // Extracting attribute values from the start-comment tag
                    int commentScore = Integer.valueOf(parser.getAttributeValue(ns,"score"));
                    String user = parser.getAttributeValue(ns,"user");

                    parser.next(); // Skip to the message tag
                    if (!parser.getName().trim().equals("message")) {
                        skip(parser); // Should never apprach here. If we get here, we need to throw error
                        Log.e("ERROR","started with non-message tag");
                        break;
                    }

                    parser.next(); // Skip to the message string
                    String messageString = parser.getText();
                    parser.next(); // Skip to end of message tag

                    // Update Nodes
                    CommentNode child = new CommentNode(user,commentScore,messageString,curNode,depth);
                    commentTree.addToIndexList(child);
                    child.setIndex(curIndex);
                    curNode.addChild(child);
                    curNode = child;
                    depth++;
                    curIndex++;
                    break;
                case XmlPullParser.TEXT:
                    String tagText = parser.getText();
                    if (tagText.trim().length() > 0) {
                        Log.e("ERROR","parser.text? how??"); // Should never reach here. Reach here if there is text outside of message tag and is NOT whitespace
                    } else {
                        continue; // current text is whitespace. continue to next tag element
                    }
                    break;
            }
        }

        return commentTree;

    }

    /*

    ********* USELESS SHIT I THINK


    // Parses the contents of an message. If it encounters a user, timestamp, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private RThread readRThread(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "thread");
        String user = null;
        String timestamp = null;
        String title = null;
        int score = 0;
        int commentNum = 0;
        String id = "null";

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
            } else if (name.equals("comments")) {
                commentNum = readCommentNum(parser);
            } else {
                skip(parser);
            }
        }
        return new RThread(user, timestamp, title, score, commentNum, id);
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


    // Reads the thread ID attribute string and parses it into an Integer
    private String readID(XmlPullParser parser) throws IOException, XmlPullParserException {
        return String.valueOf(parser.getAttributeValue(ns,"id"));
    }

    // Reads the score attribute string and parses it into an Integer
    private int readScore(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "score");
        String score = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "score");
        return Integer.valueOf(score);
    }

    // Reads the comment number attribute string and parses it into an Integer
    private int readCommentNum(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "comments");
        String comments = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "comments");
        return Integer.valueOf(comments);
    }

    */


    // For the tags user and timestamp, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
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
