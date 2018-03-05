package logic.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Graph {

    private Node startNode;
    private HashMap<String,Node> nodes;

    /**
     * Create a new graph with an initial node.
     * @param startNodeName The name (3-letter code) of the trip origin Node (airport).
     */
    public Graph(String startNodeName){
        nodes = new HashMap<>();
        startNode = new Node(startNodeName);
        nodes.put(startNodeName,startNode);
    }

    /**
     * Adds a new edge to the graph and its respective origin and destination Nodes (airport). If the graph does not contain the nodes
     * they will be created.
     * @param originName Name of the edge's origin Node (airport)
     * @param destinationName Name of the edge's destination Node (airport)
     * @param date The day in which this edge (connection) is available. An integer between 0 and n (n == number of airports).
     * @param cost The cost of the edge (flight's price).
     */
    public void addEdge(String originName, String destinationName, int date, int cost){
        Node origin = nodes.get(originName);
        Node destination = nodes.get(destinationName);

        if(origin==null) {
            origin = new Node(originName);
            nodes.put(originName,origin);
        }
        if(destination==null) {
            destination = new Node(destinationName);
            nodes.put(destinationName, destination);
        }

        Edge edge = new Edge(origin,destination, cost);
        origin.addEdge(date, edge);
        destination.addEdge(date, edge);
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

            if(it.hasNext()){
                sb.append(",");
            }
            else{
                sb.append("]");
            }
        }

        sb.append("]\n");

        return sb.toString();
    }
}
