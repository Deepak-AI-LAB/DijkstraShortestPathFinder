import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    int numVertices;
    List<List<Edge>> adjacencyList;
    private List<int[]>      edgeRecords;
    private List<String>     nodeNames;
    private Map<String, Integer> nameToIndex;

    /** Empty graph — nodes are added dynamically via addNode(). */
    public Graph() {
        adjacencyList = new ArrayList<>();
        edgeRecords   = new ArrayList<>();
        nodeNames     = new ArrayList<>();
        nameToIndex   = new LinkedHashMap<>();
        numVertices   = 0;
    }

    /** Backward-compat constructor: creates n nodes named "0".."n-1". */
    public Graph(int n) {
        this();
        for (int i = 0; i < n; i++) {
            try { addNode(String.valueOf(i)); }
            catch (GraphException e) { throw new RuntimeException(e); }
        }
    }

    /** Adds a named node and returns its internal integer index. */
    public int addNode(String name) throws GraphException {
        name = name.trim();
        if (name.isEmpty())
            throw new GraphException("City name cannot be empty.");
        if (nameToIndex.containsKey(name))
            throw new GraphException("City \"" + name + "\" already exists in the graph.");
        int index = numVertices;
        nodeNames.add(name);
        nameToIndex.put(name, index);
        adjacencyList.add(new ArrayList<>());
        numVertices++;
        return index;
    }

    /** Returns the display name for a node index, or the index as a string if unnamed. */
    public String getNodeName(int index) {
        if (index >= 0 && index < nodeNames.size()) return nodeNames.get(index);
        return String.valueOf(index);
    }

    /** Resolves a city name to its internal index. */
    public int getNodeIndex(String name) throws GraphException {
        Integer idx = nameToIndex.get(name.trim());
        if (idx == null)
            throw new GraphException("City \"" + name + "\" not found in the graph.");
        return idx;
    }

    public boolean hasNode(String name) {
        return nameToIndex.containsKey(name.trim());
    }

    /** Returns an unmodifiable view of all city names in insertion order. */
    public List<String> getNodeNames() {
        return Collections.unmodifiableList(nodeNames);
    }

    /** Wipes all nodes and edges so the graph can be rebuilt in place. */
    public void clear() {
        numVertices = 0;
        adjacencyList.clear();
        edgeRecords.clear();
        nodeNames.clear();
        nameToIndex.clear();
    }

    /** Adds an undirected edge by integer index. Used internally and by DijkstraSolver. */
    public void addEdge(int source, int destination, int weight) throws GraphException {
        if (source < 0 || source >= numVertices || destination < 0 || destination >= numVertices)
            throw new GraphException("Invalid route (" + getNodeName(source) + " → " +
                getNodeName(destination) + "). Both cities must already exist in the graph.");
        if (weight < 0)
            throw new GraphException("Distance cannot be negative (" + weight + " km).");
        adjacencyList.get(source).add(new Edge(destination, weight));
        adjacencyList.get(destination).add(new Edge(source, weight));
        edgeRecords.add(new int[]{source, destination, weight});
    }

    /** Adds an undirected edge by city name — the primary method for the GUI. */
    public void addEdge(String fromCity, String toCity, int distanceKm) throws GraphException {
        addEdge(getNodeIndex(fromCity), getNodeIndex(toCity), distanceKm);
    }

    public List<int[]> getEdgeRecords() { return edgeRecords; }
}