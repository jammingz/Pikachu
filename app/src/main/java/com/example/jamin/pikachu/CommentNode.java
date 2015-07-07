package com.example.jamin.pikachu;

import java.util.ArrayList;

/**
 * Created by jamin on 7/6/15.
 */
public class CommentNode {
    private String user;
    private int score;
    private String message;
    private CommentNode parent;
    private ArrayList<CommentNode> children;
    private int size;

    public CommentNode(CommentNode parent) {
        user = "{empty}";
        score = 0;
        message = "{empty}";
        this.parent = parent;
        children = new ArrayList<CommentNode>();
        size = 0;
    }

    public CommentNode(String user, int score, String message, CommentNode parent) {
        this.user = user;
        this.score = score;
        this.message = message;
        this.parent = parent;
        size = 0;
    }

    public void addChild(CommentNode node) {
        children.add(node);
        size++;
    }

    public int getSize() {
        return size;
    }

}

