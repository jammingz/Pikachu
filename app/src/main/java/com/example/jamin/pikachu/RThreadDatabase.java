package com.example.jamin.pikachu;

import java.util.ArrayList;

/**
 * Created by jamin on 6/19/15.
 */
public class RThreadDatabase {
    private ArrayList<RThread> database;

    public RThreadDatabase() {
        database = new ArrayList<RThread>();
    }

    public void insertRThread(RThread rthread) {
        database.add(rthread);
    }

    public void insertRThreadAtIndex(int index, RThread rthread) {
        database.add(index,rthread);
    }

    public void insertRThreadAtFront(RThread rthread) {
        database.add(0,rthread);
    }

    public void removeRThread() {
        int lastIndex = database.size() - 1;
        database.remove(lastIndex);
    }

    public boolean removeRThreadAtIndex(int index) {
        if (database.size() < index) { // If the database does not contain the index
            return false;
        } else {
            // If the database does contain the index, we remove it then return true
            database.remove(index);
            return true;
        }
    }

    public void removeRThreadLast() {
        int size = database.size();
        if (size > 0) {
            removeRThreadAtIndex(size-1);
        }
    }

    public RThread getRThread(int index) {
        return database.get(index);
    }

    public int getSize() {
        return database.size();
    }
}
