package com.db.node.BPlusTree;

import java.io.Serializable;
import java.util.Arrays;

class LeafNode<T> extends Node implements Serializable {
    int maxNumPairs;
    int minNumPairs;
    int numPairs;
    LeafNode<T> leftSibling;
    LeafNode<T> rightSibling;
    BPlusTree.DictionaryPair[] dictionary;

    public void delete(int index) {
        this.dictionary[index] = null;
        numPairs--;
    }

    public boolean insert(BPlusTree.DictionaryPair dp) {
        if (this.isFull()) {
            return false;
        } else {
            this.dictionary[numPairs] = dp;
            numPairs++;
            Arrays.sort(this.dictionary, 0, numPairs);

            return true;
        }
    }

    public boolean isDeficient() {
        return numPairs < minNumPairs;
    }

    public boolean isFull() {
        return numPairs == maxNumPairs;
    }

    public boolean isLendable() {
        return numPairs > minNumPairs;
    }

    public boolean isMergeable() {
        return numPairs == minNumPairs;
    }

    public LeafNode(int m, BPlusTree.DictionaryPair dp) {
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
        this.dictionary = new BPlusTree.DictionaryPair[m];
        this.numPairs = 0;
        this.insert(dp);
    }

    public LeafNode(int m, BPlusTree.DictionaryPair[] dps, InternalNode parent) {
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
        this.dictionary = dps;
        this.numPairs = linearNullSearch(dps);
        this.parent = parent;
    }
}
