package logic;

import logic.algorithm.*;


import logic.graph.Graph;
import logic.meta.GA;
import logic.utils.Utils;

import java.io.File;
import java.util.*;


public class Main {

    private static boolean OPTIMIZE_PARAMETERS;
    private static boolean kOpt;

    public static void main(String[] args) {
        setConfiguration();
        for(int i = 0; i < 10; i++) {
            runAlgorithms();
        }
    }

    private static void setConfiguration(){
        Algorithm.setMaximumComputationTimeMs(30000);
        OPTIMIZE_PARAMETERS = false;
        kOpt = false;
    }

    public static String CURRENT_EXECUTION_RESULTS_FOLDER;

    private static void runAlgorithms(){
        String[] wantedFiles = new String[]{ //comment unwanted files
//                Parser.DATA5, //1950
//                Parser.DATA10, //5375
//                Parser.DATA15, //
                Parser.DATA20,
//                Parser.DATA30,
//                Parser.DATA40,
//                Parser.DATA50,
                Parser.DATA60,
//                Parser.DATA70,
                Parser.DATA100,
//                Parser.DATA200
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
//                    new Backtrack(graph),
//                    new Greedy(graph),
                    new SimulatedAnnealing(graph),
//                    new AntColonyOptimization(graph),
//                    new AntColonyOptimization(graph, true),
//                    new AntColonyOptimizationWithSimulatedAnnealing(graph),
//                    new AntColonyOptimizationWithSimulatedAnnealing(graph),
//                    new AntColonyOptimizationWithSimulatedAnnealing(graph, true)
            };

            if(OPTIMIZE_PARAMETERS){

                double[] optimizedParameters;

                Algorithm.MAX_PROCESS_TIME_MILLIS = 2000;


                //GA ga = new GA();

                //optimizedParameters = ga.getOptimizedParameters(graph, 50);
                optimizedParameters = new double [] {0.08196997880479073, 9.404331306385323, 0.34581929504732634, 766.2836062451062, 3.2456692376427565, 0.04587146320415543, 2.360969972973809};

                ((AntColonyOptimizationWithSimulatedAnnealing)algorithms[6]).setParameters(optimizedParameters);
                algorithms[6].setName("ACO-SA with meta optimization");

            }

            Algorithm.MAX_PROCESS_TIME_MILLIS = 30000;


            List<String> results = new ArrayList<>();

            for(Algorithm algorithm: algorithms){
                System.out.println("==="+algorithm.getName()+"===");
                long startTime = System.currentTimeMillis();
                algorithm.computeSolution();
                long finishTime = System.currentTimeMillis();
                long deltaTime = finishTime-startTime;
                System.out.println("Time: "+deltaTime+"ms");
                algorithm.printResults();
                System.out.println();

                if(kOpt){
                    applyOptimizations(graph, algorithm, deltaTime, results, CURRENT_EXECUTION_RESULTS_FOLDER, algorithmNames);
                }

                algorithmNames.add(algorithm.getName());
                results.add(Integer.toString(algorithm.getBestRouteCost()));

                saveRoutesHistoryOfAlgorithm(CURRENT_EXECUTION_RESULTS_FOLDER, algorithm);
            }

            System.out.println();

            saveBestRoutes(CURRENT_EXECUTION_RESULTS_FOLDER, algorithmNames, numberOfCities, results);
        }

        System.out.println("Finished all instances.");
    }

    private static void applyOptimizations(Graph graph, Algorithm algorithm, long deltaTime, List<String> results, String currentRunResultsFolder, List<String> algorithmNames) {
        Algorithm[] optimizations = new Algorithm[]{ //comment unwanted algorithms
                new TwoOpt(graph, algorithm),
                new ThreeOpt(graph, algorithm)
        };

        for(Algorithm optimization: optimizations){
            System.out.println("==="+optimization.getName()+"===");
            long startTime = System.currentTimeMillis();
            optimization.computeSolution();
            long finishTime = System.currentTimeMillis();
            System.out.println("Time: "+(deltaTime+finishTime-startTime)+"ms");
            optimization.printResults();
            System.out.println();

            algorithmNames.add(optimization.getName());
            results.add(Integer.toString(algorithm.getBestRouteCost()));

            saveRoutesHistoryOfAlgorithm(currentRunResultsFolder, optimization);
        }
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
