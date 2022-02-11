package com.db.node;

import java.util.Observable;

public class LoadBalancer extends Observable {

    public void update(){
        super.setChanged();
        super.notifyObservers();
    }
}
