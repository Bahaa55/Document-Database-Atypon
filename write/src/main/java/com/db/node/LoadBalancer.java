package com.db.node;

import java.util.Observable;

public class LoadBalancer extends Observable {

    public void scale(){
        ReadNode node = new ReadNode("some url"); // TODO : create a new node
        super.addObserver(node);
    }

    public void update(){
        super.setChanged();
        super.notifyObservers();
    }

    public void unScale(){
        if(countObservers() == 3)
            return;
        ReadNode node = new ReadNode("some url"); // TODO : get a node to delete
        super.deleteObserver(node);
    }
}
