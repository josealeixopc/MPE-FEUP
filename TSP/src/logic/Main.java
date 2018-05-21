package logic;

import logic.algorithm.*;


import logic.graph.Graph;
import logic.meta.GA;
import logic.utils.Utils;

import java.io.File;
import java.util.*;


public class Main {

    public static String CURRENT_EXECUTION_RESULTS_FOLDER;

    public static void main(String[] args) {
        runAlgorithms();
    }

    public static void runAlgorithms(){
        String[] wantedFiles = new String[]{ //comment unwanted files
//                Parser.DATA5, //1950
//                Parser.DATA10, //5375
//                Parser.DATA15, //
//                Parser.DATA20,
                Parser.DATA30,
//                Parser.DATA40,
//                Parser.DATA50,
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

            CURRENT_EXECUTION_RESULTS_FOLDER = Utils.createDirectoryForResults(executionResultsFolder, Algorithm.MAX_PROCESS_TIME_MILLIS, numberOfCities);

            System.out.println("Finished parsing in "+(parsingFinishTime-parsingStartTime)+"ms");
            System.out.println();

            Algorithm[] algorithms = new Algorithm[]{ //comment unwanted algorithms
                    new Backtrack(graph),
                    new Greedy(graph),
                    new SimulatedAnnealing(graph),
                    new AntColonyOptimization(graph),
                    new AntColonyOptimizationWithSimulatedAnnealing(graph)
            };

            boolean optimize = false;

            if(optimize){

                Algorithm.MAX_PROCESS_TIME_MILLIS = 500;

                GA ga = new GA();
                double[] optimizedParameters = ga.getOptimizedParameters(graph, 50);

                ((AntColonyOptimizationWithSimulatedAnnealing)algorithms[4]).setParameters(optimizedParameters);
            }

            Algorithm.MAX_PROCESS_TIME_MILLIS = 5000;

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

                saveRoutesHistoryOfAlgorithm(CURRENT_EXECUTION_RESULTS_FOLDER, algorithm);
            }

            System.out.println();

            saveBestRoutes(CURRENT_EXECUTION_RESULTS_FOLDER, algorithmNames, numberOfCities, results);
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

    private static void saveOptimizedParameters(String folderName, GA.Individual individual){

        String filename = folderName + File.separator + "optimized-parameters" + ".csv";
        Utils.createFileIfNotExists(filename);

        String sb =
                "Individual" +
                "\n" +
                individual.toString() +
                "\n\n" +
                "Detailed parameters" +
                individual.writeParameteres() +
                "\n";

        Utils.writeToFile(filename, sb);
    }
}
