package org.moss.util;

import java.util.List;
import java.util.LinkedList;

public class RRDList<E> extends LinkedList<E> implements List<E> {

    public RRDList(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean add(E o) {
        super.addFirst(o);
        /* Trim the fat */
        for (int i = maxCapacity; i < this.size(); i++) {
            this.remove(i);
        }
        return true;
    }

    final int maxCapacity;
}
