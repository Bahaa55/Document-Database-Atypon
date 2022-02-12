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

    public static String getNodeUrl(){
        ReadNode node = nodes.poll();
        nodes.add(node);
        return node.getUrl();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        nodes.add((ReadNode) o);
    }
}
