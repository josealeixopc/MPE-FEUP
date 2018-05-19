package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;
import logic.utils.Pair;

import java.util.*;

public class AntColonyOptimizationWithSimulatedAnnealing extends Algorithm {

    private final double PHEROMON_WEIGHT = 0.5; //alpha
    private final double VISIBILITY_WEIGHT = 5; //beta
    private final double EVAPORATION_FACTOR =0.8;
    private final double Q = 1000;
    private final double INIT_PHEROMONE_LVL = 20.0;

    private int bestRouteCost = Integer.MAX_VALUE;
    private int nAnts;
    private ArrayList<ArrayList<Node>> ants = new ArrayList<>();
    private HashMap<Edge, Double> pheromoneMap;

    private ArrayList<Node> iterationBestRoute;
    private int iterationBestCost;
    private int iterationWorstCost;
    private int iterationTotalCost;

    public AntColonyOptimizationWithSimulatedAnnealing(Graph graph){
        this(graph,30);
    }

    private AntColonyOptimizationWithSimulatedAnnealing(Graph graph, int nAnts){
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
        this.startTimer();

        initPheromonePaths();

        long end = System.currentTimeMillis() + MAX_PROCESS_TIME_MILLIS;

        while(!this.timerEnded()){
            resetSAValues();
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


        // Set SA parameters
        float initialTemperature = 1000;
        float temperatureDecrease = 10;
        float iterationsPerTemperature = 10;

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
            chosenCost -= pair.getRight();
            if(chosenCost<=0.0) {
                Edge edge = pair.getLeft();
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
            updatePheromoneForAnt(ant);
        }
    }

    private void updatePheromoneForAnt(ArrayList<Node> ant) {
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
            bestRoute = ant;
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
}
