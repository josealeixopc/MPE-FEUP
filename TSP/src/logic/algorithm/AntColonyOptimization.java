package logic.algorithm;

import logic.graph.Edge;
import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntColonyOptimization extends Algorithm {

    private final int MAX_EQUAL_CONSECUTIVE_SOLUTIONS = 5;//stopping condition
    private int nAnts;
    private ArrayList<ArrayList<Node>> ants = new ArrayList<>();
    private HashMap<Edge, Integer> pheromoneMap;

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
            ants.add(new ArrayList<>());
        }
    }

    @Override
    public void computeSolution() {
        int equalSolutionCounter=0;
        while(equalSolutionCounter<MAX_EQUAL_CONSECUTIVE_SOLUTIONS){
            resetAnts();
            for(int day=0; day<graph.getNodesAmount(); day++){
                for(ArrayList<Node> ant: ants){
                    //TODO create new path using distance and pheromones
                }
            }
            //TODO update pheromones (here or inside for?)
            //TODO set bestRoute (and increment/reset counter)
        }
    }
}
