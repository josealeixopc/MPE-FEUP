package logic.graph;

public class Edge {

    private int date;
    private Node origin;
    private Node destination;
    private int cost;

    /**
     * Creates a new edge for the graph.
     * @param origin Node (airport) of origin.
     * @param destination Node (airport) of destination.
     * @param cost Cost of the edge (price of flight).
     */
    Edge(Node origin, Node destination, int date, int cost) {
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "origin=" + origin.getName() +
                ", destination=" + destination.getName() +
                ", date=" + date +
                ", cost=" + cost +
                '}';
    }
}
