package com.example.jamin.pikachu;

/**
 * Created by jamin on 6/20/15.
 */
public class RThread { // reddit thread
    private String user;
    private String timestamp;
    private String title;
    private int score;
    private int commentNum;
    private int id;

    public RThread(String user, String timestamp, String title, int score, int num, int id) {
        this.user = user;
        this.timestamp = timestamp;
        this.title = title;
        this.score = score;
        this.id = id;
        this.commentNum = num;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public int getScore() {
        return score;
    }

    public int getId() {
        return id;
    }

    public int getCommentNum() {
        return commentNum;
    }
}