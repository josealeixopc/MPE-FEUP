package logic.algorithm;

import logic.Main;
import logic.graph.Graph;
import logic.graph.Node;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class Algorithm {

    private String name;
    Graph graph;
    ArrayList<Node> bestRoute;
    int numOfIterations;

    private TreeMap<Long, Integer> historyOfBestRoutes;

    public static long MAX_PROCESS_TIME_MILLIS = 30000;

    private long startTime = 0;
    private long endTime = 0;

    /**
     * Constructor.
     * @param graph graph to be parsed.
     */
    Algorithm(String name, Graph graph){
        this.name = name;
        this.graph=graph;
        this.historyOfBestRoutes = new TreeMap<>();

        if(MAX_PROCESS_TIME_MILLIS <= 0){
            throw new InvalidParameterException("The time limit for the algorithm execution (Algorithm.MAX_PROCESS_TIME_MILLIS) must be a positive value.");
        }
    }

    void setBestRoute(ArrayList<Node> newBestRoute){
        setBestRoute(newBestRoute, this.graph.getRouteCost(newBestRoute));
    }

    void setBestRoute(ArrayList<Node> newBestRoute, int costOfNewBestRoute){
        this.bestRoute = newBestRoute;

        long timeTaken = this.getTimeElapsed();
        this.historyOfBestRoutes.put(timeTaken, costOfNewBestRoute);
    }

    /**
     * 
     * @return Returns a map whose key is the time it took to find the solution it points to.
     */
    public TreeMap<Long, Integer> getHistoryOfBestRoutes(){
        return this.historyOfBestRoutes;
    }

    public String writeHistoryOfBestRoutes(){
        StringBuilder sb = new StringBuilder();

        if(this.historyOfBestRoutes.entrySet().isEmpty()){
            sb.append("No results available");
        }
        else {
            sb.append("Time in milliseconds,Cost\n");

            for (Map.Entry<Long, Integer> bestRouteRecord : this.historyOfBestRoutes.entrySet()) {
                sb.append(bestRouteRecord.getKey());
                sb.append(",");
                sb.append(bestRouteRecord.getValue());
                sb.append("\n");
            }
        }

        return sb.toString();
    }
    
    /**
     * Gets the cost of the best route found, if available.
     * @return cost of bestRoute; -1 if bestRoute was not calculated.
     */
    public int getBestRouteCost(){
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
        System.out.println(")");
        System.out.println("Cost = "+getBestRouteCost());
        System.out.println("Number of iterations = " + this.numOfIterations);
        System.out.println("Number of nodes = " + this.graph.getNodesAmount());
    }


    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    /**
     * Implementation of the algorithm.
     */
    public abstract void computeSolution();


    /**
     * TIMER
     */

    public static void setMaximumComputationTimeMs(long time){
        MAX_PROCESS_TIME_MILLIS = time;
    }

    void startTimer(){
        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime + MAX_PROCESS_TIME_MILLIS;
    }

    long getTimeElapsed() { return System.currentTimeMillis() - this.startTime; }

    long getTimeLeft() { return this.endTime - System.currentTimeMillis(); }

    boolean timerEnded(){
        return (System.currentTimeMillis() > this.endTime);
    }
}
