package logic.graph;

import java.util.*;

public class Node {

    private String name;
    private Map<Integer, List<Edge>> edges;

    /**
     * Creates a new node for an airport, initializing the HashMap for its edges.
     * @param name The 3-letter code for the airport.
     */
    Node(String name){
        this.name = name;
        this.edges = new HashMap<>();
    }

    /**
     * @return The name of the node (3-letter code representing the airport name).
     */
    public String getName(){
        return this.name;
    }

    /**
     *
     * @param day The day for which we want the available connections from the airport.
     * @return A list containing all the Edges available for the given day.
     */
    public List<Edge> getEdges(int day){
        return this.edges.get(day);
    }

    /**
     * Adds a new Edge to the Node (airport) associated with a given day.
     * @param day Day for the connection.
     * @param edge Edge containing destination and cost.
     */
    public void addEdge(int day, Edge edge) {

        List<Edge> currentEdges = this.edges.computeIfAbsent(day, k -> new ArrayList<>());

        currentEdges.add(edge);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Node[");

        sb.append("Name: ").append(this.name).append(",");

        Iterator<Map.Entry<Integer, List<Edge>>> it = this.edges.entrySet().iterator();

        sb.append("Edges[");
        while (it.hasNext()) {
            Map.Entry<Integer, List<Edge>> pair = it.next();

            sb.append("Day: ").append(pair.getKey()).append(",");

            Iterator<Edge> itEdge = pair.getValue().iterator();

            sb.append("Edges[");
            while(itEdge.hasNext()){

                sb.append(itEdge.next().toString());

                if(itEdge.hasNext()){
                    sb.append(",");
                }
                else{
                    sb.append("]");
                }
            }

            sb.append("]");

            if(it.hasNext()){
                sb.append(",");
            }
            else{
                sb.append("]");
            }
        }

        sb.append("]");

        return sb.toString();
    }
}
