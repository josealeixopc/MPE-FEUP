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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (date != edge.date) return false;
        if (!origin.equals(edge.origin)) return false;
        return destination.equals(edge.destination);
    }

    @Override
    public int hashCode() {
        int result = date;
        result = 31 * result + origin.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + cost;
        return result;
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
