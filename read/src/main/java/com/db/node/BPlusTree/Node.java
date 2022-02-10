package com.db.node.BPlusTree;

import java.io.Serializable;

class Node<T> implements Serializable {
    InternalNode parent;

    int linearNullSearch(Object[] pointers) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == null) {
                return i;
            }
        }
        return -1;
    }

//    int linearNullSearch(BPlusTree.DictionaryPair[] dps) {
//        for (int i = 0; i < dps.length; i++) {
//            if (dps[i] == null) {
//                return i;
//            }
//        }
//        return -1;
//    }
}
