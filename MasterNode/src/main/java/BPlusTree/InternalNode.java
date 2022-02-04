package BPlusTree;

import java.io.Serializable;

class InternalNode<T> extends Node implements Serializable {
    int maxDegree;
    int minDegree;
    int degree;
    InternalNode leftSibling;
    InternalNode rightSibling;
    T[] keys;
    Node[] childPointers;

    public void appendChildPointer(Node pointer) {
        this.childPointers[degree] = pointer;
        this.degree++;
    }

    public int findIndexOfPointer(Node pointer) {
        for (int i = 0; i < childPointers.length; i++) {
            if (childPointers[i].equals(pointer)) {
                return i;
            }
        }
        return -1;
    }

    public void insertChildPointer(Node pointer, int index) {
        for (int i = degree - 1; i >= index; i--) {
            childPointers[i + 1] = childPointers[i];
        }
        this.childPointers[index] = pointer;
        this.degree++;
    }

    public boolean isDeficient() {
        return this.degree < this.minDegree;
    }

    public boolean isLendable() {
        return this.degree > this.minDegree;
    }

    public boolean isMergeable() {
        return this.degree == this.minDegree;
    }

    public boolean isOverfull() {
        return this.degree == maxDegree + 1;
    }

    public void prependChildPointer(Node pointer) {
        for (int i = degree - 1; i >= 0; i--) {
            childPointers[i + 1] = childPointers[i];
        }
        this.childPointers[0] = pointer;
        this.degree++;
    }

    public void removeKey(int index) {
        this.keys[index] = null;
    }

    public void removePointer(int index) {
        this.childPointers[index] = null;
        this.degree--;
    }

    public void removePointer(Node pointer) {
        for (int i = 0; i < childPointers.length; i++) {
            if (childPointers[i] == pointer) {
                this.childPointers[i] = null;
            }
        }
        this.degree--;
    }

    public InternalNode(int m, T[] keys) {
        this.maxDegree = m;
        this.minDegree = (int) Math.ceil(m / 2.0);
        this.degree = 0;
        this.keys = keys;
        this.childPointers = new Node[this.maxDegree + 1];
    }

    public InternalNode(int m, T[] keys, Node[] pointers) {
        this.maxDegree = m;
        this.minDegree = (int) Math.ceil(m / 2.0);
        this.degree = linearNullSearch(pointers);
        this.keys = keys;
        this.childPointers = pointers;
    }
}
