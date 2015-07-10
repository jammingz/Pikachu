package com.example.jamin.pikachu;

/**
 * Created by jamin on 7/6/15.
 */
public class CommentTree {
    private CommentNode root;
    private int size;

    public CommentTree(CommentNode node) {
        if (node == null) {
            size = 0;
        } else {
            size = 1;
        }
        root = node;
    }

    public CommentNode getRoot() {
        return root;
    }

    public int getSize() { return size;}
}
