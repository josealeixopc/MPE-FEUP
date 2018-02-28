package logic.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;
    private List<Edge> edges;

    public Node(String name){
        this.name = name;
        edges = new ArrayList<>();
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
}
