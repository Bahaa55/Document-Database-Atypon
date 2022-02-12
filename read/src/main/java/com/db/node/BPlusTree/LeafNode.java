package com.db.node.BPlusTree;

import java.io.Serializable;
import java.util.Arrays;

class LeafNode<T> extends Node implements Serializable {
    protected int numPairs;
    protected BPlusTree.DictionaryPair[] dictionary;
    private static final long serialVersionUID = 5;

    protected LeafNode(){

    }
}
