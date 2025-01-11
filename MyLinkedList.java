public class MyLinkedList<T> {
    public MyListNode<T> head;
    public MyListNode<T> tail;

    int size = 0;


    MyLinkedList(){
        // No-arg constructor
    }

    /**
     *
     * @param value Given a value, insert it into the linked list if it is not already in the linked list
     * @return True if insertion is successful, false otherwise
     */
    public boolean insert(T value){
        if (contains(value)) // Key already exists
            return false;
        MyListNode<T> newNode = new MyListNode<>(value); // Node that we will add
        // Case1 - LL is empty before insertion
        if (head == null){
            // head and tail both point to same node since there is only 1 node
            head = newNode;
            tail = head;
        }
        // Case2 - LL is not empty before insertion
        else{
            tail.next = newNode; // Add node to end
            tail = newNode; // Change the tail to newNode
        }
        size++;
        return true;
    }

    /**
     * Given a value, delete it from the linked list
     * @param value value to be deleted
     * @return True if deletion is successful, false otherwise
     */
    public boolean delete(T value){
        if (head == null) // Empty LL
            return false;

        // Case1 - removing head node
        if (head.getValue().equals(value)){
            head = head.next; // Java garbage collector automatically deletes the non-pointed node
            size--;
            // Case1a - if head becomes null, set tail to null also (before removal there was 1 node)
            if (head == null) {
                tail = null;
            }
            return true;
        }

        // Case2 - removing non-head node
        MyListNode<T> p1 = head;
        MyListNode<T> p2 = head.next;
        while (p2 != null && !p2.getValue().equals(value)){
            p1 = p1.next;
            p2 = p2.next;
        }
        if (p2 == null) // Key not found
            return false;
        p1.next = p1.next.next; // Remove the node in between
        size--;
        if (p2 == tail) // If we are deleting tail node, make p1 the new tail
            tail = p1;

        return true;

    }

    /**
     * Finds if linked list contains the value
     * @param value value to be checked
     * @return True if linked list contains the value, false otherwise
     */
    public boolean contains(T value){
        MyListNode<T> current = head;
        while (current != null){
            if (current.getValue().equals(value))
                return true;
            current = current.next;
        }
        return false;
    }


}
