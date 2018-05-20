package logic.meta;


import logic.utils.Pair;
import logic.utils.Utils;

import javax.rmi.CORBA.Util;
import java.security.InvalidParameterException;
import java.util.Random;

public class GA {

    final double CROSSOVER_RATE = 0.8;
    private final double MUTATION_RATE = 0.05;
    private final int POPULATION_SIZE = 20;

    static int PHEROMONE_WEIGHT_INDEX = 0;
    static int VISIBILITY_WEIGHT_INDEX = 1;
    static int EVAPORATION_FACTOR_INDEX = 2;
    static int Q_INDEX = 3;
    static int INIT_PHEROMONE_LVL_INDEX = 4;
    static int INITIAL_TEMPERATURE_INDEX = 5;
    static int TEMPERATURE_DECREASE_INDEX = 6;
    static int ITERATIONS_PER_TEMPERATURE_INDEX = 7;

    private static int NUMBER_OF_PARAMETERS = 8;

    public class Individual {
        int visibilityWeight = 1;
        int evaporationFactor = 2;
        int q = 3;
        int initPheromoneLvl = 4;

        int initialTemperature = 5;
        int temperatureDecrease = 6;
        int iterationsPerTemperature = 7;

        double[] parameters;

        int fitnessValue; // the lowest cost achieved

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

        public Individual() {}

        void mutate(){
            Random r = new Random();
            for(int i = 0; i < parameters.length; i++){
                if(r.nextDouble() <= MUTATION_RATE){
                    int sign = ( r.nextBoolean() ? 1 : -1 );
                    parameters[i] += sign * parameters[i] * r.nextDouble();
                }
            }
        }

        void setFitnessValue(int fitnessValue){
            this.fitnessValue = fitnessValue;
        }

        int getFitnessValue(){
            return this.fitnessValue;
        }
    }

    public class Population {
        Individual[] population;

        Population(){
            this.initializePopulation();
        }

        void initializePopulation() {

            this.population = new Individual[POPULATION_SIZE];

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
    }
}
