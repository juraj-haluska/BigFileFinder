package net.spacive.bigfilefinder.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SizedIterable<T> implements Iterable<T> {

    private final List<T> elements;

    private final int capacity;

    Comparator<T> comparator;

    public SizedIterable(int capacity, @NonNull Comparator<T> comparator) {
        this.capacity = capacity;
        this.comparator = comparator;
        this.elements = new ArrayList<>(capacity);
    }

    private int getIndexOfMin() {
        int minPos = 0;

        for (int i = 1; i < elements.size(); i++) {
            if (comparator.compare(elements.get(i), elements.get(minPos)) < 0) {
                minPos = i;
            }
        }

        return minPos;
    }

    public boolean add(T element) {
        if (capacity <= 0) {
            return false;
        }

        if (elements.size() < capacity) {
            return elements.add(element);
        } else {
            // check if currently added element is bigger than current minimums
            int minIndex = getIndexOfMin();

            T minimum = elements.get(minIndex);
            int result = comparator.compare(element, minimum);

            if (result >= 1) {
                // remove the smallest element and add the new one
                elements.set(minIndex, element);
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

    public void sort() {
        Collections.sort(this.elements, comparator);
    }
}

