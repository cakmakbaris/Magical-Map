import java.util.ArrayList;

public class Node {
    private final int x; // X coordinate of the node

    private final int y; // Y coordinate of the node

    private boolean isRevealed; // Stores if the node has been revealed by the traveler or not

    private final int type;


    ArrayList<Pair> connections = new ArrayList<>(); // ArrayList of Pairs to store the connections of the node
    // Each node has at most 4 connections, so using an ArrayList is not considered as inefficient in this case


    /**
     * Constructor
     * @param x X coordinate of the node to be created
     * @param y Y coordinate of the node to be created
     */
    Node(int x, int y, int type){
        this.x = x;
        this.y = y;
        isRevealed = false; // Initially, nodes are not revealed
        this.type = type;
    }


    public void revealNode(){
        isRevealed = true;
    }

    public boolean isRevealed(){
        return isRevealed;
    }


    // Getters
    public int getType(){
        return type;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

}
