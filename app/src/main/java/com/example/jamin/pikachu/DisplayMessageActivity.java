package com.example.jamin.pikachu;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private CommentTree commentDatabase; // Stores the chain of comments in a tree
    private TextView bodyMessage;
    //private LinearLayout comments;
    private TextView commentsNum;
    private CustomCommentsAdapter mAdapter;

    private String bodyMessageString;
    private String commentsNumString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing database
        commentDatabase = new CommentTree(null);

        // Getting fields from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        commentsNumString = intent.getStringExtra(MainActivity.EXTRA_COMMENTSNUM);
        String score = intent.getStringExtra(MainActivity.EXTRA_SCORE);
        bodyMessageString = "Null";

        // Filling out the title of the thread
        setTitle("(" + score + ") " + title);


        // Inflating the detailed_topic.xml layout
        setContentView(R.layout.detailed_topic);

        // Create ListView Adapter
        mAdapter = new CustomCommentsAdapter();
        ListView lView = (ListView) findViewById(android.R.id.list);

        lView.setAdapter(mAdapter);

        lView.setDivider(null); // Turn off border for listview

        new ReadDetailedPageTask().execute(MainActivity.TARGET_LINK + "samplethread3.xml");

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

                bodyMessageString = (String) args.get(1); // Getting body message

                /*
                for (int i = 2; i < args.size(); i++) { // Iterate through all the comments and its children and insert into database
                    commentDatabase.add(args.get(i));
                    Log.i("Adding Comment Node","Node: #" + String.valueOf(i));
                }
                */
                commentDatabase = (CommentTree) args.get(2);
                Log.i("Adding Comment Tree", "Node:root");

                if (commentDatabase.getSize() == 0) { // Write to message log if there comment section is empty
                    Log.e("Formatting Comments","databasesize = 0");
                } else {
                    fillComments(commentDatabase, 10); // generic max depth of 10. *** CHANGE LATER ***
                }

                mAdapter.notifyDataSetChanged(); // refresh view after data is fetched into database
            } else {
                Log.i("Connection Status-2","Failed");
            }
        }

        // Turns the tree database into CommentViews and add its view into comments section. We only look into a depth of maxDepth
        private void fillComments(CommentTree tree, int maxDepth) {
            Stack<CommentNode> stack = new Stack<CommentNode>(); // We use a queue to implement depth first search.
            Stack<Integer> depthStack = new Stack<Integer>(); // Keeps track of depth levels for each node

            int curDepth = 0;
            int curIndex = 0;

            // Now we insert the tree nodes into the stack
            CommentNode root = tree.getRoot();
            stack.add(root);
            depthStack.add(curDepth);
            while(stack.size() > 0 && curDepth < maxDepth) { // while the stack is not empty and is less than the desired max depth
                CommentNode curNode = stack.pop();
                curDepth = depthStack.pop();

                // We dont have to worry about cyclical graph since it's impossible.
                // First, we add the children (if any) to the stack
                for (int i = 0; i < curNode.getChildren().size(); i++) {
                    stack.add(curNode.getChildren().get(i)); // Add each child into the stack
                    depthStack.add(curDepth+1);
                }

                if (curDepth == 0) {
                    continue; // We skip parsing info from the root node of the tree.
                }

                //CommentView newComment = new CommentView(getApplicationContext(),curDepth-1);
                //newComment.setText(curNode.toString());
                //comments.addView(newComment);
                //results += curNode.toString() + "\n"; // Otherwise, we parse the information from the node if it is not node
            }
        }



    }

    // Filling the data for the listview The first row of the listview is the body. Therefore it takes up the 0th index and everything else is offset by +1 index. Index:1 is the first comment
    public class CustomCommentsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return commentDatabase.getSize()+1;
        }

        @Override
        public CommentNode getItem(int position) {
            if (position == 0) { // position 0 is the body message. Do not need to reference from the comment database
                return null;
            }
            return commentDatabase.getNode(position - 1);
        }

        @Override
        public long getItemId(int position) {
            if (position == 0) { // position 0 is the body message. Do not need to reference from the comment database
                return 0;
            }
            return commentDatabase.getNode(position-1).hashCode();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position == 0) ? 0 : 1; // Return 0 if position is 0. Otherwise, return 1. 0 is the body layout. 1 is the comment layout
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("getting view:","view: " + String.valueOf(position));
            int type = getItemViewType(position);
            if (convertView == null) {
                if (type == 0) { // Get the first view of the listview. That is the body of the thread
                    convertView = getLayoutInflater().inflate(R.layout.list_row_body, parent, false);
                } else {
                    convertView = getLayoutInflater().inflate(R.layout.list_row_comment, parent, false);
                }
            }

            if (type == 0) {
                // Creating the TextView with the message
                bodyMessage =  (TextView) convertView.findViewById(R.id.bodyMessage); // create reference to body message TextView
                commentsNum = (TextView) convertView.findViewById(R.id.commentsNum);
                commentsNum.setText(commentsNumString);

                /*
                 * GHETTO HTML FORMATTING. TEMPORARY CODE.!!!!!!!!
                 */

                bodyMessageString = bodyMessageString.replace("<li>","&nbsp;&nbsp;&#149;&nbsp;");
                bodyMessageString = bodyMessageString.replace("</li>","<br>");
                bodyMessageString = bodyMessageString.replace("<ol>","<p>");
                bodyMessageString = bodyMessageString.replace("</ol>","</p>");
;
                bodyMessage.setText(android.text.Html.fromHtml(bodyMessageString));

                } else {
                CommentNode node = commentDatabase.getNode(position-1);
                // Indent the view based on the comment's depth level
                int depth = node.getDepth();

                // Create new layout params for adjusted margin
                LinearLayout layoutWrapper = (LinearLayout) convertView.findViewById(R.id.list_row_comment_layout_wrapper);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutWrapper.getLayoutParams();
                params.setMargins((depth - 1) * 100, 0, 0, 0);
                layoutWrapper.setLayoutParams(params);


                TextView user = (TextView) convertView.findViewById(R.id.list_row_comment_user);
                TextView score = (TextView) convertView.findViewById(R.id.list_row_comment_score);
                TextView comment = (TextView) convertView.findViewById(R.id.list_row_comment);

                // Substituting the strings into the coressponding fields
                int scoreVal = node.getScore();
                if (scoreVal > 9999) {
                    scoreVal = 9999; // set score to 9999 if overflowed
                }

                score.setText(String.valueOf(scoreVal));
                user.setText(node.getUser());
                String commentText = node.getMessage();
                /*
                 * GHETTO HTML FORMATTING. TEMPORARY CODE.!!!!!!!!

                 */

                commentText = commentText.replace("<li>","&nbsp;&nbsp;&#149;&nbsp;");
                commentText = commentText.replace("</li>","<br>");
                commentText = commentText.replace("<ol>","<p>");
                commentText = commentText.replace("</ol>","</p>");

                comment.setText(android.text.Html.fromHtml(commentText));
            }
            return convertView;
        }

    }


}
