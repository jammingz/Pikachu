package com.example.jamin.pikachu;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {

    // Test Variables
    private static final int TAB_COUNT = 5;
    private static final String[] sumting = {"yolo","swag","i","am","sellout"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListAdapter(new CustomAdapter());
    }


    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public String getItem(int position) {
            return sumting[position];
        }

        @Override
        public long getItemId(int position) {
            return sumting[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_view,parent,false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position));
            return convertView;
        }
    }

}
