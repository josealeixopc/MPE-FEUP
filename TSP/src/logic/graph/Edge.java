package logic.graph;

public class Edge {

    private Node origin;
    private Node destination;
    private int cost;
    private int date;

    public Edge(Node origin, Node destination, int cost, int date) {
        this.origin = origin;
        this.destination = destination;
        this.cost = cost;
        this.date = date;
    }
}
