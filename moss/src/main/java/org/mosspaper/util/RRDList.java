package org.mosspaper.util;

import java.util.List;
import java.util.LinkedList;

public class RRDList<E> extends LinkedList<E> implements List<E> {

    public RRDList(int maxCapacity) {
        this.mMaxCapacity = maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.mMaxCapacity = maxCapacity;
    }

    @Override
    public boolean add(E o) {
        super.addFirst(o);
        /* Trim the fat */
        for (int i = mMaxCapacity; i < this.size(); i++) {
            this.remove(i);
        }
        return true;
    }

    private int mMaxCapacity;
}
