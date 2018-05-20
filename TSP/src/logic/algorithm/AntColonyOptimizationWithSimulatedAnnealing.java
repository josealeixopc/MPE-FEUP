package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.*;

public class AntColonyOptimizationWithSimulatedAnnealing extends AntColonyOptimization {

    private ArrayList<Node> iterationBestRoute;
    private int iterationBestCost;
    private int iterationWorstCost;
    private int iterationTotalCost;

    public AntColonyOptimizationWithSimulatedAnnealing(Graph graph){
        this(graph,30);
    }

    private AntColonyOptimizationWithSimulatedAnnealing(Graph graph, int nAnts){
        super("Ant Colony Optimization with SA Optimization", graph, nAnts);
    }

    // Parameters

    // ACO Parameters are in AntColonyOptimization.java

    // Elitist SA parameters
    private float initialTemperature = 1000;
    private float temperatureDecrease = 10;
    private float iterationsPerTemperature = 10;

    @Override
    public void computeSolution() {
        this.startTimer();

        initPheromonePaths();

        while(!this.timerEnded()){
            resetSAValues();
            resetAntsPath();
            moveAnts();
            updatePheromones();

            float populationDiversity = (((float)iterationTotalCost/nAnts)-bestRouteCost)/(iterationWorstCost-bestRouteCost);
            if(populationDiversity>0.5f)
                elitistSimulatedAnnealing();
            else mutationOperator();

            this.numOfIterations++;
        }
    }

    private void mutationOperator() {
        int k1 = new Random().nextInt(graph.getNodesAmount()-2)+1;
        int k2= k1+1;
        ArrayList<Node> ant = new ArrayList<>(bestRoute);
        Node tmp = ant.get(k1);
        ant.set(k1,ant.get(k2));
        ant.set(k2,tmp);
        updatePheromoneForAnt(ant);
    }

    private void resetSAValues() {
        iterationBestCost=Integer.MAX_VALUE;
        iterationWorstCost=-1;
        iterationTotalCost=0;
        iterationBestRoute=null;
    }

    private void elitistSimulatedAnnealing() {
        // Get random initial route
        ArrayList<Node> currentRoute = new ArrayList<>(this.iterationBestRoute);
        ArrayList<Node> bestRouteSA;

        // Set best route
        bestRouteSA = currentRoute;
        int bestCost = this.graph.getRouteCost(bestRouteSA);

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
                        bestRouteSA = currentRoute;
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
        updatePheromoneForAnt(bestRouteSA);
    }

    protected void updatePheromoneForAnt(ArrayList<Node> ant) {
        int routeCost = graph.getRouteCost(ant);
        if(routeCost < 0)
            return;

        double addedPheromone = Q/(double)routeCost;
        for(int i=0; i<ant.size()-1; i++){
            Edge edge = ant.get(i).getEdgeToNode(ant.get(i+1),i);
            double currentPheromone = pheromoneMap.get(edge);
            pheromoneMap.put(edge,currentPheromone+addedPheromone);
        }

        // update values for SA and stopping condition
        if(routeCost<bestRouteCost){
            bestRouteCost = routeCost;
            this.setBestRoute(ant);
            iterationBestRoute = ant;
            iterationBestCost = routeCost;



        } else if(routeCost<iterationBestCost) {
            iterationBestRoute = ant;
            iterationBestCost = routeCost;
        }

        if(routeCost>iterationWorstCost)
            iterationWorstCost=routeCost;

        iterationTotalCost+=routeCost;
    }

    /**
     * Optimize ACO-SA parameters using genetic algorithms
     */
    private void optimizeParameters(){

    }
}
