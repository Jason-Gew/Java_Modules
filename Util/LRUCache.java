package gew.training;

import java.util.HashMap;
import java.util.Map;

/**
 * Least Recently Used (LRU) cache -> 3 operations: get, put and remove.
 * get(key) -> Get the value of the key if the key exists in the cache, otherwise return null.
 * put(key, value) -> Update value or insert new value if the key is not present.
 * remove(key) -> Remove the value from the cache by the key.
 * When the cache reached its capacity, it should invalidate the least recently used item before inserting a new item.
 */
public class LRUCache<K, V> {

    private final int MAX_CACHE_SIZE;
    private Node<K, V> first;
    private Node<K, V> last;
    class Node<k, v> {
        Node<k, v> pre;
        Node<k, v> next;
        k key;
        v value;
    }
    private Map<K, Node<K, V>> hashMap;

    public LRUCache(int cacheSize) {
        MAX_CACHE_SIZE = cacheSize;
        hashMap = new HashMap<>();
    }

    public void put(K key, V value) {
        Node<K, V> node = hashMap.get(key);
        if (MAX_CACHE_SIZE <= 0) {
            return;
        } else if (node == null) {
            if (hashMap.size() >= MAX_CACHE_SIZE) {
                hashMap.remove(last.key);
                removeLast();
            }
            node = new Node<>();
            node.key = key;
        }
        node.value = value;
        moveToFirst(node);
        hashMap.put(key, node);
    }

    public V get(K key) {
        Node<K, V> node = hashMap.get(key);
        if (node == null) {
            return null;
        }
        moveToFirst(node);
        return node.value;
    }

    public void remove(K key) {
        Node<K, V> node = hashMap.get(key);
        if (node != null) {
            if (node.pre != null) {
                node.pre.next = node.next;
            }
            if (node.next != null) {
                node.next.pre = node.pre;
            }
            if (node == first) {
                first = node.next;
            }
            if (node == last) {
                last = node.pre;
            }
        }
        hashMap.remove(key);
    }

    private void moveToFirst(Node<K, V> node) {
        if (node == first) {
            return;
        }
        if (node.pre != null) {
            node.pre.next = node.next;
        }
        if (node.next != null) {
            node.next.pre = node.pre;
        }
        if (node == last) {
            last = last.pre;
        }
        if (first == null || last == null) {
            first = last = node;
            return;
        }
        node.next = first;
        first.pre = node;
        first = node;
        node.pre = null;
    }

    private void removeLast() {
        if (last != null) {
            last = last.pre;
            if (last == null) {
                first = null;
            } else {
                last.next = null;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        Node<K, V> node = first;
        while (node != null) {
            sb.append(String.format("[%s:%s], ", node.key, node.value));
            node = node.next;
        }
        sb.delete(sb.lastIndexOf(", "), sb.length()).append("}");
        return sb.toString();
    }

    /* Usage Example */
    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "1");
        cache.put(2, "22");
        cache.put(3, "333");
        System.out.println(cache);
        System.out.println("Get Key 2: " + cache.get(2));
        System.out.println(cache);
        cache.put(1, "666666");
        cache.remove(3);
        System.out.println(cache);
    }
}