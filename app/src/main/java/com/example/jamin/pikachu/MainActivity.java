package com.example.jamin.pikachu;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    public static final String EXTRA_TITLE = "com.example.pikachu.jamin.TITLE";
    private static final String TARGET_LINK = "http://107.200.40.169:82/";
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rThreadDatabase = new RThreadDatabase();
        setContentView(R.layout.activity_main);

        // Read pikachu.xml from server
        Log.i("Status", "Preparing to Fetch Xml");
        new ReadMainPageTask().execute(TARGET_LINK + "pikachu.xml");
        Log.i("Status", "Fetched Xml Complete");
        Toast.makeText(this,"Reading from Server",Toast.LENGTH_SHORT);

        // Create ListView Adapter
        mAdapter = new CustomAdapter();
        setListAdapter(mAdapter);
        ListView lView = (ListView) findViewById(android.R.id.list);


        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),DisplayMessageActivity.class);
                TextView titleView = (TextView) view.findViewById(R.id.list_row_main_title);
                String title = titleView.getText().toString();//"Position: " + String.valueOf(position);
                String message = "001";// SAMPLE hash
                //based on item add info to intent
                intent.putExtra(EXTRA_TITLE,title);
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

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                 convertView = getLayoutInflater().inflate(R.layout.list_row_main,parent,false);

            }


            // Getting height of the screen
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int screenHeight = size.y;//parent.getMeasuredHeight();
            int screenWidth = size.x;//parent.getMeasuredWidth();
            int rowHeight = screenHeight/10; // Lets try 8 rows for the screen
            int rowWidth = screenWidth;

            // Formatting the listView

            convertView.getLayoutParams().height = rowHeight;
            convertView.setBackgroundColor(Color.CYAN);

            TextView score = (TextView) convertView.findViewById(R.id.list_row_main_score);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.list_row_main_avatar);
            TextView title = (TextView) convertView.findViewById(R.id.list_row_main_title);
            TextView comments = (TextView) convertView.findViewById(R.id.list_row_main_comments);
            TextView placeholder = (TextView) convertView.findViewById(R.id.list_row_main_comments_placeholder);
            RelativeLayout rLayout = (RelativeLayout) convertView.findViewById(R.id.list_row_main_right_layout);

            int scoreWidth = (int) (0.1 * rowWidth);
            int scoreHeight = rowHeight;

            int avatarWidth = rowHeight;
            int avatarHeight = rowHeight;

            int rLayoutWidth = rowWidth - scoreWidth - avatarWidth;
            int rLayoutHeight = screenHeight;

            int titleWidth = rLayoutWidth;
            // int titleHeight = (int) (0.75 * rowHeight);

            //int commentsHeight = (int) (0.25 * rowHeight);

            //int placeholderHeight = commentsHeight;

            score.getLayoutParams().width = scoreWidth;
            score.getLayoutParams().height = scoreHeight;

            avatar.getLayoutParams().width = avatarWidth;
            avatar.getLayoutParams().height = avatarHeight;

            rLayout.getLayoutParams().width = rLayoutWidth;
            rLayout.getLayoutParams().height = rLayoutHeight;

            title.getLayoutParams().width = titleWidth;
            //title.getLayoutParams().height = titleHeight;

            //comments.getLayoutParams().height = commentsHeight;

            //placeholder.getLayoutParams().height = placeholderHeight;

            avatar.setBackgroundColor(Color.GREEN);

            // Substituting the strings into the coressponding fields
            RThread curRThread = rThreadDatabase.getRThread(position);
            int scoreVal = curRThread.getScore();
            if (scoreVal > 9999) {
                scoreVal = 9999; // set score to 9999 if overflowed
            }

            score.setText(String.valueOf(scoreVal));
            title.setText(curRThread.getTitle());
            comments.setText(String.valueOf(curRThread.getCommentNum()));

            /*
            //((TextView) convertView.findViewById(R.id.list_row_main_body)).setText(getItem(position));

            RThread curRThread = rThreadDatabase.getRThread(position);
            TextView score = (TextView) convertView.findViewById(R.id.list_row_main_score);
            TextView title = (TextView) convertView.findViewById(R.id.list_row_main_title);
            score.setText(String.valueOf(curRThread.getScore()));
            title.setText(curRThread.getTitle());
            //convertView.setBackgroundColor(R.color.red);
            //ImageView avatar = (ImageView) convertView.findViewById(R.id.list_row_main_avatar);
            //avatar.setLayoutParams(new LinearLayout.LayoutParams(avatar.getMeasuredHeight(),avatar.getMeasuredHeight())); // Making the imageview square shaped
            */
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
                mAdapter.notifyDataSetChanged(); // refresh view after data is fetched into database
            } else {
                Log.i("Connection Status","Failed");
            }
        }

    }


}
