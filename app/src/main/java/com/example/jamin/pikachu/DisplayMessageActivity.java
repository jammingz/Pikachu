package com.example.jamin.pikachu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by jamin on 6/17/15.
 */
public class DisplayMessageActivity extends AppCompatActivity {
    private String bodyMessage; // Stores the message
    private CommentTree commentDatabase; // Stores the chain of comments in a tree
    private TextView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing database
        commentDatabase = new CommentTree(null);

        // Getting message from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Filling out the title of the thread
        setTitle(title);

        // Creating the TextView with the message
        testView = new TextView(this);
        testView.setText(message);

        new ReadDetailedPageTask().execute(MainActivity.TARGET_LINK + "samplethread2.xml");

        // Setting the texteview onto context
        setContentView(testView);
    }

    /* Read information for detailed thread page. Reads xml file from server */

    private class ReadDetailedPageTask extends AsyncTask<String,Void,List> {

        @Override
        protected List doInBackground(String... args) {
            // TODO Auto-generated method stub
            String myurl = args[0];
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;
            ApacheDetailedXmlParser parser = new ApacheDetailedXmlParser();
            int responseCode = -1;
            List results = new ArrayList();

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //conn.setReadTimeout(10000 /* milliseconds */);
                //conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                responseCode= conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into array of body message and comments
                results = parser.parse(is); // results is a list of comments with the first index being (in text) the body message


                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            results.add(0, String.valueOf(responseCode)); // add status code to the beginning of list
            return results;
        }

        protected void onPostExecute(List args) {
            String responseCode = (String) args.get(0);
            int responseCodeInt = Integer.parseInt(responseCode); // We have the status code
            // TextView status = (TextView) findViewById(R.id.status);
            // status.setText(responseCode);
            if (responseCodeInt == 200) { // Parse xml only if status is 200/OK
                Log.i("Connection Status-2", "Connected");
                // If data has been received, we input the cache the data into our database

                // First we wipe out any old data in the database
                commentDatabase = new CommentTree(null);

                bodyMessage = (String) args.get(1); // Getting body message. WARNING: ADDRESS NULL STRING LATER

                /*
                for (int i = 2; i < args.size(); i++) { // Iterate through all the comments and its children and insert into database
                    commentDatabase.add(args.get(i));
                    Log.i("Adding Comment Node","Node: #" + String.valueOf(i));
                }
                */
                commentDatabase = (CommentTree) args.get(2);
                Log.i("Adding Comment Tree","Node:root");

                String messageToDisplay = "";
                if (commentDatabase.getSize() == 0) {
                    messageToDisplay = "databasesize = 0";
                } else {
                    messageToDisplay = formatTree(commentDatabase,10); // generic max depth of 10. *** CHANGE LATER ***
                }
                testView.setText(messageToDisplay);


            } else {
                Log.i("Connection Status-2","Failed");
            }
        }

        // Turns the tree database into a readable string. We only look into a depth of maxDepth
        private String formatTree(CommentTree tree, int maxDepth) {
            String results = ""; // This will be the string we return
            Stack<CommentNode> stack = new Stack<CommentNode>(); // We use a queue to implement depth first search.
            Stack<Integer> depthStack = new Stack<Integer>(); // Keeps track of depth levels for each node

            int curDepth = 0;

            // Now we insert the tree nodes into the stack
            CommentNode root = tree.getRoot();
            stack.add(root);
            depthStack.add(curDepth);
            while(stack.size() > 0 && curDepth < maxDepth) { // while the stack is not empty and is less than the desired max depth
                CommentNode curNode = stack.pop();
                curDepth = depthStack.pop();

                // We dont have to worry about cyclical graph since it's impossible.
                // First, we add the children (if any) to the stack
                for (int i = 0; i < curNode.getSize(); i++) {
                    stack.add(curNode.getChildren().get(i)); // Add each child into the stack
                    depthStack.add(curDepth+1);
                }

                if (curDepth == 0) {
                    continue; // We skip parsing info from the root node of the tree.
                }

                results += numTabs(curDepth-1) + " <" + curNode.getMessage() + ">\n "; // Otherwise, we parse the information from the node if it is not node
            }
            return results;
        }

        private String numTabs(int n) {
            String eachTab = "   "; // Three spaces = 1 tab
            return new String(new char[n]).replace("\0", eachTab); // each tab is multiplied n times
        }



    }


}
