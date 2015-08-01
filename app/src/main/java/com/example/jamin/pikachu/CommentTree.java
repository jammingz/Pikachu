package com.example.jamin.pikachu;

import java.util.ArrayList;

/**
 * Created by jamin on 7/6/15.
 */
public class CommentTree {
    private CommentNode root;
    private ArrayList<CommentNode> indexList;

    public CommentTree(CommentNode node) {
        root = node;
        indexList = new ArrayList<CommentNode>();
    }

    public CommentNode getRoot() {
        return root;
    }

    public int getSize() {
        if (root != null) {
            return root.getSize();
        }

        return 0;
    }

    public void addToIndexList(CommentNode node) {
        indexList.add(node);
    }

    // Returns the node in sequencial order.
    public CommentNode getNode(int index) {
        return indexList.get(index);
    }
}
