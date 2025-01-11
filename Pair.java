/**
 * This class holds a Node with a weight. We use this class in Dijkstra's algorithm
 * {node, weight} pair represents {node, potential smallest time to arrive at Node node}
 */
public class Pair implements Comparable<Pair>{
    private Node node; // Node of the pair
    private double weight; // Weight to reach node (In this Project, weights are seconds)

    /**
     * Constructor
     * @param node Node of the pair
     * @param weight Current weight to reach the node
     */
    Pair(Node node, double weight){
        this.node = node;
        this.weight = weight;
    }

    /**
     * Compares pairs according to their weights, smaller weight comes first
     * @param o the Pair to be compared.
     * @return corresponding integer of comparison
     */
    @Override
    public int compareTo(Pair o) {
        if (o.weight < this.weight)
            return 1;
        else if (o.weight > this.weight)
            return -1;
        else
            return 0;
    }


    // Getters
    public Node getNode(){
        return node;
    }

    public double getWeight(){
        return weight;
    }
}
