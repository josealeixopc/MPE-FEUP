package logic.algorithm;

import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;

public class Greedy implements Algorithm {
    @Override
    public ArrayList<Node> computeSolution(Graph graph) {
        ArrayList<Node> path = new ArrayList<>();
        int currentDate = 0;
        /*Node currentNode = graph.getStartNode();
        path.add(currentNode);

        while(path.size() < graph.getNoOfAirports()){
            List<Edge> edges = currentNode.getFutureEdges(currentDay);
            Edge bestEdge = null;
            for(Iterator<Edge> it = edges.iterator(); it.hasNext();){
                Edge edge = it.next();
                if(edge.getCost() < bestEdge.getCost()){
                    bestEdge = edge;
                }
            }
            if(bestEdge==null)
                return null;
            path.add(bestEdge.getDestination());
            date = bestEdge.getDate()+1;

        }*/
    }
}
