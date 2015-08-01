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
    private int depth; // Depth in the tree
    private int index; // indexes the i'th comment from top to bottom. First comment at the top is the 0'th index comment

    public CommentNode(CommentNode parent) {
        user = "{empty}";
        score = 0;
        message = "{empty}";
        this.parent = parent;
        children = new ArrayList<CommentNode>();
        size = 0;
        depth = 0;
        index = -1; // Uninitialized index is -1

    }

    public CommentNode(String user, int score, String message, CommentNode parent, int depth) {
        this.user = user;
        this.score = score;
        this.message = message;
        this.parent = parent;
        children = new ArrayList<CommentNode>();
        size = 0;
        this.depth = depth;
        index = -1;
    }

    public void addChild(CommentNode node) {
        children.add(node); // We add child into list of children
        // Now we increment the size of current node and all its parents recursively
        CommentNode curNode = this;
        while(curNode != null) {
            curNode.incrementSize();
            curNode = curNode.getParent();
        }

    }

    public void setIndex(int index) {
        this.index = index;
    }

    // The size field only counts all the leaves
    public int getSize() {
        return size;
    }

    public String getMessage() { return message; }

    public ArrayList<CommentNode> getChildren() { return children; }

    public CommentNode getParent() { return parent; }

    public int getScore() {
        return score;
    }

    public String getUser() {
        return user;
    }

    public int getDepth() {
        return depth;
    }


    public String toString() {
        //return numTabs(depth-1) + user + " " + String.valueOf(score) + " points\n" + numTabs(depth) + getMessage();
        return user + " " + String.valueOf(score) + " points\n" + numTabs(1) + getMessage();
    }

    private String numTabs(int n) {
        String eachTab = "   "; // Three spaces = 1 tab
        return new String(new char[n]).replace("\0", eachTab); // each tab is multiplied n times
    }

    private void incrementSize() {
        size++;
    }

}

