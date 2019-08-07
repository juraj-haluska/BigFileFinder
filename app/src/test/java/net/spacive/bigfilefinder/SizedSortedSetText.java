package net.spacive.bigfilefinder;

import net.spacive.bigfilefinder.util.SizedSortedSet;

import org.junit.Test;

import static org.junit.Assert.*;

public class SizedSortedSetText {
    @Test
    public void test() {

        SizedSortedSet<Integer> set = new SizedSortedSet<>(5, Integer::compareTo);

        assertTrue(set.add(-5));
        assertTrue(set.add(-4));
        assertTrue(set.add(-3));
        assertTrue(set.add(-2));
        assertTrue(set.add(-1));
        assertTrue(set.add(0));
        assertFalse(set.add(-2));

        assertEquals((int) set.last(), 0);
        assertEquals((int) set.first(), -4);

        int shouldBe = -4;
        for(Integer i: set) {
            assertEquals((int) i, shouldBe);
            shouldBe++;
        }

        assertFalse(set.add(-100));
        assertTrue(set.add(100));

        assertEquals(set.size(), 5);
    }
}
