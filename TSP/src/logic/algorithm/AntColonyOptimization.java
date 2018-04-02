package logic.algorithm;

import javafx.util.Pair;
import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.*;

public class AntColonyOptimization extends Algorithm {

    private final int MAX_EQUAL_CONSECUTIVE_SOLUTIONS = 5;//stopping condition
    private final double PHEROMON_WEIGHT = 1;
    private final double VISIBILITY_WEIGHT = 1;
    private final double EVAPORATION_FACTOR =0.5;
    private final double Q = 1;

    private int equalSolutionCounter=0;
    private int nAnts;
    private ArrayList<ArrayList<Node>> ants = new ArrayList<>();
    private HashMap<Edge, Double> pheromoneMap;

    public AntColonyOptimization(Graph graph){
        this(graph,7);
    }

    public AntColonyOptimization(Graph graph, int nAnts){
        super(graph);
        this.nAnts = nAnts;
        this.pheromoneMap = new HashMap<>();
        resetAnts();
    }

    private void resetAnts() {
        ants = new ArrayList<>();
        for(int i=0; i<nAnts; i++){
            ArrayList<Node> ant = new ArrayList<>();
            ant.add(graph.getStartNode());
            ants.add(ant);
        }
    }

    @Override
    public void computeSolution() {
        while(equalSolutionCounter<MAX_EQUAL_CONSECUTIVE_SOLUTIONS){
            resetAnts();
            for(ArrayList<Node> ant: ants){
                if(ant.isEmpty())
                    continue;

                for(int day=0; day<graph.getNodesAmount(); day++){
                    if(!calculateNextMove(ant,day)){
                        ant.clear();
                        break;
                    }
                }
            }
            updatePheromones();
        }
    }

    private void updatePheromones() {
        // evaporate pheromones
        for(Map.Entry<Edge, Double> pheromoneOnEdge: pheromoneMap.entrySet()){
            double newAmount = EVAPORATION_FACTOR*pheromoneOnEdge.getValue();
            pheromoneMap.put(pheromoneOnEdge.getKey(), newAmount);
        }

        // add pheromones where ants passed (while also calculation the bestRoute for this iteration)
        int bestRouteCost=Integer.MAX_VALUE;
        for(ArrayList<Node> ant: ants){
            int routeCost = getRouteCost(ant);
            if(routeCost < 0)
                continue;
            double addedPheromone = Q/(double)routeCost;
            for(int i=0; i<ant.size()-1; i++){
                Edge edge = ant.get(i).getEdgeToNode(ant.get(i+1),i);
                double currentPheromone = pheromoneMap.get(edge);
                pheromoneMap.put(edge,currentPheromone+addedPheromone);
            }

            if(routeCost<bestRouteCost){
                bestRouteCost = routeCost;
                bestRoute = ant;
                equalSolutionCounter=0;
            }
        }
        equalSolutionCounter++;
    }

    /**
     * Calculates the next move of an ant.
     * @param ant ant to move.
     * @param day day of travel.
     * @return true if a new move was chosen; false in case no move was chosen.
     */
    private boolean calculateNextMove(ArrayList<Node> ant, int day) {
        Node lastNode = ant.get(ant.size()-1);
        List<Edge> edges = lastNode.getEdges(day);

        ArrayList<Pair<Edge, Double>> edgesWeightedCosts = new ArrayList<>();
        double totalCost=0;
        for(Edge edge: edges){
            if(ant.contains(edge.getDestination()))
                continue;
            pheromoneMap.putIfAbsent(edge, 0.0);
            double pheromone = pheromoneMap.get(edge);
            double visibility = 20/(double)edge.getCost();
            double cost = Math.pow(visibility, VISIBILITY_WEIGHT)*Math.pow(pheromone,PHEROMON_WEIGHT);
            System.out.println(lastNode.getName()+"-("+day+")->"+edge.getDestination().getName()+", vis="+visibility+", pher="+pheromone+", final="+cost);
            totalCost += cost;
            edgesWeightedCosts.add(new Pair<>(edge,cost));
        }

        double chosenCost = new Random().nextDouble()*totalCost;
        for(Pair<Edge, Double> pair: edgesWeightedCosts){
            chosenCost -= pair.getValue();
            if(chosenCost<=0) {
                Edge edge = pair.getKey();
                ant.add(edge.getDestination());
                return true;
            }
        }
        return false;
    }
}
