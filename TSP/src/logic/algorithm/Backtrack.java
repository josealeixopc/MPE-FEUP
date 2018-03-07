package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;
import java.util.Iterator;

public class Backtrack extends Algorithm {
    private int bestCost;

    public Backtrack(Graph graph){
        super(graph);
        bestCost = Integer.MAX_VALUE;
    }

    @Override
    public void computeSolution() {
        ArrayList<Node> currentRoute = new ArrayList<>();
        currentRoute.add(graph.getStartNode());
        backtrackDFS(currentRoute,0,0,graph.getStartNode());
    }

    private void backtrackDFS(ArrayList<Node> route, int day, int cost, Node node){
        if(cost >= bestCost) //if route already costs more, return
            return;

        if(day == graph.getNodesAmount()-1){
            int lastDayCost = route.get(day).getCostToNode(graph.getStartNode(),day);
            if(lastDayCost<0 || cost+lastDayCost>=bestCost)
                return;

            bestRoute = new ArrayList<>();
            for(int i=0; i<route.size(); i++){
                bestRoute.add(route.get(i));
            }
            bestRoute.add(graph.getStartNode());
            bestCost = cost+lastDayCost;
            return;
        }

        for(Iterator<Edge> it = node.getEdges(day).iterator(); it.hasNext();){
            Edge edge = it.next();
            Node nextNode = edge.getDestination();

            if(route.contains(nextNode))
                continue;

            route.add(nextNode);
            backtrackDFS(route,day+1,cost+edge.getCost(),nextNode);
            route.remove(nextNode);
        }
    }
}
