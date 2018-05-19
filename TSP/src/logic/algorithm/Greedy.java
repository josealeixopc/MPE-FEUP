package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Greedy extends Algorithm {

    public Greedy(Graph graph){
        super("Greedy", graph);
    }

    @Override
    public void computeSolution() {
        this.startTimer();

        ArrayList<Node> route = new ArrayList<>();
        Node currentNode = graph.getStartNode();
        route.add(currentNode);

        for(int currentDate=0; currentDate<graph.getNodesAmount()-1; currentDate++){

            if(this.timerEnded()){
                return;
            }

            List<Edge> edges = currentNode.getEdges(currentDate);
            Edge bestEdge = null;
            for(Iterator<Edge> it = edges.iterator(); it.hasNext();){
                Edge edge = it.next();

                if(route.contains(edge.getDestination())) {
                    continue;
                }
                if(bestEdge==null) {
                    bestEdge = edge;
                }

                if(edge.getCost() < bestEdge.getCost()){
                    bestEdge = edge;
                }
            }

            if(bestEdge==null)
                return;

            route.add(bestEdge.getDestination());
            currentNode=bestEdge.getDestination();
        }

        //add return to first
        route.add(graph.getStartNode());

        //if cannot end in start node, return
        int lastDay = graph.getNodesAmount()-1;
        int lastDayCost = route.get(lastDay).getCostToNode(graph.getStartNode(),lastDay);
        if(lastDayCost<0)
            return;

        bestRoute = route;
    }
}
