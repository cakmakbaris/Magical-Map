public class MyListNode<T> {
    public MyListNode<T> next;
    private T value;

    /**
     * @param value Value to be stored inside the node
     */
    MyListNode(T value){
        this.value = value;
    }


    public T getValue(){
        return value;
    }

}
