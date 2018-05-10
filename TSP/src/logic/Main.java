package logic;

import logic.algorithm.*;


import logic.graph.Graph;


public class Main {

    public static void main(String[] args) {

        String[] wantedFiles = new String[]{ //comment unwanted files
                Parser.DATA5, //1950
                Parser.DATA10, //5375
                Parser.DATA15, //
                //Parser.DATA20,
                //Parser.DATA30,
                //Parser.DATA40,
                //Parser.DATA50,
//                Parser.DATA60,
//                Parser.DATA70,
//                Parser.DATA100,
//                Parser.DATA200,
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

            Algorithm[] algorithms = new Algorithm[]{ //comment unwanted algorithms
                    //new Backtrack(graph),
                    //new Greedy(graph),
                    //new SimulatedAnnealing(graph),
                    new AntColonyOptimization(graph),
                    new AntColonyOptimizationWithSimulatedAnnealing(graph)
            };
            for(Algorithm algorithm: algorithms){
                System.out.println("==="+algorithm.getName()+"===");
                long startTime = System.currentTimeMillis();
                algorithm.computeSolution();
                long finishTime = System.currentTimeMillis();
                System.out.println("Time: "+(finishTime-startTime)+"ms");
                algorithm.printResults();
                System.out.println();
            }
            System.out.println();
        }
        /*for(Graph graph: graphs){
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
        }//*/


    }
}
