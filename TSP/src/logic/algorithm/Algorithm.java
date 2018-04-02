package logic.algorithm;

import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;

public abstract class Algorithm {
    Graph graph;
    ArrayList<Node> bestRoute;

    /**
     * Constructor.
     * @param graph graph to be parsed.
     */
    Algorithm(Graph graph){
        this.graph=graph;
    }

    /**
     * Gets the cost of the best route found, if available.
     * @return cost of bestRoute; -1 if bestRoute was not calculated.
     */
    private int getBestRouteCost(){
        return this.graph.getRouteCost(this.bestRoute);
    }

    /**
     * Prints the results of the algorithm in a user-friendly way, including the best route and its cost.
     */
    public void printResults(){
        if(bestRoute==null || bestRoute.size()==0){
            System.out.println("Best route is not defined!");
            return;
        }

        boolean firstPrint = true;
        System.out.print("Route(");
        for(Node node: bestRoute){
            if(firstPrint){
                firstPrint = false;
                System.out.print(node.getName());
            } else System.out.print(", "+node.getName());
        }
        System.out.println("), cost="+getBestRouteCost());
    }

    /**
     * Implementation of the algorithm.
     */
    public abstract void computeSolution();
}
