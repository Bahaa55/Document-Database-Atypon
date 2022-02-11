package com.db.node.Services;

import com.db.node.ReadNode;
import java.util.*;

public class LoadBalancer extends Observable {
    private static LoadBalancer instance = new LoadBalancer();
    private static Queue<ReadNode> nodes;

    public static LoadBalancer getInstance(){return instance;}

    private LoadBalancer(){
        nodes = new LinkedList<>();
    }

    public void update(){
        super.setChanged();
        super.notifyObservers();
    }

    public static String getNode(){
        ReadNode node = nodes.poll();
        nodes.add(node);
        return node.getNodeUrl();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        nodes.add((ReadNode) o);
        super.addObserver(o);
    }
}
