package com.db.node.BPlusTree;

import java.io.Serializable;
import java.util.*;

public class BPlusTree<T extends Comparable<T>> implements Serializable {
    private int m;
    private InternalNode root;
    private LeafNode firstLeaf;
    private DuplicateHandler duplicateHandler;
    private String schema;
    private static final long serialVersionUID = 5;

    public BPlusTree(int m, String schema) {
        this.m = m;
        this.root = null;
        this.duplicateHandler = new DuplicateHandler<>();
        this.schema = schema;
    }

    private int binarySearch(DictionaryPair<T>[] dps, int numPairs, T t) {

        DictionaryPair<T> target = new DictionaryPair<>(t,0);

        int left = 0, right = numPairs-1;
        while(right >= left){
            int mid = (left+right)/2;
            DictionaryPair<T> current = dps[mid];
            if(current.compareTo(target) == 0){
                return mid;
            }
            else if(current.compareTo(target) == 1)
                right = mid - 1;
            else
                left = mid + 1;
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