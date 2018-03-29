package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealing extends Algorithm {

    private float initialTemperature;
    private float currentTemperature;
    private float temperatureDecrease;

    private ArrayList<Node> currentRoute;
    private int currentCost;

    private int bestCost;

    public SimulatedAnnealing(Graph graph){
        super(graph);

        currentRoute = new ArrayList<>();
        currentCost = 0;

        bestCost = Integer.MAX_VALUE;
    }

    /**
     * Calculates the probability of accepting a worse state
     * @param currentCost Cost of current state
     * @param nextCost Cost of the next (worse) evaluated state
     * @return The probability of accepting the new state
     */
    private float acceptanceProbability(int currentCost, int nextCost) {
        return (float) Math.pow(Math.E, -(currentCost - nextCost) / this.currentTemperature);
    }

    private float costFunction(ArrayList<Node> route){
        return 0;
    }

    /**
     * Tries to find a random valid route for the given first city.
     * @param graph The graph of all the routes.
     * @param maxIterations The maximum number of iterations to try to find a random route.
     * @return The list of nodes constituting the route if a valid path was found. Null if a valid route was not found.
     */
    private ArrayList<Node> createRandomRoute(Graph graph, int maxIterations) {
        ArrayList<Node> randomRoute = new ArrayList<>();
        ArrayList<Node> alreadyVisited = new ArrayList<>();

        Node firstCity = graph.getStartNode();
        int numberOfCities = graph.getNodesAmount();

        for(int iterations = 0; iterations < maxIterations; iterations++) {
            randomRoute.add(firstCity);
            Node currentCity = firstCity;
            alreadyVisited.add(currentCity);

            for(int day = 0; day < numberOfCities; day++) {

                // Last day see if you can go back to initial city
                if(day == numberOfCities - 1) {

                    // If you can travel from the current city to the start (and final) city, then the path is valid.
                    // Else, do nothing and make another iteration.
                    if(currentCity.getCostToNode(firstCity, day) != -1){
                        randomRoute.add(firstCity);
                        return randomRoute;
                    }
                }
                // If it's not the last day, find a random connection to another city
                else {
                    List<Edge> currentDayEdges = currentCity.getEdges(day);

                    int randomInt = new Random().nextInt(currentDayEdges.size());
                    Node nextCity = currentDayEdges.get(randomInt).getDestination();

                    // If it has chosen a city which has already been visited, generate a new number.
                    while(alreadyVisited.contains(nextCity)){
                        randomInt = new Random().nextInt(currentDayEdges.size());
                        nextCity = currentDayEdges.get(randomInt).getDestination();
                    }

                    randomRoute.add(nextCity);
                    currentCity = nextCity;
                    alreadyVisited.add(currentCity);
                }
            }
        }

        // If it reaches this, it has not found a valid random path
        return null;
    }

    @Override
    public void computeSolution() {
        int numOfIterations = 100;

        this.bestRoute = this.createRandomRoute(this.graph, numOfIterations);
    }
}