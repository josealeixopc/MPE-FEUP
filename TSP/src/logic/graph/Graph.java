package logic.graph;

import java.util.HashMap;

public class Graph {

    private Node startNode;
    private HashMap<String,Node> nodes;

    public Graph(String startNodeName){
        nodes = new HashMap<>();
        startNode = new Node(startNodeName);
        nodes.put(startNodeName,startNode);
    }

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

        Edge edge = new Edge(origin,destination, cost, date);
        origin.addEdge(edge);
        destination.addEdge(edge);
    }
}
