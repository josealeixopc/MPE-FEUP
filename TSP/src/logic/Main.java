package logic;

import logic.algorithm.*;


import logic.graph.Graph;
import logic.meta.GA;
import logic.utils.Utils;

import java.io.File;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        testingGA();
    }

    public static void runAlgorithms(){
        String[] wantedFiles = new String[]{ //comment unwanted files
                Parser.DATA5, //1950
                Parser.DATA10, //5375
                Parser.DATA15, //
                Parser.DATA20,
                Parser.DATA30,
                //Parser.DATA40,
                //Parser.DATA50,
//                Parser.DATA60,
//                Parser.DATA70,
//                Parser.DATA100,
//                Parser.DATA200,
        };

        Utils.createDirectoryIfNotExists(Utils.RESULTS_FOLDER);
        String executionResultsFolder = Utils.createDirectoryForExecution();

        List<String> algorithmNames = new ArrayList<>();

        for(String file: wantedFiles){

            System.out.println("##########################################################");
            System.out.println("Parsing "+file);
            System.out.println("##########################################################");

            long parsingStartTime = System.currentTimeMillis();
            Graph graph = new Parser(file).getGraph();
            long parsingFinishTime = System.currentTimeMillis();

            int numberOfCities = graph.getNodesAmount();

            System.out.println("Finished parsing in "+(parsingFinishTime-parsingStartTime)+"ms");
            System.out.println();

            Algorithm[] algorithms = new Algorithm[]{ //comment unwanted algorithms
                    new Backtrack(graph),
                    new Greedy(graph),
                    new SimulatedAnnealing(graph),
                    new AntColonyOptimization(graph),
                    new AntColonyOptimizationWithSimulatedAnnealing(graph)
            };

            String currentRunResultsFolder = Utils.createDirectoryForResults(executionResultsFolder, Algorithm.MAX_PROCESS_TIME_MILLIS, numberOfCities);

            for(Algorithm a : algorithms){
                if(!algorithmNames.contains(a.getName())){
                    algorithmNames.add(a.getName());
                }
            }

            List<String> results = new ArrayList<>();

            for(Algorithm algorithm: algorithms){
                System.out.println("==="+algorithm.getName()+"===");
                long startTime = System.currentTimeMillis();
                algorithm.computeSolution();
                long finishTime = System.currentTimeMillis();
                System.out.println("Time: "+(finishTime-startTime)+"ms");
                algorithm.printResults();
                System.out.println();

                results.add(Integer.toString(algorithm.getBestRouteCost()));

                saveRoutesHistoryOfAlgorithm(currentRunResultsFolder, algorithm);
            }

            System.out.println();

            saveBestRoutes(currentRunResultsFolder, algorithmNames, numberOfCities, results);
        }

        System.out.println("Finished all instances.");
    }

    private static void saveBestRoutes(String folderName, List<String> algorithmNames, int numberOfCities, List<String> results){

        String filename = folderName + File.separator + "best-routes" + ".csv";
        Utils.createFileIfNotExists(filename);

        StringBuilder sb = new StringBuilder();

        sb.append("Number of cities");

        for(String a :algorithmNames){
            sb.append(",");
            sb.append(a);
        }

        sb.append("\n");


        sb.append(numberOfCities);

        for(String s : results){
            sb.append(",");
            sb.append(s);
        }

        sb.append("\n");

        Utils.writeToFile(filename, sb.toString());
    }

    private static void saveRoutesHistoryOfAlgorithm(String folderName, Algorithm algorithm){
        String historyFileName = folderName + File.separator + "cost-history_" + algorithm.getName() + ".csv";
        Utils.createFileIfNotExists(historyFileName);
        Utils.writeToFile(historyFileName, algorithm.writeHistoryOfBestRoutes());
    }

    private static void testingGA(){
        System.out.println("Going for meta");

        Graph graph = new Parser(Parser.DATA5).getGraph();

        GA ga = new GA();
        ga.run(graph, 5);
    }
}
