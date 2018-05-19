package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealing extends Algorithm {

    public SimulatedAnnealing(Graph graph){
        super("Simulated Annealing", graph);
    }

    /**
     * Calculates the probability of accepting a worse state
     * @param currentCost Cost of current state
     * @param nextCost Cost of the next (worse) evaluated state
     * @return The probability of accepting the new state
     */
    static float calculateAcceptanceProbability(int currentCost, int nextCost, float temperature) {
        return (float) Math.pow(Math.E, -(currentCost - nextCost) / temperature);
    }

    static ArrayList<Node> swapOperation(Graph graph, ArrayList<Node> route) {

        ArrayList<Node> swappedRoute = new ArrayList<>(route);

        int index1 = new Random().nextInt(swappedRoute.size() - 2) + 1;
        int index2 = new Random().nextInt(swappedRoute.size() - 2) + 1;

        Collections.swap(swappedRoute, index1, index2);

        // swap until you have a valid route
        while(graph.getRouteCost(swappedRoute) == -1){
            index1 = new Random().nextInt(swappedRoute.size() - 2) + 1;
            index2 = new Random().nextInt(swappedRoute.size() - 2) + 1;
            Collections.swap(swappedRoute, index1, index2);
        }

        return swappedRoute;
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
        this.startTimer();

        // Get random initial route
        int maxIterations = 100;
        ArrayList<Node> currentRoute = this.createRandomRoute(this.graph, maxIterations);

        if(currentRoute == null){
            System.out.println("Could not generate a random initial path for Simulated Annealing.");
            return;
        }

        // Set best route
        this.bestRoute = currentRoute;
        int bestCost = this.graph.getRouteCost(this.bestRoute);


        // Set SA parameters
        float initialTemperature = 1000;
        float temperatureDecrease = 0.05f;
        float iterationsPerTemperature = 100;

        float currentTemperature = initialTemperature;

        while(currentTemperature > 0 && !this.timerEnded()){

            int numOfIterations = 0;

            while(numOfIterations < iterationsPerTemperature){

                ArrayList<Node> nextRoute = swapOperation(this.graph, currentRoute);

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

                    float probabilityOfAcceptance = calculateAcceptanceProbability(currentCost, nextCost, currentTemperature);

                    if(Math.random() < probabilityOfAcceptance){
                        currentRoute = nextRoute; // next route is worse than current
                    }
                }

                numOfIterations++;
            }

            currentTemperature = currentTemperature - temperatureDecrease;
        }
    }
}