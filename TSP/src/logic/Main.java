package logic;

import logic.algorithm.*;


import logic.graph.Graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


public class Main {

    private static String RESULTS_FOLDER = "results";

    private static void saveResultsForTimeLimit(String timeLimitInSeconds, List<String> algorithmNames, Map<String, List<String>> numOfCitiesToAlgorithmResults){
        String filename = RESULTS_FOLDER + File.separator + timeLimitInSeconds + "ms" + ".csv";

        boolean directoryCreated = new File(RESULTS_FOLDER).mkdirs();

        try {
            File file = new File(filename);
            boolean fileCreated = file.createNewFile();

            StringBuilder sb = new StringBuilder();

            sb.append("Number of cities");

            for(String a :algorithmNames){
                sb.append(",");
                sb.append(a);
            }

            sb.append("\n");

            for(Map.Entry<String, List<String>> entry : numOfCitiesToAlgorithmResults.entrySet()){
                sb.append(entry.getKey());

                for(String s : entry.getValue()){
                    sb.append(",");
                    sb.append(s);
                }

                sb.append("\n");
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(sb.toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

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

        List<String> algorithmNames = new ArrayList<>();
        Map<String, List<String>> numOfCitiesToAlgorithmResults = new HashMap<>();

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
                    new Backtrack(graph),
                    new Greedy(graph),
                    new SimulatedAnnealing(graph),
                    new AntColonyOptimization(graph),
                    new AntColonyOptimizationWithSimulatedAnnealing(graph)
            };

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
            }
            System.out.println();

            numOfCitiesToAlgorithmResults.put(Integer.toString(graph.getNodesAmount()), results);

            saveResultsForTimeLimit(Long.toString(Algorithm.MAX_PROCESS_TIME_MILLIS), algorithmNames, numOfCitiesToAlgorithmResults);
        }
    }
}
