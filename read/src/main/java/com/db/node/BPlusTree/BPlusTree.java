package com.db.node.BPlusTree;

import java.io.Serializable;
import java.util.*;

public class BPlusTree<T extends Comparable<T>> implements Serializable {
    private int m;
    private InternalNode root;
    private LeafNode firstLeaf;
    private DuplicateHandler duplicateHandler;
    private String schema;

    public BPlusTree(int m, String schema) {
        this.m = m;
        this.root = null;
        this.duplicateHandler = DuplicateHandler.getInstance();
        this.schema = schema;
    }

    private int binarySearch(DictionaryPair<T>[] dps, int numPairs, T t) {

        DictionaryPair<T> target = new DictionaryPair<>(t,0);

        int l = 0, r = numPairs-1;
        while(r>=l){
            int mid = (l+r)/2;
            DictionaryPair<T> temp = dps[mid];
            if(temp.compareTo(target) == 0){
                return mid;
            }
            else if(temp.compareTo(target) == 1)
                r = mid - 1;
            else
                l = mid + 1;
        }
        return -1;
    }

    private LeafNode findLeafNode(T key) {

        T[] keys = (T[]) this.root.keys;
        int i;

        for (i = 0; i < this.root.degree - 1; i++) {
            if (key.compareTo(keys[i]) < 0) {
                break;
            }
        }

        Node child = this.root.childPointers[i];
        if (child instanceof LeafNode) {
            return (LeafNode) child;
        } else {
            return findLeafNode((InternalNode) child, key);
        }
    }

    private LeafNode findLeafNode(InternalNode node, T key) {

        T[] keys = (T[]) node.keys;
        int i;

        for (i = 0; i < node.degree - 1; i++) {
            if (key.compareTo(keys[i]) < 0) {
                break;
            }
        }
        Node childNode = node.childPointers[i];
        if (childNode instanceof LeafNode) {
            return (LeafNode) childNode;
        } else {
            return findLeafNode((InternalNode) node.childPointers[i], key);
        }
    }


    private boolean isEmpty() {
        return firstLeaf == null;
    }


    public List<String> search(T key) {

        if (isEmpty()) {
            return null;
        }

        LeafNode ln = (this.root == null) ? this.firstLeaf : findLeafNode(key);

        DictionaryPair[] dps = ln.dictionary;
        int index = binarySearch(dps, ln.numPairs, key);

        if (index < 0) {
            return null;
        } else {
            List<String> values = duplicateHandler.getValues(this.schema, dps[index].key);
            values.add(new Integer(dps[index].value).toString());
            return values;
        }
    }

    public class DictionaryPair<T extends Comparable<T>> implements Comparable<DictionaryPair<T>>,Serializable {
        T key;
        int value;

        public DictionaryPair(T key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public int compareTo(DictionaryPair<T> o) {
            return key.compareTo(o.key);
        }

    }

}