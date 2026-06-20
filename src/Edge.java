/**
 * Edge.java
 *
 * Represents a single connection (edge) between two nodes in the graph.
 * Example: an edge with destination = 2 and weight = 5 means
 * "you can travel to node 2, and it costs 5 to get there".
 *
 * This class is intentionally small and simple -- its only job is to
 * hold these two pieces of data together so the rest of the program
 * doesn't have to pass them around separately.
 */
public class Edge {

    // -------- Variables (fields) --------
    // These are marked 'private' so that no other class can directly
    // change them once an Edge is created. This is called
    // "encapsulation" -- a core OOP principle. Other classes must go
    // through the getter methods below to read these values.
    private int destination;
    private int weight;

    // -------- Constructor --------
    // This runs when we create a new Edge object, e.g.:
    //   Edge e = new Edge(2, 5);
    // It sets up the edge's destination node and its weight (cost).
    public Edge(int destination, int weight) {
        this.destination = destination; // 'this.destination' is the field,
        this.weight = weight;           // 'destination' (right side) is the parameter
    }

    // -------- Getter methods --------
    // These let other classes (like DijkstraSolver) safely READ the
    // values without being able to directly modify them. This keeps
    // our edge data safe from accidental changes elsewhere in the code.

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}