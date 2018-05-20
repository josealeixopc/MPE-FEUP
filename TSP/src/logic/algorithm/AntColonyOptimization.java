package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;
import logic.utils.Pair;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AntColonyOptimization extends Algorithm {

    private final double PHEROMON_WEIGHT = 0.5; //alpha
    private final double VISIBILITY_WEIGHT = 5; //beta
    private final double EVAPORATION_FACTOR =0.8;
    final double Q = 1000;
    private final double INIT_PHEROMONE_LVL = 20.0;
    private final boolean parallel;

    int bestRouteCost = Integer.MAX_VALUE;
    int nAnts;
    private ArrayList<ArrayList<Node>> ants = new ArrayList<>();
    HashMap<Edge, Double> pheromoneMap;

    public AntColonyOptimization(Graph graph){
        this(graph,30);
    }

    public AntColonyOptimization(Graph graph, int nAnts){
        this(graph, nAnts, false);
    }

    public AntColonyOptimization(Graph graph, boolean parallel){
        this(graph, 30, parallel);
    }

    public AntColonyOptimization(Graph graph, int nAnts, boolean parallel){
        this("Ant Colony Optimization", graph, nAnts, parallel);
    }

    AntColonyOptimization(String name, Graph graph, int nAnts){
        this(name, graph, nAnts, false);
    }

    AntColonyOptimization(String name, Graph graph, int nAnts, boolean parallel){
        super(name+(parallel?" (parallel)":""), graph);
        this.nAnts = nAnts;
        this.parallel = parallel;
        this.pheromoneMap = new HashMap<>();
        resetAntsPath();
    }

    @Override
    public void computeSolution() {
        this.startTimer();

        initPheromonePaths();

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        while(!timerEnded()){
            resetAntsPath();
            if(!parallel)
                moveAnts();
            else moveAntsParallel(executor);
            updatePheromones();

            this.numOfIterations++;
        }

        executor.shutdown();

    }

    protected void initPheromonePaths() {
        List<Edge> edges = graph.getEdges();
        for(Edge edge: edges){
            pheromoneMap.put(edge, INIT_PHEROMONE_LVL);
        }
    }

    protected void resetAntsPath() {
        ants = new ArrayList<>();
        for(int i=0; i<nAnts; i++){
            ArrayList<Node> ant = new ArrayList<>();
            ant.add(graph.getStartNode());
            ants.add(ant);
        }
    }

    protected void moveAnts(){
        for(ArrayList<Node> ant: ants){
            moveAnt(ant);
        }
    }

    private void moveAntsParallel(ThreadPoolExecutor executor) {
        for(ArrayList<Node> ant: ants){
            executor.submit(() -> moveAnt(ant));
        }
        while(executor.getActiveCount()>0); //finish all ants
    }

    private ArrayList<Node> moveAnt(ArrayList<Node> ant) {
        if(ant.isEmpty()) //if ant hit a dead end
            return ant;

        for(int day=0; day<graph.getNodesAmount()-1; day++){
            if(!calculateNextMove(ant,day)){
                ant.clear();
                break;
            }
        }
        if(!ant.isEmpty())
            if(!calculateLastMove(ant,graph.getNodesAmount()-1))
                ant.clear();
        return ant;
    }

    /**
     * Calculates the next move of an ant.
     * @param ant ant to move.
     * @param day day of travel.
     * @return true if a new move was chosen; false in case no move was chosen.
     */
    protected boolean calculateNextMove(ArrayList<Node> ant, int day) {
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

    protected boolean calculateLastMove(ArrayList<Node> ant, int day) {
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

    protected void updatePheromones() {
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

    protected void updatePheromoneForAnt(ArrayList<Node> ant){
        int routeCost = graph.getRouteCost(ant);
        if(routeCost < 0)
            return;
        double addedPheromone = Q/(double)routeCost;
        for(int i=0; i<ant.size()-1; i++){
            Edge edge = ant.get(i).getEdgeToNode(ant.get(i+1),i);
            double currentPheromone = pheromoneMap.get(edge);
            pheromoneMap.put(edge,currentPheromone+addedPheromone);
        }

        if(routeCost<bestRouteCost){
            bestRouteCost = routeCost;
            this.setBestRoute(ant, bestRouteCost);
        }
    }
}
