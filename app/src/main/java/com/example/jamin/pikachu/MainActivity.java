package com.example.jamin.pikachu;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private RThreadDatabase rThreadDatabase;
    public static final String EXTRA_MESSAGE = "com.example.pikachu.jamin.MESSAGE";
    private static final String TARGET_LINK = "http://107.200.40.169:82/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rThreadDatabase = new RThreadDatabase();
        setContentView(R.layout.activity_main);

        // Read pikachu.xml from server
        Log.i("Status","Preparing to Fetch Xml");
        new ReadMainPageTask().execute(TARGET_LINK + "pikachu.xml");
        Log.i("Status","Fetched Xml Complete");
        Toast.makeText(this,"Reading from Server",Toast.LENGTH_SHORT);

        setListAdapter(new CustomAdapter());
        ListView lView = (ListView) findViewById(android.R.id.list);


        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),DisplayMessageActivity.class);
                String message = "Position: " + String.valueOf(position);
                //based on item add info to intent
                intent.putExtra(EXTRA_MESSAGE,message);
                startActivity(intent);
            }

        });
    }


    public class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return rThreadDatabase.getSize();
        }

        @Override
        public RThread getItem(int position) {
            return rThreadDatabase.getRThread(position);
        }

        @Override
        public long getItemId(int position) {
            return rThreadDatabase.getRThread(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_row_main,parent,false);
            }

            //((TextView) convertView.findViewById(R.id.list_row_main_body)).setText(getItem(position));

            RThread curRThread = rThreadDatabase.getRThread(position);
            TextView score = (TextView) convertView.findViewById(R.id.list_row_main_score);
            TextView body = (TextView) convertView.findViewById(R.id.list_row_main_body);
            score.setText(String.valueOf(curRThread.getScore()));
            body.setText(curRThread.getTitle());
            return convertView;
        }

    }


	/* Read information for front page. Reads xml file from server */

    private class ReadMainPageTask extends AsyncTask<String,Void,List> {

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

                // Convert the InputStream into messages
                results = parser.parse(is); // results is a list of rthreads


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

            results.add(0, String.valueOf(responseCode));
            return results;
        }

        protected void onPostExecute(List args) {
            String responseCode = (String) args.get(0);
            int responseCodeInt = Integer.parseInt(responseCode); // We have the status code
            // TextView status = (TextView) findViewById(R.id.status);
            // status.setText(responseCode);
            if (responseCodeInt == 200) { // Parse xml only if status is 200/OK
                Log.i("Connection Status","Connected");
                // If data has been received, we input the cache the data into our database

                // First we wipe out any old data in the database
                rThreadDatabase = new RThreadDatabase();

                for (int i = 1; i < args.size(); i++) { // Iterate through all the RThreads and add each rthread into database
                    RThread curRThread = (RThread) args.get(i);
                    rThreadDatabase.insertRThread(curRThread);
                    Log.i("Adding Rthread","Rthread: #" + String.valueOf(i));
                }
            } else {
                Log.i("Connection Status","Failed");
            }
        }

    }


}
