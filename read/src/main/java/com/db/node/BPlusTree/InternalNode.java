package com.db.node.BPlusTree;

import java.io.Serializable;

class InternalNode<T> extends Node implements Serializable {
    protected int degree;
    protected T[] keys;
    protected Node[] childPointers;
    private static final long serialVersionUID = 5;

    protected InternalNode(){}

}
