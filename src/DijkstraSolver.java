import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * DijkstraSolver.java
 *
 * Contains the actual Dijkstra's Algorithm logic.
 * This class doesn't know or care how the graph was built or how
 * results will be displayed -- it just solves the shortest path problem.
 * Keeping it separate like this makes it reusable (e.g. later from a GUI).
 *
 * Time complexity: O((V + E) log V) thanks to the priority queue,
 * where V = number of vertices and E = number of edges.
 */
public class DijkstraSolver {

    /**
     * Finds the shortest distance from 'source' to every other node.
     *
     * @param graph    the graph to search (built using Graph.java + Edge.java)
     * @param source   the starting node
     * @param previous an array that will be filled in so we can trace
     *                 back the actual path later (previous[v] = the node
     *                 we came from to reach v on the shortest path)
     * @return an array where distance[i] = shortest distance from source to node i
     * @throws GraphException if the source vertex is out of range
     */
    public static int[] findShortestDistances(Graph graph, int source, int[] previous) throws GraphException {
        int n = graph.numVertices;

        // Fail fast with a clear, custom error message if the source is
        // invalid, rather than letting an ArrayIndexOutOfBoundsException
        // crash the program later with a confusing message.
        if (source < 0 || source >= n) {
            throw new GraphException("Source vertex " + source + " is out of range (0 to " + (n - 1) + ").");
        }

        // ---- STEP 1: Initialize distances ----
        // We don't know the real shortest distance to anything yet, so we
        // treat every node as "unreachable for now" using Integer.MAX_VALUE
        // as a stand-in for infinity. The only exception is the source
        // itself -- it costs 0 to reach the place you're already standing at.
        int[] distance = new int[n];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source] = 0;

        // visited[i] = true means "we've already found the FINAL shortest
        // distance for node i, don't bother reprocessing it."
        boolean[] visited = new boolean[n];

        // ---- STEP 2: Set up the priority queue ----
        // Each entry in the queue is a tiny array: {node, distanceSoFar}.
        // We use a custom comparator so the queue always hands us the
        // entry with the SMALLEST distance first, not just whatever was
        // added first. This "always explore the cheapest option next"
        // behavior is the heart of Dijkstra's greedy strategy.
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.add(new int[]{source, 0}); // start by exploring from the source

        // ---- STEP 3: Process nodes until the queue is empty ----
        while (!pq.isEmpty()) {

            // Take out the unvisited node that is currently CLOSEST to source
            int[] current = pq.poll();
            int currentNode = current[0];

            // Important detail: the same node can be added to the queue
            // multiple times (once for each time we find a cheaper way to
            // reach it). Once we've processed a node for real, any older
            // "stale" copy of it still sitting in the queue is junk -- we
            // just skip it instead of trying to remove it (this trick is
            // called "lazy deletion" and keeps the code simple).
            if (visited[currentNode]) continue;
            visited[currentNode] = true;

            // ---- STEP 4: Relaxation ----
            // "Relaxing an edge" means: check if going through the current
            // node gives a CHEAPER path to its neighbor than what we knew
            // before. If yes, update our records.
            for (Edge edge : graph.adjacencyList.get(currentNode)) {
                int neighbor = edge.getDestination();
                int newDist = distance[currentNode] + edge.getWeight();

                if (newDist < distance[neighbor]) {
                    distance[neighbor] = newDist;       // found a cheaper path
                    previous[neighbor] = currentNode;    // remember how we got here
                    pq.add(new int[]{neighbor, newDist}); // re-queue with updated priority
                }
            }
        }

        // Once the queue is empty, every reachable node has its true
        // shortest distance locked in.
        return distance;
    }

    /**
     * Rebuilds the actual path to 'destination' by walking backwards
     * through the 'previous' array until we reach the source (-1).
     *
     * Think of 'previous' as breadcrumbs: previous[5] = 2 means
     * "the shortest path reaches node 5 by coming from node 2".
     * We follow these breadcrumbs backward, then reverse the list
     * because we built it back-to-front.
     */
    public static List<Integer> getPath(int[] previous, int destination) {
        List<Integer> path = new ArrayList<>();
        for (int at = destination; at != -1; at = previous[at]) {
            path.add(at);
        }
        Collections.reverse(path); // it's backwards, so flip it
        return path;
    }
}