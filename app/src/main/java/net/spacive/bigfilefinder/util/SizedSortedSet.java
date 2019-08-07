package net.spacive.bigfilefinder.util;

import java.util.Comparator;
import java.util.TreeSet;

// sized and sorted set - the same as TreeSet
// with restricted capacity
public class SizedSortedSet<T> extends TreeSet<T> {

    private final int capacity;

    public SizedSortedSet(int capacity, Comparator<T> comparator) {
        super(comparator);
        this.capacity = capacity;
    }

    @Override
    public boolean add(T t) {
        if (capacity <= 0) {
            return false;
        }

        if (size() < capacity) {
            return super.add(t);
        }

        T smallestElement = first();
        int comparisonResult = comparator().compare(t, smallestElement);

        // replace smallest element if currently added element is greater
        if (comparisonResult > 0) {
            // check duplicities
            if (super.add(t)) {
                remove(smallestElement);
                return true;
            }
            return false;
        }

        return false;
    }
}
