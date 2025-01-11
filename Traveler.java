
/**
 * This class represents the traveler who walks in the map through the process
 */
public class Traveler {
    private final int radiusOfSight; // Radius of the circle that the traveler can see
    // (It is helpful at this point to think of a traveler with a torch in his hand)

    private final int rowCount; // Row count of the grid
    private final int columnCount; // Column count of the grid

    private final Node[][] nodes; // 2-D node array, it will be passed to this class from Main via constructor

    private int x; // Current X coordinate of the traveler
    private int y; // Current Y coordinate of the traveler


    /**
     * @param initialX      Initial X position of the traveler
     * @param initialY      Initial Y position of the traveler
     * @param radiusOfSight Sight restriction of the traveler
     * @param nodes         2-D Node array that stores every node
     */
    Traveler(int initialX, int initialY, int radiusOfSight, Node[][] nodes) {
        this.x = initialX;
        this.y = initialY;
        this.radiusOfSight = radiusOfSight;
        this.rowCount = nodes.length;
        this.columnCount = nodes[0].length;
        this.nodes = nodes;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Moves the traveler to the specified node (traveler never moves to a node with jumps, it is next to him)
     *
     * @param targetNode     Node the traveler is moving to
     * @param path    Current path we are moving on
     * @param arr            Used a string array to transfer a message from this class to Main since the files are in Main
     * @return If after the move, we encountered an unreachable node that is in our path, return false, true otherwise
     */
    public boolean moveToNode(Node targetNode, MyHashSet<Node> path, String[] arr, MyHashSet<Integer> pickedNumbers) {
        int targetX = targetNode.getX();
        int targetY = targetNode.getY();
        int moveX = targetX - x;
        int moveY = targetY - y;
        x = targetX;
        y = targetY;
        arr[0] = "Moving to " + x + "-" + y;
        if (moveX == 1 && moveY == 0)
            return revealNodes(path, pickedNumbers, "R");
        else if (moveX == -1 && moveY == 0)
            return revealNodes(path, pickedNumbers, "L");
        else if (moveX == 0 && moveY == 1)
            return revealNodes(path, pickedNumbers, "U");
        else if (moveX == 0 && moveY == -1)
            return revealNodes(path, pickedNumbers, "D");

        return true; // Code never reaches here since traveler can not move more than 1 squares in each move
    }

    /**
     * Creates an imaginary square of edges 2*radiusOfSight where the traveler is in the center of the square
     * If the imaginary square goes beyond the borders, cuts the infeasible parts and turns the square into a potential rectangle
     * @param path hash set of the nodes in the path currently the traveler is walking on
     * @return True if there aren't any problems throughout the seen part of the path, False otherwise
     */
    public boolean revealNodes(MyHashSet<Node> path, MyHashSet<Integer> pickedNumbers, String option) {
        boolean isPathValid = true; // Initially we treat the path like it is valid

        // Set coefficients to set up the grid efficiently
        int coefficientR = radiusOfSight;
        int coefficientL = radiusOfSight;
        int coefficientU = radiusOfSight;
        int coefficientD = radiusOfSight;

        // For different options of move types, set coefficients
        // Ex:If traveler is moving to right, no need to check left vica versa
        if (option.equals("R"))
            coefficientL = 0;
        if (option.equals("L"))
            coefficientR = 0;
        if (option.equals("U"))
            coefficientD = 0;
        if (option.equals("D"))
            coefficientU = 0;

        // Construct the walls to be checked
        int leftWall = x - coefficientL;
        int rightWall = x + coefficientR;
        int upperWall = y + coefficientU;
        int lowerWall = y - coefficientD;

        // Check their validity, if they are out of bonds, set them to their extreme values
        if (leftWall < 0) // If left wall is out of bounds, set it to the leftmost index possible
            leftWall = 0;
        if (rightWall >= rowCount) // If right wall is out of bounds, set it to rightmost index possible
            rightWall = rowCount - 1;
        if (upperWall >= columnCount) // If upper wall is out of bounds, set it to uppermost index possible
            upperWall = columnCount - 1;
        if (lowerWall < 0) // If lower wall is out of bounds, set it to the lowermost index possible
            lowerWall = 0;

        // Traverse the rectangle
        for (int i = leftWall; i <= rightWall; i++) {
            for (int j = lowerWall; j <= upperWall; j++) {
                int type = nodes[i][j].getType();
                // If current index is in the sight of traveler
                if (type <= 1)
                    continue;
                if (isInSight(i, j)) {
                    nodes[i][j].revealNode();
                    if (!pickedNumbers.contains(type) && path.contains(nodes[i][j])) { // If both node's type is greater than 1 and it is in our path
                        // This means we have a problem in the path
                        isPathValid = false; // Path is invalid
                    }
                }
            }
        }

        return isPathValid;


    }

    /**
     * Calculates distance between 2 nodes
     * @param x1 x coordinate of 1st node
     * @param y1 y coordinate of 1st node
     * @param x2 x coordinate of 2nd node
     * @param y2 y coordinate of 2nd node
     * @return the distance between this 2 nodes
     */
    public double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * @param x1 x coordinate of the node
     * @param y1 y coordinat of the node
     * @return True if this node is in sight of the traveler
     */
    public boolean isInSight(int x1, int y1) {
        return calculateDistance(x, y, x1, y1) <= (double) radiusOfSight;
    }

}