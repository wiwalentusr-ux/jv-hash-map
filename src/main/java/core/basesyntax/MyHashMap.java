package core.basesyntax;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int SCALE_SIZE_FACTOR = 2;

    private int size;
    private int capacity;
    private final double loadFactor;
    private Node<K,V>[] nodeArray;
    private int threshold;

    public MyHashMap() {
        capacity = DEFAULT_CAPACITY;
        loadFactor = DEFAULT_LOAD_FACTOR;
        nodeArray = (Node<K,V>[]) new Node[capacity];
        threshold = (int) (capacity * loadFactor);
    }

    public MyHashMap(int capacity) {
        loadFactor = DEFAULT_LOAD_FACTOR;
        int currentCapacity = DEFAULT_CAPACITY;
        while (currentCapacity * loadFactor < capacity) {
            currentCapacity *= SCALE_SIZE_FACTOR;
        }
        this.capacity = currentCapacity;
        nodeArray = (Node<K,V>[]) new Node[this.capacity];
        threshold = (int) (this.capacity * loadFactor);
    }
    
    @Override
    public void put(K key, V value) {
        int hash = getHashKey(key);
        Node<K,V> currentNode = getNode(key, hash);
        if (currentNode == null) {
            if (size + 1 > threshold) {
                resize();
            }
            createNewNode(hash, key, value);
        } else {
            currentNode.value = value;
        }
    }

    @Override
    public V getValue(K key) {
        int hash = getHashKey(key);
        Node<K,V> currentNode = getNode(key, hash);
        return currentNode == null ? null : currentNode.value;
    }

    @Override
    public int getSize() {
        return size;
    }

    private int getHashKey(K key) {
        return key == null ? 0 : key.hashCode();
    }

    private int getPosition(int hash) {
        return (hash & 0x7FFFFFFF) % capacity;
    }

    private Node<K,V> getNode(K key, int hash) {
        int position = getPosition(hash);
        Node<K,V> currentNode = nodeArray[position];
        while (currentNode != null) {
            K currentNodeKey = currentNode.key;
            if (currentNodeKey == key
                    || currentNodeKey != null && currentNodeKey.equals(key)) {
                break;
            }
            currentNode = currentNode.next;
        }
        return currentNode;
    }

    private void createNewNode(int hash, K key, V value) {
        Node<K,V> newNode = new Node<>(hash, key, value);
        int position = getPosition(hash);
        newNode.next = nodeArray[position];
        nodeArray[position] = newNode;
        size++;
    }

    private void resize() {
        capacity *= SCALE_SIZE_FACTOR;
        Node<K,V>[] oldNodeArray = nodeArray;
        nodeArray = (Node<K,V>[]) new Node[capacity];
        for (Node<K,V> node : oldNodeArray) {
            Node<K,V> oldCurrentNode = node;
            while (oldCurrentNode != null) {
                int position = getPosition(oldCurrentNode.hash);
                Node<K,V> currentNode = oldCurrentNode;
                oldCurrentNode = oldCurrentNode.next;
                currentNode.next = nodeArray[position];
                nodeArray[position] = currentNode;
            }
        }
        threshold = (int) (capacity * loadFactor);
    }

    private class Node<K,V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K,V> next;

        private Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }
    }
}
