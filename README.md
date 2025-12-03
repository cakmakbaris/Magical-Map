# Dynamic Grid Navigation with Fog of War and Dijkstra

This project implements a **grid-based pathfinding and exploration system** in Java.

A `Traveler` moves on a 2D grid of `Node`s, trying to reach a set of objective locations while:
- only seeing cells within a limited **radius of sight** (fog of war),
- discovering new nodes gradually as they come into view,
- avoiding blocked or picked nodes,
- and using **Dijkstra’s shortest-path algorithm** to decide where to move next.

The program reads the map, edges, and objectives from text files, runs the simulation, and writes a detailed log to `output.txt`.

---

## 📁 Project Structure

- `Main.java`  
  Entry point.  
  - Reads node, edge, and objective files.  
  - Builds the grid of `Node` objects and their connections.  
  - Creates the `Traveler`.  
  - Iterates through all objectives, repeatedly calling Dijkstra to get shortest paths and moving the traveler step by step.  
  - Writes all events and results to an output file.

- `Traveler.java`  
  Represents the agent walking in the map.  
  - Stores current `(x, y)` position and `radiusOfSight`.  
  - Reveals nodes within sight as it moves.  
  - Provides:
    - `revealCurrentNodes(...)` to update visibility,
    - `moveToNode(...)` to attempt a move and return messages about success/failure,
    - helpers like `calculateDistance(...)` and `isInSight(...)`.

- `Node.java`  
  Represents a cell in the grid.  
  - Immutable integer coordinates `x`, `y`.  
  - A `type` indicating node status (e.g., normal, blocked, objective / special types).  
  - A flag `isRevealed` used for fog of war.  
  - An `ArrayList<Pair> connections` with up to 4 neighbors and edge weights.  
  - Methods to get/set type, revealed status, and to add new connections.

- `Pair.java`  
  Simple container for a `Node` and a `double` **weight**.  
  Used as the elements stored in the priority queue (`MinHeap`) during Dijkstra’s algorithm.

- `MinHeap.java`  
  Generic **min-heap** implementation used as a priority queue.  
  Supports:
  - `insert(...)`,
  - `extractMin()`,
  - `isEmpty()`.  
  It orders `Pair` objects by their weight to always expand the currently cheapest node in Dijkstra.

- `MyHashSet.java`  
  Custom hash set implementation using an array of `MyLinkedList<T>` buckets.  
  - Used to efficiently track:
    - which objective numbers have been picked,
    - which nodes belong to a current path (for quick membership tests).  
  Provides:
  - `insert(T value)`,
  - `contains(T value)`,
  - `remove(T value)`.

- `MyLinkedList-2.java`, `MyListNode-2.java`  
  Simple singly linked list and node classes used internally by `MyHashSet`.

---

## 📄 Input Files

The program uses three text files (names are hardcoded in `Main.java`, but you can change them):

1. **Node file** – e.g. `nodes-500-500.txt`  
   Each line:

   ```text
   x y type
   ```

   - `x`, `y` – integer grid coordinates,  
   - `type` – integer representing the kind of cell (e.g. 0 = normal, 1 = blocked, others = special / objectives).

2. **Edge file** – e.g. `edges-500-500.txt`  
   Each line describes a connection between two nodes and its cost:

   ```text
   x1 y1 x2 y2 weight
   ```

   The loader creates bidirectional connections between `(x1,y1)` and `(x2,y2)` with the given weight.

3. **Objective file** – e.g. `obj-500-500-withRadius20.txt`  
   The format is:

   ```text
   <radiusOfSight>
   <startX> <startY>
   <obj1X> <obj1Y> [typeOrNumber]
   <obj2X> <obj2Y> [typeOrNumber]
   ...
   ```

   - First line: integer radius of sight for the traveler.  
   - Second line: starting coordinates of the traveler.  
   - Remaining lines: objectives. Each objective line is stored as an `ArrayList<Integer>`; the first two integers are the objective coordinates, and additional integers (if present) are treated as **options / numbers to be picked** on the way.

---

## 🔁 High-Level Flow

1. **Read grid and edges**
   - `Main` reads the node file and creates a `Node[x][y]` grid.
   - It then reads the edge file and, for each line, adds `Pair` connections between the corresponding nodes with the given weight.

2. **Read objectives and create Traveler**
   - `Main` reads the radius of sight and starting coordinates from the objective file.
   - A `Traveler` is created at `(startX, startY)` on the shared `Node[][]` grid.
   - The list of objectives is stored as `ArrayList<ArrayList<Integer>>`.

3. **Process each objective in order**
   - For each objective:
     - Determine the **objective node** coordinates.
     - If extra numbers are present in that objective line, they represent special node types to be **picked** (e.g., numbered cells).  
       These are stored in a `MyHashSet<Integer>` called `pickedNumbers`.
     - Log which numbers must be picked before reaching the objective.

4. **Candidate selection (when special numbers exist)**
   - If the objective has extra numbers:
     - Build a set of **candidate nodes** (grid cells with any of the required types).
     - For each candidate:
       - Temporarily treat already picked numbers as type 0,
       - Run Dijkstra from the traveler’s current node to the candidate node,
       - Pick the candidate with the **minimum distance**.
     - Move to the best candidate first, logging the choice and picking that number.

5. **Dijkstra-based pathfinding**
   - To move from the current node to a target (candidate or final objective):
     - `Main` calls `dijkstra(startNode, endNode, nodes, path, pickedNumbers, outWriter)`.
     - The algorithm:
       - Uses a 2D `totalWeights[][]` array, initialized to infinity.
       - Uses a 2D `previousNode[][]` array to reconstruct the path.
       - Uses a `MinHeap<Pair>` as the priority queue.
       - Ignores:
         - blocked nodes,
         - unrevealed nodes with disallowed types,
         - nodes whose type corresponds to a number already in `pickedNumbers`.
       - Stops early when the end node is extracted from the heap.
       - Writes the path cost and any special warnings to the output.
       - Reconstructs the path (in reverse) from `endNode` back to `startNode` and fills the `path` list.

6. **Traveler movement along the path**
   - The returned `path` list is traversed **in reverse order** (because it was built backwards).
   - For each step:
     - `Traveler.moveToNode(...)` is called. It:
       - updates the traveler’s coordinates,
       - calls `revealCurrentNodes(...)` to mark nodes within `radiusOfSight` as revealed,
       - may update node types based on picked numbers,
       - returns a message string such as `"Moving to x-y"` or an error if movement is not possible.
     - The message is written to `output.txt`.
     - If at any step the path becomes invalid (e.g. reveals an impassable node ahead), the loop breaks and a new path is computed.

7. **Repeat until objective is reached**
   - The traveler repeatedly recomputes paths and moves until it reaches the current objective node.
   - Then the next objective in the list is processed in the same way.

---

## 🧪 Output

All logs are written to `output.txt`, including:

- which objective is currently being processed,
- which numbers (if any) must be picked,
- which candidate number is chosen as “best” and at what distance,
- each movement step (`"Moving to x-y"`),
- any `"Path is impassable!"` messages when a path is invalid,
- distances returned by Dijkstra for paths to candidates and objectives.

This makes it easy to trace the traveler’s behavior and verify correctness.

---

## 🧰 Requirements

- **Java 8 or higher**
- All `.java` files placed in the same directory:
  - `Main.java`
  - `Traveler.java`
  - `Node.java`
  - `Pair.java`
  - `MinHeap.java`
  - `MyHashSet.java`
  - `MyLinkedList-2.java`
  - `MyListNode-2.java`
- Input files:
  - `nodes-500-500.txt`
  - `edges-500-500.txt`
  - `obj-500-500-withRadius20.txt`  
  (or your own files, with the names updated in `Main`).

---

## ▶️ How to Compile and Run

1. **Compile**

   ```bash
   javac Main.java Traveler.java Node.java Pair.java MinHeap.java MyHashSet.java MyLinkedList-2.java MyListNode-2.java
   ```

2. **Run**

   ```bash
   java Main
   ```

3. **Check the output**

   Open `output.txt` to see:
   - the chosen paths,
   - picked numbers,
   - step-by-step traveler movement,
   - and any pathfinding messages.

---

## 🔧 Customization Tips

- **Change map size or content**  
  Edit `nodes-***.txt` and `edges-***.txt` to define different grids and edge weights.

- **Change objectives / radius**  
  Edit the objective file to change:
  - radius of sight,
  - starting position,
  - objectives and associated numbers.

- **Experiment with visibility rules**  
  You can modify:
  - how `isInSight(...)` is defined,  
  - which node types are allowed / blocked in Dijkstra,  
  to simulate different game or robotics scenarios.

---

## 📚 Learning Outcomes

This project is a good reference if you want to learn:

- How to implement **Dijkstra’s algorithm** on a grid with obstacles and dynamic constraints,
- How to represent graphs with adjacency lists in Java (`Node` + `Pair`),
- How to build your own **min heap** and **hash set**,
- How to simulate **fog of war** and local visibility on a grid,
- How to structure a multi-file Java project with clean separation of concerns.
