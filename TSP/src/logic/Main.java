package logic;

import logic.algorithm.*;


import logic.graph.Graph;
import logic.meta.GA;
import logic.utils.Utils;

import java.io.File;
import java.util.*;


public class Main {

    // CHOOSE DATASETS TO USE
    private static boolean runFor5Cities = true;
    private static boolean runFor10Cities = true;
    private static boolean runFor15Cities = true;
    private static boolean runFor20Cities = true;
    private static boolean runFor30Cities = true;
    private static boolean runFor40Cities = true;
    private static boolean runFor50Cities = true;
    private static boolean runFor60Cities = true;
    private static boolean runFor70Cities = true;
    private static boolean runFor100Cities = true;

    // GENERATE METAOPTIMIZATION WITH GENETIC ALGORITHMS
    private static boolean OPTIMIZE_PARAMETERS = false;

    // SELECT ALGORITHMS TO RUN
    private static boolean runBacktrack = false;
    private static boolean runGreedy = false;
    private static boolean runSimulatedAnnealing = false;
    private static boolean runAntColonyOptimization = true;
    private static boolean runAcoSa = true; // runs AntColonyOptimizationWithSimulatedAnnealing

    // SELECT EXTRA FEATURES TO USE ON ALGORITHMS
    private static boolean useParallelIfAvailable = true; // compatible with ACO and ACO-SA
    private static boolean useMetaoptimizationIfAvailable = true; // compatible with ACO-SA

    // SELECT FINAL OPTIMIZATIONS TO APPLY TO FINAL ROUTES
    private static boolean use2Opt = true;
    private static boolean use3Opt = true;

    // DO NOT TOUCH THESE!!!!
    private static int numberOfRunsForAllAgorithms;
    public static String CURRENT_EXECUTION_RESULTS_FOLDER;
    private static String[] wantedFiles;

    public static void main(String[] args) {
        setConfiguration();
        for(int i = 0; i < numberOfRunsForAllAgorithms; i++) {
            runAlgorithms();
        }
    }

    private static void setConfiguration(){
        Algorithm.setMaximumComputationTimeMs(30000);
        numberOfRunsForAllAgorithms = 1;

        // define wanted datasets
        ArrayList<String> files = new ArrayList<>();
        if(runFor5Cities)
            files.add(Parser.DATA5);
        if(runFor10Cities)
            files.add(Parser.DATA10);
        if(runFor15Cities)
            files.add(Parser.DATA15);
        if(runFor20Cities)
            files.add(Parser.DATA20);
        if(runFor30Cities)
            files.add(Parser.DATA30);
        if(runFor40Cities)
            files.add(Parser.DATA40);
        if(runFor50Cities)
            files.add(Parser.DATA50);
        if(runFor60Cities)
            files.add(Parser.DATA60);
        if(runFor70Cities)
            files.add(Parser.DATA70);
        if(runFor100Cities)
            files.add(Parser.DATA100);

        wantedFiles = new String[files.size()];
        System.arraycopy(files.toArray(),0,wantedFiles,0, files.size());
    }

    private static Algorithm[] getAlgorithms(Graph graph) {
        ArrayList<Algorithm> algorithms = new ArrayList<>();
        if(runBacktrack)
            algorithms.add(new Backtrack(graph));
        if(runGreedy)
            algorithms.add(new Greedy(graph));
        if(runSimulatedAnnealing)
            algorithms.add(new SimulatedAnnealing(graph));
        if(runAntColonyOptimization) {
            algorithms.add(new AntColonyOptimization(graph));
            if(useParallelIfAvailable)
                algorithms.add(new AntColonyOptimization(graph, true));
        }
        if(runAcoSa) {
            algorithms.add(new AntColonyOptimizationWithSimulatedAnnealing(graph));
            if(useMetaoptimizationIfAvailable)
                algorithms.add(new AntColonyOptimizationWithSimulatedAnnealing(graph, false, true));
            if(useParallelIfAvailable)
                algorithms.add(new AntColonyOptimizationWithSimulatedAnnealing(graph, true, false));
            if(useMetaoptimizationIfAvailable && useParallelIfAvailable)
                algorithms.add(new AntColonyOptimizationWithSimulatedAnnealing(graph, true, true));
        }

        Algorithm[] ret = new Algorithm[algorithms.size()];
        System.arraycopy(algorithms.toArray(),0,ret,0,algorithms.size());
        return ret;
    }

    private static Algorithm[] getOptimizations(Graph graph, Algorithm algorithm) {
        ArrayList<Algorithm> optimizations = new ArrayList<>();
        if(use2Opt)
            optimizations.add(new TwoOpt(graph, algorithm));
        if(use3Opt)
            optimizations.add(new ThreeOpt(graph, algorithm));

        Algorithm[] ret = new Algorithm[optimizations.size()];
        System.arraycopy(optimizations.toArray(),0,ret,0,optimizations.size());
        return ret;
    }

    private static void runAlgorithms(){
        String finalOutput = new String();

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

            Algorithm[] algorithms = getAlgorithms(graph);

            if(OPTIMIZE_PARAMETERS){

                double[] optimizedParameters;

                Algorithm.MAX_PROCESS_TIME_MILLIS = 2000;


                GA ga = new GA();

                optimizedParameters = ga.getOptimizedParameters(graph, 50);
                //optimizedParameters = new double [] {0.08196997880479073, 9.404331306385323, 0.34581929504732634, 766.2836062451062, 3.2456692376427565, 0.04587146320415543, 2.360969972973809};

                //((AntColonyOptimizationWithSimulatedAnnealing)algorithms[6]).setParameters(optimizedParameters);
                //algorithms[6].setName("ACO-SA with meta optimization");

            }

            Algorithm.MAX_PROCESS_TIME_MILLIS = 30000;


            List<String> results = new ArrayList<>();

            for(Algorithm algorithm: algorithms){
                finalOutput += file.split("data_")[1].split(".txt")[0];
                finalOutput += "; ";
                finalOutput += algorithm.getName();
                finalOutput += "; ";

                System.out.println("==="+algorithm.getName()+"===");
                long startTime = System.currentTimeMillis();
                algorithm.computeSolution();
                long finishTime = System.currentTimeMillis();
                long deltaTime = finishTime-startTime;
                System.out.println("Time: "+deltaTime+"ms");
                algorithm.printResults();
                System.out.println();

                if((use2Opt || use3Opt) && algorithm.getBestRouteCost() != -1){
                    finalOutput += deltaTime;
                    finalOutput += "; ";
                    finalOutput += algorithm.getBestRouteCost();
                    finalOutput += "; ";
                    finalOutput += algorithm.getNumIterations();
                    finalOutput += "; ";
                    finalOutput = applyOptimizations(graph, algorithm, deltaTime, results, CURRENT_EXECUTION_RESULTS_FOLDER,algorithmNames, finalOutput);
                }

                finalOutput += "\n";

                algorithmNames.add(algorithm.getName());
                results.add(Integer.toString(algorithm.getBestRouteCost()));

                saveRoutesHistoryOfAlgorithm(CURRENT_EXECUTION_RESULTS_FOLDER, algorithm);
            }

            System.out.println();

            saveBestRoutes(CURRENT_EXECUTION_RESULTS_FOLDER, algorithmNames, numberOfCities, results);
        }

        System.out.println("Finished all instances.");
        System.out.println();
        System.out.println("=====================");
        System.out.println("=====================");
        System.out.println("=====================");
        System.out.println();
        System.out.println(finalOutput);
    }

    private static String applyOptimizations(Graph graph, Algorithm algorithm, long deltaTime, List<String> results, String currentRunResultsFolder, List<String> algorithmNames, String finalOutput) {
        Algorithm[] optimizations = getOptimizations(graph, algorithm);


        for(int i=0; i<optimizations.length; i++){
            KOpt optimization = (KOpt) optimizations[i];
            System.out.println("==="+optimization.getName()+"===");
            long startTime = System.currentTimeMillis();
            optimization.computeSolution();
            long finishTime = System.currentTimeMillis();
            System.out.println("Time: "+(deltaTime+finishTime-startTime)+"ms");
            optimization.printResults();
            System.out.println();

            algorithmNames.add(optimization.getName());
            finalOutput += finishTime-startTime;
            finalOutput += "; ";
            finalOutput += deltaTime+finishTime-startTime;
            finalOutput += "; ";
            finalOutput += optimization.getBestRouteCost();
            finalOutput += "; ";
            finalOutput += optimization.getkOptIterations();
            finalOutput += "; ";
            finalOutput += optimization.getkOptIterations()+algorithm.getNumIterations();
            finalOutput += "; ";

            results.add(Integer.toString(algorithm.getBestRouteCost()));

            saveRoutesHistoryOfAlgorithm(currentRunResultsFolder, optimization);

        }
        return finalOutput;
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
