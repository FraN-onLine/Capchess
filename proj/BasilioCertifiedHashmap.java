
import java.util.Vector;


public class BasilioCertifiedHashmap<K, V> { 
    //hashmap class with type parameters
    //thanks to the tutorials, bale user specified type on runtime yung ipapasa

    // Node to store key-value pairs
    static class Node<K, V> {
        final K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final int BUCKET = 20; // TWENTY buckets nalang
  
    private Vector<BasilioCertifiedLinkedList<Node<K, V>>> buckets; // Vector of buckets
    private int size; // Number of elements in the HashMap

    public BasilioCertifiedHashmap() {
        buckets = new Vector<>();
        for (int i = 0; i < BUCKET; i++) {
            buckets.add(null);
        }
        size = 0;
    }

    //multiplicative hashing dba
    private int hashfunc(K key) {
     double A = 0.6180339887; // Fractional constant
        return key == null ? 0 : (int) Math.abs(Math.round(BUCKET * ((key.hashCode() * A) % 1))); 
    }

    public void put(K key, V value) {
        int index = hashfunc(key);
        if (buckets.get(index) == null) {
            buckets.set(index, new BasilioCertifiedLinkedList<>()); //it makes use of a LinkedList
        }

        //napa overcomplicated since custom Linkedlists are not iterable daw
        BasilioCertifiedLinkedList<Node<K, V>> bucket = buckets.get(index);
        for (int i = 1; i <= bucket.size(); i++) {
            if (bucket.getatPos(i).key == key) {
                bucket.getatPos(i).value = value; // Update value if key already exists
                return;
            }
        }

        bucket.addatEnd(new Node<>(key, value));
        size++;

    }

    public V get(K key) {
        int index = hashfunc(key);
        if (buckets.get(index) == null) return null;

        BasilioCertifiedLinkedList<Node<K, V>> bucket = buckets.get(index);
        for (int i = 1; i <= bucket.size(); i++) {
            if (bucket.getatPos(i).key == key) {
                return bucket.getatPos(i).value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = hashfunc(key);
        if (buckets.get(index) == null) return false;

        BasilioCertifiedLinkedList<Node<K, V>> bucket = buckets.get(index);
        for (int i = 1; i <= bucket.size(); i++) {
            if (bucket.getatPos(i).key == key) {
                return true;
            }
        }
        return false;
    }

    public V remove(K key) {
        int index = hashfunc(key);
        if (buckets.get(index) == null) return null;

        BasilioCertifiedLinkedList<Node<K, V>> bucket = buckets.get(index);
        for (int i = 1; i <= bucket.size(); i++) {
            if (bucket.getatPos(i).key == key) {
                V value = bucket.getatPos(i).value;
                bucket.removeatPos(i);
                size--;
                return value;
            }
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

}