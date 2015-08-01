package com.example.jamin.pikachu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jamin on 7/15/15.
 */
public class CommentView extends TextView {
    private int marginLeftTab; // Number of tabs to indent

    public CommentView(Context context) {
        super(context);
        this.setPadding(10, 10, 10, 10);
        setTextColor(Color.BLACK);
    }

    public CommentView(Context context, int marginLeftTab) {
        this(context);
        this.marginLeftTab = marginLeftTab;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        params.setMargins(this.marginLeftTab * 100,0,0,0);
        this.setLayoutParams(params);
}

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    // Change the number of tags to indent and change the textview's params
    public void setMargin(int margin) {
        marginLeftTab = margin;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        params.setMargins(marginLeftTab * 4,0,0,0);
        this.setLayoutParams(params);
    }
}
