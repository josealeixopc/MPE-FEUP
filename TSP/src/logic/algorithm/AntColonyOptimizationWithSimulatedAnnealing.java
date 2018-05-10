package logic.algorithm;

import javafx.util.Pair;
import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.*;

public class AntColonyOptimizationWithSimulatedAnnealing extends Algorithm {

    private final int MAX_EQUAL_CONSECUTIVE_SOLUTIONS = 200;//stopping condition
    private final double PHEROMON_WEIGHT = 0.5; //alpha
    private final double VISIBILITY_WEIGHT = 5; //beta
    private final double EVAPORATION_FACTOR =0.8;
    private final double Q = 1000;
    private final double INIT_PHEROMONE_LVL = 20.0;

    private int equalSolutionCounter=0;
    private int bestRouteCost = Integer.MAX_VALUE;;
    private int nAnts;
    private ArrayList<ArrayList<Node>> ants = new ArrayList<>();
    private HashMap<Edge, Double> pheromoneMap;

    private ArrayList<Node> iterationBestRoute;
    private int iterationBestCost;

    public AntColonyOptimizationWithSimulatedAnnealing(Graph graph){
        this(graph,30);
    }

    public AntColonyOptimizationWithSimulatedAnnealing(Graph graph, int nAnts){
        super("Ant Colony Optimization with SA Optimization", graph);
        this.nAnts = nAnts;
        this.pheromoneMap = new HashMap<>();
        resetAntsPath();
    }

    private void resetAntsPath() {
        ants = new ArrayList<>();
        for(int i=0; i<nAnts; i++){
            ArrayList<Node> ant = new ArrayList<>();
            ant.add(graph.getStartNode());
            ants.add(ant);
        }
    }

    @Override
    public void computeSolution() {
        initPheromonePaths();
        while(equalSolutionCounter<MAX_EQUAL_CONSECUTIVE_SOLUTIONS){
            iterationBestCost=Integer.MAX_VALUE;
            iterationBestRoute=null;
            resetAntsPath();
            for(ArrayList<Node> ant: ants){
                if(ant.isEmpty()) //if ant hit a dead end
                    continue;

                for(int day=0; day<graph.getNodesAmount()-1; day++){
                    if(!calculateNextMove(ant,day)){
                        ant.clear();
                        break;
                    }
                }
                if(!ant.isEmpty())
                    if(!calculateLastMove(ant,graph.getNodesAmount()-1))
                        ant.clear();
            }
            updatePheromones();
            elitistSimulatedAnnealing();
        }
    }

    private void elitistSimulatedAnnealing() {
        // Get random initial route
        ArrayList<Node> currentRoute = new ArrayList<>(this.iterationBestRoute);

        // Set best route
        this.bestRoute = currentRoute;
        int bestCost = this.graph.getRouteCost(this.bestRoute);


        // Set SA parameters
        float initialTemperature = 1000;
        float temperatureDecrease = 1;
        float iterationsPerTemperature = 5;

        float currentTemperature = initialTemperature;

        while(currentTemperature > 0){

            int numOfIterations = 0;

            while(numOfIterations < iterationsPerTemperature){

                ArrayList<Node> nextRoute = SimulatedAnnealing.swapOperation(this.graph, currentRoute);

                int nextCost = this.graph.getRouteCost(nextRoute);
                int currentCost = this.graph.getRouteCost(currentRoute);

                // If next state is better than current, accept
                if(nextCost < currentCost){
                    currentRoute = nextRoute;

                    // If it's better than best route, update
                    if(currentCost < bestCost){
                        this.bestRoute = currentRoute;
                        bestCost = currentCost;
                    }
                }
                else{

                    float probabilityOfAcceptance = SimulatedAnnealing.calculateAcceptanceProbability(currentCost, nextCost, currentTemperature);

                    if(Math.random() < probabilityOfAcceptance){
                        currentRoute = nextRoute; // next route is worse than current
                    }
                }

                numOfIterations++;
            }

            currentTemperature = currentTemperature - temperatureDecrease;
        }
    }

    private void initPheromonePaths() {
        List<Edge> edges = graph.getEdges();
        for(Edge edge: edges){
            pheromoneMap.put(edge, INIT_PHEROMONE_LVL);
        }
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
            double pheromone = pheromoneMap.get(edge);
            double visibility = Q/(double)edge.getCost();
            double cost = Math.pow(visibility, VISIBILITY_WEIGHT)*Math.pow(pheromone,PHEROMON_WEIGHT);
            //System.out.println(lastNode.getName()+"-("+day+")->"+edge.getDestination().getName()+", vis="+visibility+", pher="+pheromone+", final="+cost);
            totalCost += cost;
            edgesWeightedCosts.add(new Pair<>(edge,cost));
        }

        if(totalCost==0)
            return false; //dead end

        double chosenCost = new Random().nextDouble()*totalCost;
        for(Pair<Edge, Double> pair: edgesWeightedCosts){
            chosenCost -= pair.getValue();
            if(chosenCost<=0.0) {
                Edge edge = pair.getKey();
                ant.add(edge.getDestination());
                return true;
            }
        }
        return false; //should never reach here
    }

    private boolean calculateLastMove(ArrayList<Node> ant, int day) {
        Node lastNode = ant.get(ant.size()-1);
        List<Edge> edges = lastNode.getEdges(day);

        for(Edge edge: edges){
            if(edge.getDestination().getName().equals(graph.getStartNode().getName())){
                ant.add(edge.getDestination());
                return true;
            }
        }
        return false;
    }

    private void updatePheromones() {
        // evaporate pheromones
        for(Map.Entry<Edge, Double> pheromoneOnEdge: pheromoneMap.entrySet()){
            double newAmount = EVAPORATION_FACTOR*pheromoneOnEdge.getValue();
            pheromoneMap.put(pheromoneOnEdge.getKey(), newAmount);
        }

        // add pheromones where ants passed (while also calculation the bestRoute for this iteration)
        for(ArrayList<Node> ant: ants){
            int routeCost = graph.getRouteCost(ant);
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

                iterationBestRoute = ant;
                iterationBestCost = routeCost;
            } else if(routeCost<iterationBestCost) {
                iterationBestRoute = ant;
                iterationBestCost = routeCost;
            }
        }
        equalSolutionCounter++;
    }
}
