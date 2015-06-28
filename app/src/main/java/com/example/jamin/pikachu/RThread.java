package com.example.jamin.pikachu;

/**
 * Created by jamin on 6/20/15.
 */
public class RThread { // reddit thread
    public final String user;
    public final String timestamp;
    public final String title;
    public final int score;
    public final int id;

    public RThread(String user, String timestamp, String title, int score, int id) {
        this.user = user;
        this.timestamp = timestamp;
        this.title = title;
        this.score = score;
        this.id = id;
    }
}