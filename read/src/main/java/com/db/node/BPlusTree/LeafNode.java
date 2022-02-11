package com.db.node.BPlusTree;

import java.io.Serializable;
import java.util.Arrays;

class LeafNode<T> extends Node implements Serializable {
    int numPairs;
    BPlusTree.DictionaryPair[] dictionary;

    protected LeafNode(){

    }
}
