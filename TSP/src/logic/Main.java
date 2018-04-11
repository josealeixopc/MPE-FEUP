package logic;

import logic.algorithm.Algorithm;
import logic.algorithm.AntColonyOptimization;
import logic.algorithm.Backtrack;
import logic.algorithm.Greedy;


import logic.algorithm.SimulatedAnnealing;
import logic.graph.Graph;

public class Main {

    public static void main(String[] args) {

        Graph[] graphs = {new Parser(Parser.DATA5).getGraph(),
                            new Parser(Parser.DATA10).getGraph()};

        for(Graph graph: graphs){
            System.out.println("Data");
            Algorithm greedy = new Greedy(graph);
            greedy.computeSolution();
            System.out.print("Greedy: ");
            greedy.printResults();

            Algorithm backtrack = new Backtrack(graph);
            backtrack.computeSolution();
            System.out.print("Backtrack: ");
            backtrack.printResults();

            Algorithm antColony = new AntColonyOptimization(graph);
            antColony.computeSolution();
            System.out.print("AntColony: ");
            antColony.printResults();

            Algorithm simulatedAnnealing = new SimulatedAnnealing(graph);
            simulatedAnnealing.computeSolution();
            System.out.print("Simulated Annealing: ");
            simulatedAnnealing.printResults();

            System.out.println();
        }


    }
}
