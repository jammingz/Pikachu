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
    private String id;
    private String thumbnail;

    public RThread(String user, String timestamp, String title, int score, int num, String id, String thumbnail) {
        this.user = user;
        this.timestamp = timestamp;
        this.title = title;
        this.score = score;
        this.id = id;
        this.commentNum = num;
        this.thumbnail = thumbnail;
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

    public String getId() {
        return id;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}