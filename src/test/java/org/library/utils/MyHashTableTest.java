package org.library.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class MyHashTableTest {
    @Test
    void testPutAndGet() {
        MyHashTable<String, Integer> table = new MyHashTable<>();
        table.put("a", 1);
        table.put("b", 2);
        assertEquals(1, table.get("a"));
        assertEquals(2, table.get("b"));
        assertNull(table.get("c"));
    }

    @Test
    void testRemove() {
        MyHashTable<String, Integer> table = new MyHashTable<>();
        table.put("a", 1);
        assertTrue(table.remove("a"));
        assertFalse(table.remove("a"));
        assertNull(table.get("a"));
    }

    @Test
    void testValues() {
        MyHashTable<String, Integer> table = new MyHashTable<>();
        table.put("a", 1);
        table.put("b", 2);
        List<Integer> vals = table.values();
        assertTrue(vals.contains(1));
        assertTrue(vals.contains(2));
        assertEquals(2, vals.size());
    }

    @Test
    void testResize() {
        MyHashTable<Integer, Integer> table = new MyHashTable<>();
        for (int i = 0; i < 100; i++) table.put(i, i * 2);
        for (int i = 0; i < 100; i++) assertEquals(i * 2, table.get(i));
    }
} 