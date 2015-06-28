package com.example.jamin.pikachu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by jamin on 6/17/15.
 */
public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting message from intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //Creating the TextView with the message
        TextView textView = new TextView(this);
        textView.setText(message);

        // Setting the texteview onto context
        setContentView(textView);
    }
}
