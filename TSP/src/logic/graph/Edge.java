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
     * @param date date on which the edge can be traveled.
     * @param cost Cost of the edge (price of flight).
     */
    Edge(Node origin, Node destination, int date, int cost) {
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.cost = cost;
    }
    
    public int getDate() {
        return date;
    }

    public Node getDestination() {
        return destination;
    }

    public int getCost() {
        return cost;
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
