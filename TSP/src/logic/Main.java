package logic;

import logic.algorithm.*;


import logic.graph.Graph;


public class Main {

    public static void main(String[] args) {

        String[] wantedFiles = new String[]{ //TODO comment unwanted files
                Parser.DATA5,
                Parser.DATA10,
                Parser.DATA15,
                Parser.DATA20,
                Parser.DATA30,
                Parser.DATA40,
                Parser.DATA50,
                Parser.DATA60,
                Parser.DATA70,
                Parser.DATA100,
                Parser.DATA200,
        };

        for(String file: wantedFiles){
            System.out.println("##########################################################");
            System.out.println("Parsing "+file);
            System.out.println("##########################################################");
            long parsingStartTime = System.currentTimeMillis();
            Graph graph = new Parser(file).getGraph();
            long parsingFinishTime = System.currentTimeMillis();
            System.out.println("Finished parsing in "+(parsingFinishTime-parsingStartTime)+"ms");
            System.out.println();

                    new Backtrack(graph),
            Algorithm[] algorithms = new Algorithm[]{ //TODO comment unwanted algorithms
                    new Greedy(graph),
                    new SimulatedAnnealing(graph),
                    new AntColonyOptimization(graph)
            };
            for(Algorithm algorithm: algorithms){
                runAlgorithm(algorithm);

                Algorithm[] optimizations = new Algorithm[]{ //TODO comment unwanted optimizations
                    new KOpt(graph,algorithm, 2)
                };
                for(Algorithm optimization: optimizations){
                    runAlgorithm(optimization);
                }
            }
            System.out.println();
        }
    }

    private static void runAlgorithm(Algorithm algorithm) {
        System.out.println("==="+algorithm.getName()+"===");
        long startTime = System.currentTimeMillis();
        algorithm.computeSolution();
        long finishTime = System.currentTimeMillis();
        System.out.println("Time: "+(finishTime-startTime)+"ms");
        algorithm.printResults();
        System.out.println();
    }
}
