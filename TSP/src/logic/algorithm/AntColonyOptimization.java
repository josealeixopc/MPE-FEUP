package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;
import logic.utils.Pair;

import java.util.*;

public class AntColonyOptimization extends Algorithm {

    double pheromoneWeight = 0.5; //alpha
    double visibilityWeight = 5; //beta
    double evaporationFactor =0.8;
    double q = 1000;
    double initPheromoneLvl = 20.0;

    int bestRouteCost = Integer.MAX_VALUE;
    int nAnts;
    private ArrayList<ArrayList<Node>> ants = new ArrayList<>();
    HashMap<Edge, Double> pheromoneMap;

    public AntColonyOptimization(Graph graph){
        this(graph,30);
    }

    private AntColonyOptimization(Graph graph, int nAnts){
        this("Ant Colony Optimization", graph, nAnts);
    }

    AntColonyOptimization(String name, Graph graph, int nAnts){
        super(name, graph);
        this.nAnts = nAnts;
        this.pheromoneMap = new HashMap<>();
        resetAntsPath();
    }

    @Override
    public void computeSolution() {
        this.startTimer();

        initPheromonePaths();

        while(!timerEnded()){
            resetAntsPath();
            moveAnts();
            updatePheromones();

            this.numOfIterations++;
        }
    }

    void initPheromonePaths() {
        List<Edge> edges = graph.getEdges();
        for(Edge edge: edges){
            pheromoneMap.put(edge, initPheromoneLvl);
        }
    }

    void resetAntsPath() {
        ants = new ArrayList<>();
        for(int i=0; i<nAnts; i++){
            ArrayList<Node> ant = new ArrayList<>();
            ant.add(graph.getStartNode());
            ants.add(ant);
        }
    }

    void moveAnts(){
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
            double visibility = q /(double)edge.getCost();
            double cost = Math.pow(visibility, visibilityWeight)*Math.pow(pheromone, pheromoneWeight);
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

    void updatePheromones() {
        // evaporate pheromones
        for(Map.Entry<Edge, Double> pheromoneOnEdge: pheromoneMap.entrySet()){
            double newAmount = evaporationFactor *pheromoneOnEdge.getValue();
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
        double addedPheromone = q /(double)routeCost;
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
