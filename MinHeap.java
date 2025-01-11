public class MinHeap<T extends Comparable<T>>{

    private T[] array; // Internal array where we store Nodes

    private int size;


    MinHeap(int capacity){
        array = (T[]) new Comparable[capacity];
        size = 0;
    }


    /**
     * Deletes the value at index 1(biggest) and percolates down hole 1 to secure heap property
     * @return the deleted node
     */
    public T deleteMin(){
        if (size == 0)
            return null;
        T minItem = array[1];
        array[1] = array[size--];
        percolateDown(1);
        return minItem;
    }



    /**
     * Given an index, finds its true position by comparing it with indexes below it and locates it there
     * @param hole Index of the value we want to percolate down
     */
    public void percolateDown(int hole){
        int childIndex;
        T initialValue = array[hole]; // The value we want to find true position of

        while (hole*2 <= size){ // Continue if index hole has at least 1 child
            childIndex = hole*2; // Left child of index hole
            if (childIndex != size && array[childIndex+1].compareTo(array[childIndex]) < 0) // If this child is not the last index and hole's right child(we now know it exists) is greater than left child
                childIndex++; // Switch to Right child
            // Here, childIndex is the index of the hole's smallest valued child
            if (array[childIndex].compareTo(initialValue) < 0) // Child is smaller than its parent ---> violation of min heap property
                array[hole] = array[childIndex]; // Move bigger child up
            else // Otherwise, we found the position for initialValue
                break;
            hole = childIndex; // New hole is childIndex since we moved it upwards, now this index is logically empty
        }
        array[hole] = initialValue; // Hole is the true position for initialValue, set it.
        // Note: If initialValue is already in its true position, loop will break in first loop and nothing will change as it can be seen.
    }

    /**
     * Insert into the minHeap
     * @param elem Element to be inserted
     */
    public void insert(T elem){
        if (size == array.length-1) // If array is full, resize it
            resize();

        int hole = ++size; // Set hole to size++
        for (array[0] = elem; elem.compareTo(array[hole/2]) < 0; hole /= 2) // Percolate up
            array[hole] = array[hole/2];
        array[hole] = elem; // Insert the elem to the hole place
    }

    /**
     * Starting from the last non-leaf index, percolate down until index 1 (inclusive)
     */
    private void resize(){
        T[] temp = array;
        array = (T[]) new Comparable[array.length*2];
        System.arraycopy(temp, 0, array, 0, temp.length);
    }

    public boolean isEmpty(){
        return size == 0;
    }


}