package org.library.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyHashTable<K, V> {
    private static class Entry<K, V> {
        K key;
        V value;
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private List<Entry<K, V>>[] table;
    private int size;
    private static final int INIT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    public MyHashTable() {
        table = new List[INIT_CAPACITY];
        size = 0;
    }

    private int hash(K key) {
        return (key == null ? 0 : key.hashCode()) & (table.length - 1);
    }

    public void put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) resize();
        int idx = hash(key);
        if (table[idx] == null) table[idx] = new LinkedList<>();
        for (Entry<K, V> entry : table[idx]) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        table[idx].add(new Entry<>(key, value));
        size++;
    }

    public V get(K key) {
        int idx = hash(key);
        if (table[idx] == null) return null;
        for (Entry<K, V> entry : table[idx]) {
            if (entry.key.equals(key)) return entry.value;
        }
        return null;
    }

    public boolean remove(K key) {
        int idx = hash(key);
        if (table[idx] == null) return false;
        for (Entry<K, V> entry : table[idx]) {
            if (entry.key.equals(key)) {
                table[idx].remove(entry);
                size--;
                return true;
            }
        }
        return false;
    }

    public List<V> values() {
        List<V> vals = new ArrayList<>();
        for (List<Entry<K, V>> bucket : table) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    vals.add(entry.value);
                }
            }
        }
        return vals;
    }

    private void resize() {
        List<Entry<K, V>>[] oldTable = table;
        table = new List[oldTable.length * 2];
        size = 0;
        for (List<Entry<K, V>> bucket : oldTable) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    put(entry.key, entry.value);
                }
            }
        }
    }
} 