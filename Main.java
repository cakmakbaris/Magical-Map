import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        File nodeFile = new File("nodes-500-500.txt"); // File that stores node information
        File edgeFile = new File("edges-500-500.txt"); // File that stores edge information
        File objectiveFile = new File("obj-500-500-withRadius20.txt"); // File that stores objective information
        File outputFile = new File("output.txt"); // File to write the outputs


        BufferedWriter outWriter; // To output in a file we will use BufferedWriter class

        // Preparation for writing to outputFile
        try{
            outWriter =new BufferedWriter(new FileWriter(outputFile));
        }catch (IOException e2){
            e2.printStackTrace();
            return;
        }


        // Preparation for reading the nodeFile
        Scanner reader; // Scanner for the node file
        try{
            reader = new Scanner(nodeFile);
        }catch (FileNotFoundException e){
            System.out.println("Cannot find input file");
            return;
        }


        String line; // Stores each line of the file
        String[] temp; // Stores the splitted version of each line
        line = reader.nextLine();;
        temp = line.split(" ");

        int rowCount = Integer.parseInt(temp[0]); // rowCount of the grid
        int columnCount = Integer.parseInt(temp[1]); // columnCount of the grid


        Node[][] nodes = new Node[rowCount][columnCount]; // 2-D Node array that will store every node

        while (reader.hasNextLine()){
            line = reader.nextLine();
            temp = line.split(" ");
            int x = Integer.parseInt(temp[0]); // X coordinate of the node
            int y = Integer.parseInt(temp[1]); // Y coordinate of the node
            int type = Integer.parseInt(temp[2]); // Type of the node
            nodes[x][y] = new Node(x, y, type);
        }


        // Preparation for reading the edgeFile
        try{
            reader = new Scanner(edgeFile);
        }catch (FileNotFoundException e){
            System.out.println("Cannot find input file");
            return;
        }
        line = "";
        temp = null;

        while (reader.hasNextLine()){
            line = reader.nextLine();
            temp = line.split(" ");
            double weight = Double.parseDouble(temp[1]); // Weight of the edge
            temp = temp[0].split(","); // New temp consisting of x, y coordinates of 2 nodes
            String[] values1 = temp[0].split("-");
            String[] values2 = temp[1].split("-");
            int x1 = Integer.parseInt(values1[0]); // X coordinate of 1st node
            int y1 = Integer.parseInt(values1[1]); // Y coordinate of 1st node
            int x2 = Integer.parseInt(values2[0]); // X coordinate of 2nd node
            int y2 = Integer.parseInt(values2[1]); // Y coordinate of 2nd node
            Node node1 = nodes[x1][y1]; // 1st node
            Node node2 = nodes[x2][y2]; // 2nd node
            node1.connections.add(new Pair(node2, weight)); // Add {node2, weight} to node1's connections
            node2.connections.add(new Pair(node1, weight)); // Add {node1, weight} to node2's connections
        }


        // Preparation for objectiveFile
        try{
            reader = new Scanner(objectiveFile);
        }catch (FileNotFoundException e){
            System.out.println("Cannot find input file");
            return;
        }


        ArrayList<ArrayList<Integer>> objectives = new ArrayList<>(); // ArrayList of ArrayList of Integers to store objectives
        // ArrayList<Integer> represents the x, y coordinates and options if exists
        line = "";
        temp = null;
        line = reader.nextLine(); // First line represents the radiusOfSight
        int radius = Integer.parseInt(line);
        line = reader.nextLine(); // Second line if the starting node
        temp = line.split(" ");
        int startingX = Integer.parseInt(temp[0]); // Initial x coordinate of the traveler
        int startingY = Integer.parseInt(temp[1]); // Initial y coordinate of the traveler
        while (reader.hasNextLine()){
            line = reader.nextLine();
            temp = line.split(" ");
            ArrayList<Integer> arrList = new ArrayList<>(); // Current objectives
            for (int i = 0; i < temp.length; i++){
                arrList.add(Integer.parseInt(temp[i]));
            }
            objectives.add(arrList); // Add to objectives ArrayList
        }


        Traveler traveler = new Traveler(startingX, startingY, radius, nodes); // Traveler starts from the specified starting node

        // Important! Before moving the traveler first call revealNodes method 1 time since initially traveler must see his sight
        traveler.revealNodes(new MyHashSet<>(), new MyHashSet<>(), "all"); // HashSet is empty since there is no path yet

        ArrayList<Integer> options = new ArrayList<>(); // ArrayList to transmit options from previous steps to current step

        MyHashSet<Integer> pickedNumbers = new MyHashSet<>(); // HashSet of numbers that was picked as an option before

        int count = 1;
        for (ArrayList<Integer> currentObjective : objectives){ // While objectives ArrayList is not empty
            Node startNode = nodes[traveler.getX()][traveler.getY()]; // Starting node is the node where traveler stands currently


            ArrayList<Integer> nextOptions = new ArrayList<>(); // Options to transmit to next iteration
            for (int i = 2; i < currentObjective.size(); i++)
                nextOptions.add(currentObjective.get(i));
            int objectiveX = currentObjective.get(0); // X coordinate of the current objective
            int objectiveY = currentObjective.get(1); // Y coordinate of the current objective
            Node objectiveNode = nodes[objectiveX][objectiveY]; // Objective node


            // If there are options from previous steps, i.e, currently you want to pick a number from wizard's offer
            if (!options.isEmpty()){
                double minDistance = Double.MAX_VALUE; // Minimum distance of the various paths found so far, initially infinity
                int bestPick = Integer.MAX_VALUE; // Best pick from the given options, initial value is not important

                // Iterate through options
                for (int candidate : options) {
                    if (pickedNumbers.contains(candidate)) // If candidate was already picked in previous steps, don't pick it, not efficient
                        continue;
                    pickedNumbers.insert(candidate); // Mark candidate as picked (It may not be picked)
                    double currentDistance = dijkstra(startNode, objectiveNode, nodes, new ArrayList<>(), pickedNumbers);
                    pickedNumbers.remove(candidate); // Remove the mark of the candidate
                    if (currentDistance <= minDistance){ // If there is a shorter path, mark this pick as bestPick and change the minDistance of the path
                        minDistance = currentDistance;
                        bestPick = candidate;
                    }
                }
                outWriter.write("Number " + bestPick + " is chosen!\n");
                pickedNumbers.insert(bestPick); // Mark this number as picked, now it will be treated as type 0
            }

            // Continue until objective node is not reached
            while (!(traveler.getX() == objectiveX && traveler.getY() == objectiveY)){
                startNode = nodes[traveler.getX()][traveler.getY()];
                ArrayList<Node> path = new ArrayList<>(); // Path to follow in this iteration
                dijkstra(startNode, objectiveNode, nodes, path, pickedNumbers); // Call dijkstra to find the shortest path form startNode to objectiveNode
                MyHashSet<Node> pathHashSet = new MyHashSet<>();
                for (Node node : path) // Create a hashset from the path
                    pathHashSet.insert(node);


                // Start moving in the path (path is always given in reverse order from dijkstra, so iterate backwards)
                for (int i = path.size()-1; i >=0; i--){
                    String[] arr = new String[1]; // Array to retrieve the corresponding message from Traveler
                    arr[0] = "";
                    boolean moveSuccessful = traveler.moveToNode(path.get(i), pathHashSet, arr, pickedNumbers); // Move to next node and reveal nodes immediately
                    // If there is a node within the path that is unreachable, path is invalid

                    outWriter.write(arr[0] + "\n"); // "Moving to x-y"

                    if (!moveSuccessful) { // Means path is invalid
                        outWriter.write("Path is impassable!\n");
                        break;
                    }
                }
            }
            outWriter.write("Objective " + count + " reached!\n");
            count++;

            options = nextOptions; // Prepare the options for the next iteration

        }

        reader.close();
        outWriter.close();

    }


    /**
     *
     * @param startingNode starting node of the Dijkstra's algorithm
     * @param endNode end node of the Dijkstra's algorithm
     * @param nodes 2-D Nodes array representing every node (4, 3 means node with x=4, y=3)
     * @param path Shortest path that Dijkstra's algorithm will return(It is given as empty initially)
     * @param pickedNumbers HashSet of integers representing numbers picked from wizard's offer(they will be treated as type 0)
     * @return total path distance of the shortest path
     */
    public static double dijkstra(Node startingNode, Node endNode, Node[][] nodes, ArrayList<Node> path, MyHashSet<Integer> pickedNumbers){
        int rowCount = nodes.length;
        int columnCount = nodes[0].length;
        boolean[][] visitedNodes = new boolean[rowCount][columnCount];
        if (endNode == null) // Early terminate condition
            return 0;

        double[][] totalWeights =  new double[rowCount][columnCount]; // 2-D double array that stores the total weight to reach a node
        // Initially, every weight is INFINITY
        for (int i = 0; i < rowCount; i++){
            for (int j = 0; j < columnCount; j++){
                totalWeights[i][j] = Double.MAX_VALUE;
            }
        }
        Node[][] previousNode = new Node[rowCount][columnCount]; // 2-D Node array of previous nodes of every node

        MinHeap<Pair> pq = new MinHeap<>(rowCount*columnCount); // Priority Queue(MinHeap) that will store Pairs {node, weight}
        pq.insert(new Pair(startingNode, 0)); // We can reach starting node with 0 weight
        totalWeights[startingNode.getX()][startingNode.getY()] = 0;
        previousNode[startingNode.getX()][startingNode.getY()] = startingNode;


        while (!pq.isEmpty()){
            Pair p = pq.deleteMin(); // Pair with minimum weight
            Node currentNode = p.getNode(); // Node of the corresponding pair
            if (visitedNodes[currentNode.getX()][currentNode.getY()]) // If this node is already visited, continue
                continue;
            if (currentNode == endNode) // Early terminate, endNode reached
                break;
            double currentWeight = p.getWeight(); // Weight of the corresponding pair
            visitedNodes[currentNode.getX()][currentNode.getY()] = true;
            // Iterate through connections
            for (Pair neighbour : currentNode.connections){
                Node neighbourNode = neighbour.getNode();
                int neighX = neighbourNode.getX(); // X coordinate of the neighbour node
                int neighY = neighbourNode.getY(); // Y coordinate of the neighbour node
                int type = neighbourNode.getType(); // Initial type of the neighbour node

                // If type is 1 or pickednumbers does not contain the node and node is revealed and type>1 or it has been visited before
                if ((type == 1) || (neighbourNode.isRevealed() && type>1 && !pickedNumbers.contains(type)) || (visitedNodes[neighX][neighY]))
                    continue;
                double neighbourWeight = neighbour.getWeight(); // Weight to reach neighbour from current node
                double totalWeight = currentWeight  + neighbourWeight; // Total weight to reach neighbour
                if (totalWeight < totalWeights[neighX][neighY]){ // If we found a smaller weight arrival
                    totalWeights[neighX][neighY] = totalWeight; // Change total weight
                    pq.insert(new Pair(neighbourNode, totalWeight)); // Add node, weight to pq
                    previousNode[neighX][neighY] = currentNode; // Change prev node of neighbour node to current node
                }
            }


        }

        // Construct the path in reverse order
        Node currentNode = endNode;
        while (currentNode != startingNode && currentNode!=null){
            path.add(currentNode);
            currentNode = previousNode[currentNode.getX()][currentNode.getY()];
        }


        return totalWeights[endNode.getX()][endNode.getY()]; // Return the distance of the shortest path
    }

}