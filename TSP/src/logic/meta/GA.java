package logic.meta;


import logic.algorithm.Algorithm;
import logic.algorithm.AntColonyOptimization;
import logic.algorithm.AntColonyOptimizationWithSimulatedAnnealing;
import logic.graph.Graph;
import logic.utils.Pair;
import logic.utils.Utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GA {

    final double CROSSOVER_RATE = 0.8;
    private final double MUTATION_RATE = 0.05;
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

    public class Individual implements Comparable<Individual>{

        double[] parameters;

        private int lowestCostAchieved; // the lowest cost achieved
        private int fitnessValue;

        public Individual(double pheromoneWeight, double visibilityWeight, double evaporationFactor, double q, double initPheromoneLvl, double initialTemperature, double temperatureDecrease, double iterationsPerTemperature) {
            this.parameters = new double[NUMBER_OF_PARAMETERS];

            this.parameters[PHEROMONE_WEIGHT_INDEX] = pheromoneWeight;
            this.parameters[VISIBILITY_WEIGHT_INDEX] = visibilityWeight;
            this.parameters[EVAPORATION_FACTOR_INDEX] = evaporationFactor;
            this.parameters[Q_INDEX] = q;
            this.parameters[INIT_PHEROMONE_LVL_INDEX] = initPheromoneLvl;
            this.parameters[INITIAL_TEMPERATURE_INDEX] = initialTemperature;
            this.parameters[TEMPERATURE_DECREASE_INDEX] = temperatureDecrease;
            this.parameters[ITERATIONS_PER_TEMPERATURE_INDEX] = iterationsPerTemperature;
        }

        public Individual(double[] parameters){
            if(parameters.length != NUMBER_OF_PARAMETERS){
                throw new InvalidParameterException();
            }

            this.parameters = parameters;
        }

        Individual() {
            this.parameters = new double[NUMBER_OF_PARAMETERS];
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

        void setLowestCostAchieved(int lowestCostAchieved){
            this.lowestCostAchieved = lowestCostAchieved;
            this.fitnessValue = -lowestCostAchieved; // the higher the cost, the lower the fitness value
        }

        double getFitnessValue(){
            return this.fitnessValue;
        }

        @Override
        public String toString() {
            return "Individual{" +
                    "parameters=" + Arrays.toString(parameters) +
                    ", lowestCostAchieved=" + lowestCostAchieved +
                    ", fitnessValue=" + fitnessValue +
                    '}';
        }

        @Override
        public int compareTo(Individual o) {
            return (this.fitnessValue - o.fitnessValue);
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
            newPopulation.add(this.population[0]);
            newPopulation.add(this.population[1]);

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
        }

        void evaluatePopulation(Graph graph){

            List<Thread> threads = new ArrayList<>();

            for(Individual anIndividual : this.population) {
                Runnable task = () -> {
                    AntColonyOptimizationWithSimulatedAnnealing acoSa = new AntColonyOptimizationWithSimulatedAnnealing(graph);
                    acoSa.setParameters(anIndividual.parameters);

                    acoSa.computeSolution();
                    anIndividual.setLowestCostAchieved(acoSa.getBestRouteCost());
                };

                Thread t = new Thread(task);
                t.start();
                threads.add(t);
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
            Arrays.sort(this.population);
        }

        Individual getBestIndividual(){
            sortPopulation();
            return population[0];
        }

        Individual getProbabilisticIndividual(){
            // total fitness will always be negative, because fitness = -cost
            double rnd = this.r.nextDouble() * this.getTotalFitnessValue();
            int i;

            for(i = 0; i < POPULATION_SIZE && rnd < 0; i++){
                rnd -= this.population[i].fitnessValue;
            }

            // the individuals with greater fitness are the more probable to be chosen
            return this.population[i-1];
        }

        int getTotalFitnessValue(){
            int totalFitnessValue = 0;

            for (Individual aIndividual : this.population) {
                totalFitnessValue += aIndividual.fitnessValue;
            }

            return totalFitnessValue;
        }

        @Override
        public String toString() {
            return "Population{" +
                    "population=" + Arrays.toString(population) +
                    '}';
        }
    }

    public void run(Graph graph, int numberOfGenerations) {

        Population population = new Population();
        population.insertRandomPopulation();

        population.evaluatePopulation(graph);

        for(int i = 0; i < numberOfGenerations; i++){
            population.nextGeneration();
            population.evaluatePopulation(graph);
        }

        Individual bestIndividual = population.getBestIndividual();
        System.out.println(bestIndividual);
    }
}
