
public class MyHashSet<T> {
    private final int INITIAL_CAPACITY = 1000;
    private MyLinkedList<T>[] hashTable; // Array of HashNodes to represent the hashset

    private int size;

    private int capacity;


    /**
     * No-arg constructor
     */
    MyHashSet(){
        hashTable = new MyLinkedList[INITIAL_CAPACITY];
        for (int i = 0; i < INITIAL_CAPACITY; i++)
            hashTable[i] = new MyLinkedList<>();
        this.size = 0;
        capacity = INITIAL_CAPACITY;

    }


    /**
     * @param value to be hashed
     * @return hash of value
     */
    private int hash(T value){
        int hash = value.hashCode() % capacity;
        if (hash < 0) // If it is negative, make it positive
            return hash + capacity;
        return hash;
    }

    /**
     * @param value Insert the value into the hashset if it is not already in the hashset
     */
    public void insert(T value){
        if ((double)size / capacity >= 0.7)
            rehash();
        int index = hash(value);
        MyLinkedList<T> cluster = hashTable[index];
        if (cluster.insert(value)){
            size++;
        }
    }

    /**
     * (Not used in this implementation)
     * @param value Remove the value from the hashset if it is in the hashset
     * @return True if removal is successful
     */
    public boolean remove(T value){
        int index = hash(value);
        MyLinkedList<T> cluster = hashTable[index];
        if (cluster.delete(value)){
            size--;
            return true;
        }
        return false;
    }

    /**
     * Checks if hashset contains the element
     * @param value value to be checked
     * @return True if hashset contains the element, false otherwise
     */
    public boolean contains(T value){
        int index = hash(value); // Potential index of the value
        MyLinkedList<T> cluster = hashTable[index]; // Potential cluster of the value
        return cluster.contains(value);
    }

    /**
     * Rehashes the hashset by increasing the capacity to next prime greater than or equal to 2*capacity
     */
    private void rehash(){
        MyLinkedList<T>[] oldHashTable = hashTable;
        capacity  = nextPrime(2*capacity); // Theoretically it is better to pick the next prime
        hashTable = new MyLinkedList[capacity]; // New hash table
        for (int i = 0; i < capacity; i++){
            hashTable[i] = new MyLinkedList<>(); // Initialize linked lists for the new table
        }

        for (MyLinkedList<T> row : oldHashTable){
            MyListNode<T> current = row.head;
            while (current != null){
                int index = hash(current.getValue()); // Find the new index of Key
                hashTable[index].insert(current.getValue()); // Directly insert to its corresponding linked list
                current = current.next;
            }
        }

    }

    /**
     * Finds the next prime greater than or equal to a given number
     * @param n Number to find the next prime from
     * @return the next prime greater than or equal to the number n
     */
    private int nextPrime(int n){
        while (!isPrime(n)){
            n++;
        }
        return n;
    }

    /**
     * Finds if a number is prime or not
     * @param n number to find out if prime or not
     * @return True if the number is prime, false otherwise
     */
    private boolean isPrime(int n){
        if (n <= 1)
            return false;
        for (int i = 2; i <= Math.sqrt(n); i++){
            if (n%i == 0)
                return false;
        }
        return true;
    }


}
