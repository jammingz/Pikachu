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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamin on 6/17/15.
 */
public class DisplayMessageActivity extends AppCompatActivity {
    private String bodyMessage; // Stores the message
    private ArrayList commentDatabase; // Stores the chain of comments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing database
        commentDatabase = new ArrayList();

        // Getting message from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Filling out the title of the thread
        setTitle(title);

        // Creating the TextView with the message
        TextView textView = new TextView(this);
        textView.setText(message);

        // Setting the texteview onto context
        setContentView(textView);
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
            ApacheXmlParser parser = new ApacheXmlParser();
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
                commentDatabase = new ArrayList();

                bodyMessage = (String) args.get(1); // Getting body message. WARNING: ADDRESS NULL STRING LATER

                for (int i = 2; i < args.size(); i++) { // Iterate through all the comments and its children and insert into database
                    commentDatabase.add(args.get(i));
                    Log.i("Adding Comment Node","Node: #" + String.valueOf(i));
                }

            } else {
                Log.i("Connection Status-2","Failed");
            }
        }



    }


}
