package net.spacive.bigfilefinder.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SizedSortedIterable<T> implements Iterable<T> {

    private final List<T> elements;

    private final int capacity;

    Comparator<T> comparator;

    public SizedSortedIterable(int capacity, @NonNull Comparator<T> comparator) {
        this.capacity = capacity;
        this.comparator = comparator;
        this.elements = new ArrayList<>(capacity);
    }

    public boolean add(T element) {
        if (capacity <= 0) {
            return false;
        }

        if (elements.size() < capacity) {
            if (elements.add(element)) {
                Collections.sort(elements, comparator);
                return true;
            }

            return false;
        } else {
            // check if currently added element is bigger than current minimum
            T minimum = elements.get(0);

            int result = comparator.compare(element, minimum);

            if (result >= 1) {
                // remove the smallest element (the first one) and add the new one
                elements.remove(0);
                elements.add(element);
                Collections.sort(elements, comparator);
                return true;
            } else {
                // elements are the same, or new one is smaller
                return false;
            }
        }
    }

    @Override
    @NonNull
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int position = 0;

            @Override
            public boolean hasNext() {
                return position < elements.size();
            }

            @Override
            public T next() {
                return elements.get(position++);
            }
        };
    }

    @NonNull
    public Iterator<T> descendingIterator() {
        return new Iterator<T>() {

            private int position = elements.size() - 1;

            @Override
            public boolean hasNext() {
                return position >= 0;
            }

            @Override
            public T next() {
                return elements.get(position--);
            }
        };
    }
}
