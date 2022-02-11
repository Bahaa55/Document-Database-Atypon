package com.db.node.BPlusTree;

import java.io.Serializable;
import java.util.List;

class Node<T> implements Serializable {
    protected InternalNode parent;

    protected Node(){}

    int linearNullSearch(Object[] pointers) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == null) {
                return i;
            }
        }
        return -1;
    }
}
