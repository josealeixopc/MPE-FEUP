package logic.meta;


import logic.Main;
import logic.algorithm.Algorithm;
import logic.algorithm.AntColonyOptimization;
import logic.algorithm.AntColonyOptimizationWithSimulatedAnnealing;
import logic.graph.Graph;
import logic.utils.Pair;
import logic.utils.Utils;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.*;

public class GA {

    private final double MUTATION_RATE = 0.2;
    private final int POPULATION_SIZE = 10;

    public static int PHEROMONE_WEIGHT_INDEX = 0;
    public static int VISIBILITY_WEIGHT_INDEX = 1;
    public static int EVAPORATION_FACTOR_INDEX = 2;
    public static int Q_INDEX = 3;
    public static int INIT_PHEROMONE_LVL_INDEX = 4;
    public static int INITIAL_TEMPERATURE_INDEX = 5;
    public static int TEMPERATURE_DECREASE_INDEX = 6;
    public static int ITERATIONS_PER_TEMPERATURE_INDEX = 7;

    private static int NUMBER_OF_PARAMETERS = 8;

    private static int NUMBER_OF_RUNS_PER_INDIVIDUAL = 10;

    private static int CONSTANT_FOR_FITNESS_CALCULATION = 10000;

    public class Individual implements Comparable<Individual>{

        double[] parameters;

        private List<Integer> lowestCostsAchieved;
        private double fitnessValue;

        public Individual(Individual i){
            this(
                    i.parameters[PHEROMONE_WEIGHT_INDEX],
                    i.parameters[VISIBILITY_WEIGHT_INDEX],
                    i.parameters[EVAPORATION_FACTOR_INDEX],
                    i.parameters[Q_INDEX],
                    i.parameters[INIT_PHEROMONE_LVL_INDEX],
                    i.parameters[INITIAL_TEMPERATURE_INDEX],
                    i.parameters[TEMPERATURE_DECREASE_INDEX],
                    i.parameters[ITERATIONS_PER_TEMPERATURE_INDEX]
            );
        }

        public Individual(double pheromoneWeight, double visibilityWeight, double evaporationFactor, double q, double initPheromoneLvl, double initialTemperature, double temperatureDecrease, double iterationsPerTemperature) {
            this();

            this.parameters[PHEROMONE_WEIGHT_INDEX] = pheromoneWeight;
            this.parameters[VISIBILITY_WEIGHT_INDEX] = visibilityWeight;
            this.parameters[EVAPORATION_FACTOR_INDEX] = evaporationFactor;
            this.parameters[Q_INDEX] = q;
            this.parameters[INIT_PHEROMONE_LVL_INDEX] = initPheromoneLvl;
            this.parameters[INITIAL_TEMPERATURE_INDEX] = initialTemperature;
            this.parameters[TEMPERATURE_DECREASE_INDEX] = temperatureDecrease;
            this.parameters[ITERATIONS_PER_TEMPERATURE_INDEX] = iterationsPerTemperature;
        }

        Individual() {
            this.parameters = new double[NUMBER_OF_PARAMETERS];
            this.lowestCostsAchieved = new ArrayList<>();
        }

        void mutate(){
            Random r = new Random();
            for(int i = 0; i < parameters.length; i++){
                if(r.nextDouble() <= MUTATION_RATE){
                    int sign = ( r.nextBoolean() ? 1 : -1 );
                    parameters[i] += sign * parameters[i] * r.nextDouble();
                }
            }
        }

        void addLowestCostAchieved(int lowestCostAchieved){
            this.lowestCostsAchieved.add(lowestCostAchieved);

            double averageCost = 0;

            for(Integer i : this.lowestCostsAchieved){
                averageCost += i;
            }

            averageCost = averageCost / this.lowestCostsAchieved.size();

            this.fitnessValue = CONSTANT_FOR_FITNESS_CALCULATION/averageCost; // the higher the cost, the less the fitness
        }

        double getFitnessValue(){
            return this.fitnessValue;
        }

        @Override
        public int compareTo(Individual o) {
            return Double.compare(this.fitnessValue, o.fitnessValue);
        }

        @Override
        public String toString() {
            return "Individual{" +
                    "parameters=" + Arrays.toString(parameters) +
                    ", lowestCostsAchieved=" + lowestCostsAchieved +
                    ", fitnessValue=" + fitnessValue +
                    '}';
        }

        public String writeParameteres(){
            StringBuilder sb = new StringBuilder();

            sb.append("Pheromone weight: ").append(this.parameters[PHEROMONE_WEIGHT_INDEX]).append("\n");
            sb.append("Visibility weight: ").append(this.parameters[VISIBILITY_WEIGHT_INDEX]).append("\n");
            sb.append("Evaporation factor: ").append(this.parameters[EVAPORATION_FACTOR_INDEX]).append("\n");
            sb.append("Q: ").append(this.parameters[Q_INDEX]).append("\n");
            sb.append("Initial pheromone level: ").append(this.parameters[INIT_PHEROMONE_LVL_INDEX]).append("\n");

            sb.append("Initial temperature: ").append(this.parameters[INITIAL_TEMPERATURE_INDEX]).append("\n");
            sb.append("Temperature decrease: ").append(this.parameters[TEMPERATURE_DECREASE_INDEX]).append("\n");
            sb.append("Iterations per temperature: ").append(this.parameters[ITERATIONS_PER_TEMPERATURE_INDEX]).append("\n");

            return sb.toString();
        }
    }

    public class Population {
        Individual[] population;
        Random r;

        Population(){
            this.population = new Individual[POPULATION_SIZE];
            this.r= new Random();
        }

        void insertRandomPopulation() {

            Random r = new Random();

            for(int i = 0; i < POPULATION_SIZE; i++){
                Individual ind = new Individual();

                // ACO
                ind.parameters[PHEROMONE_WEIGHT_INDEX] = Utils.randomDoubleInRange(r, 0, 1);
                ind.parameters[VISIBILITY_WEIGHT_INDEX] = Utils.randomDoubleInRange(r, 0, 10);
                ind.parameters[EVAPORATION_FACTOR_INDEX] = Utils.randomDoubleInRange(r, 0, 1);
                ind.parameters[Q_INDEX] = Utils.randomDoubleInRange(r, 0, 1000);
                ind.parameters[INIT_PHEROMONE_LVL_INDEX] = Utils.randomDoubleInRange(r, 0, 40);

                // SA
                ind.parameters[INITIAL_TEMPERATURE_INDEX] = Utils.randomDoubleInRange(r, 0, 2000);
                ind.parameters[ITERATIONS_PER_TEMPERATURE_INDEX] = Utils.randomDoubleInRange(r, 0, 100);
                ind.parameters[TEMPERATURE_DECREASE_INDEX] = Utils.randomDoubleInRange(r, 0, ind.parameters[ITERATIONS_PER_TEMPERATURE_INDEX]);


                this.population[i] = ind;
            }
        }

        Pair<Individual, Individual> crossover(Individual i1, Individual i2){

            Individual newI1 = new Individual();
            Individual newI2 = new Individual();

            Random r = new Random();
            int low = 0;
            int high = NUMBER_OF_PARAMETERS;

            int cutPoint = r.nextInt(high-low) + low;

            System.arraycopy(i1.parameters, 0, newI1.parameters, 0, cutPoint);
            System.arraycopy(i2.parameters, 0, newI2.parameters, 0, cutPoint);

            System.arraycopy(i1.parameters, cutPoint, newI2.parameters, cutPoint, NUMBER_OF_PARAMETERS - cutPoint);
            System.arraycopy(i2.parameters, cutPoint, newI1.parameters, cutPoint, NUMBER_OF_PARAMETERS - cutPoint);

            return new Pair<>(newI1, newI2);
        }

        void nextGeneration(){
            this.sortPopulation();

            List<Individual> newPopulation = new ArrayList<>();

            // Elitism GA with the best 2 passing to next generation
            newPopulation.add(new Individual(this.population[0]));
            newPopulation.add(new Individual(this.population[1]));

            while(newPopulation.size() < POPULATION_SIZE) {

                Individual ind1 = getProbabilisticIndividual();
                Individual ind2 = getProbabilisticIndividual();

                Pair<Individual, Individual> children = crossover(ind1, ind2);

                Individual child1 = children.getLeft();
                Individual child2 = children.getRight();

                child1.mutate();
                child2.mutate();

                newPopulation.add(child1);
                newPopulation.add(child2);
            }

            newPopulation.toArray(this.population);
        }

        void evaluatePopulation(Graph graph){

            List<Thread> threads = new ArrayList<>();

            for(int i = 0; i < NUMBER_OF_RUNS_PER_INDIVIDUAL; i++) {
                for(Individual anIndividual : this.population) {

                    Runnable task = () -> {
                        AntColonyOptimizationWithSimulatedAnnealing acoSa = new AntColonyOptimizationWithSimulatedAnnealing(graph);
                        acoSa.setParameters(anIndividual.parameters);

                        acoSa.computeSolution();
                        anIndividual.addLowestCostAchieved(acoSa.getBestRouteCost());
                    };

                    Thread t = new Thread(task);
                    t.start();
                    threads.add(t);

                }
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void sortPopulation(){
            Arrays.sort(this.population, Collections.reverseOrder()); // reverse because list is in ascending order and we want best first
        }

        Individual getBestIndividual(){
            sortPopulation();
            return population[0];
        }

        Individual getProbabilisticIndividual(){
            // total fitness will always be negative, because fitness = -cost
            double rnd = this.r.nextDouble() * this.getTotalFitnessValue();
            int i;

            for(i = 0; i < POPULATION_SIZE && rnd > 0; i++){
                rnd -= this.population[i].fitnessValue;
            }

            // the individuals with greater fitness are the more probable to be chosen
            return this.population[i-1];
        }

        double getTotalFitnessValue(){
            double totalFitnessValue = 0;

            for (Individual aIndividual : this.population) {
                totalFitnessValue += aIndividual.fitnessValue;
            }

            return totalFitnessValue;
        }

        String writePopulation(){
            StringBuilder sb = new StringBuilder();

            sb.append("## BEGIN OF POPULATION ##");

            for(Individual anIndividual : population){
                sb.append(anIndividual);
                sb.append("\n");
            }

            sb.append("## END OF POPULATION ##");

            return sb.toString();
        }

        @Override
        public String toString() {
            return "Population{" +
                    "population=" + writePopulation() +
                    '}';
        }
    }

    public double[] getOptimizedParameters(Graph graph, int numberOfGenerations) {

        Population population = new Population();
        population.insertRandomPopulation();

        population.evaluatePopulation(graph);

        for(int i = 0; i < numberOfGenerations; i++){
            population.nextGeneration();
            population.evaluatePopulation(graph);
        }

        Individual bestIndividual = population.getBestIndividual();

        saveOptimizedParameters(Main.CURRENT_EXECUTION_RESULTS_FOLDER, bestIndividual);

        return bestIndividual.parameters;
    }

    private static void saveOptimizedParameters(String folderName, GA.Individual individual){

        String filename = folderName + File.separator + "optimized-parameters" + ".txt";
        Utils.createFileIfNotExists(filename);

        String sb =
                "Individual" +
                        "\n" +
                        individual.toString() +
                        "\n\n" +
                        "Detailed parameters" +
                        "\n" +
                        individual.writeParameteres() +
                        "\n";

        Utils.writeToFile(filename, sb);
    }
}
