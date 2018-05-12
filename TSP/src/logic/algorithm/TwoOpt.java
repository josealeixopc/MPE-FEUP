package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;

public class TwoOpt extends Algorithm {

    public TwoOpt(Graph graph, Algorithm prevAlgorithm){
        super(prevAlgorithm.getName()+" with 2-opt", graph);
        super.bestRoute = prevAlgorithm.bestRoute;
    }

    @Override
    public void computeSolution() {
        int bestRouteCost = getBestRouteCost();
        for(int i=1; i<bestRoute.size()-2; i++){
            for(int j=i+1; j<bestRoute.size()-1; j++){
                ArrayList<Node> route = optSwap(i,j);
                int cost = graph.getRouteCost(route);
                if(cost<0)
                    continue;
                if(cost<bestRouteCost){
                    bestRouteCost=cost;
                    super.bestRoute = route;
                }
            }
        }
    }

    private ArrayList<Node> optSwap(int i, int j) {
        ArrayList<Node> route = new ArrayList<>();

        // add route's beginning
        for(int k=0; k<i; k++){
            route.add(bestRoute.get(k));
        }

        // add route's middle reversed
        for(int k=j; k>=i; k--){
            String nodeName = bestRoute.get(k).getName();
            Edge edge = route.get(route.size()-1).getEdgeToNode(new Node(nodeName),i+j-k);
            if(edge==null)
                return null;
            route.add(edge.getDestination());
        }

        // add route's end
        for(int k=j+1; k<bestRoute.size(); k++){
            route.add(bestRoute.get(k));
        }

        return route;
    }
}
