package logic.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Graph {

    private Node startNode;
    private HashMap<String, Node> nodes;

    /**
     * Create a new graph with an initial node.
     *
     * @param startNodeName The name (3-letter code) of the trip origin Node (airport).
     */
    public Graph(String startNodeName) {
        nodes = new HashMap<>();
        startNode = new Node(startNodeName);
        nodes.put(startNodeName, startNode);
    }

    /**
     * Adds a new edge to the graph and its respective origin and destination Nodes (airport). If the graph does not contain the nodes
     * they will be created.
     *
     * @param originName      Name of the edge's origin Node (airport)
     * @param destinationName Name of the edge's destination Node (airport)
     * @param date            The day in which this edge (connection) is available. An integer between 0 and n (n == number of airports).
     * @param cost            The cost of the edge (flight's price).
     */
    public void addEdge(String originName, String destinationName, int date, int cost) {
        Node origin = nodes.get(originName);
        Node destination = nodes.get(destinationName);

        if (origin == null) {
            origin = new Node(originName);
            nodes.put(originName, origin);
        }
        if (destination == null) {
            destination = new Node(destinationName);
            nodes.put(destinationName, destination);
        }

        Edge edge = new Edge(origin, destination, date, cost);
        origin.addEdge(date, edge);
    }

    /**
     * Getter for the starting node.
     *
     * @return the node (airport) where the algorithm starts.
     */
    public Node getStartNode() {
        return startNode;
    }

    /**
     * Gets the total number of nodes available.
     *
     * @return amount of nodes airports in existance.
     */
    public int getNodesAmount() {
        return nodes.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Graph[\n");
        sb.append("Start node: ").append(startNode.toString()).append(",");

        Iterator<Map.Entry<String, Node>> it = this.nodes.entrySet().iterator();

        sb.append("Nodes[");
        while (it.hasNext()) {
            Map.Entry<String, Node> pair = it.next();

            sb.append(pair.getValue().toString());

            if (it.hasNext()) {
                sb.append(",");
            } else {
                sb.append("]");
            }
        }

        sb.append("]\n");

        return sb.toString();
    }

    /**
     * Takes a route and calculates the cost of that route according to this Graph.
     * @param route The ordered list of nodes constituting the list.
     * @return The cost of the route. -1 if the route is null, invalid or does not have the correct number of nodes.
     */
    public int getRouteCost(ArrayList<Node> route) {
        if (route == null || route.size() - 1 != this.getNodesAmount())
            return -1;

        int cost = 0;
        for (int i = 0; i + 1 < route.size(); i++) {

            // if any of the connections is not valid.
            if(route.get(i).getCostToNode(route.get(i + 1), i) == -1){
                return -1;
            }

            cost += route.get(i).getCostToNode(route.get(i + 1), i);
        }

        return cost;
    }
}
