package logic;

import logic.algorithm.Algorithm;
import logic.algorithm.Greedy;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        Parser data5Parser = new Parser(Parser.DATA5);

        System.out.println(data5Parser.getGraph().toString());

        Algorithm algorithm = new Greedy(); //this is the line that changes with the algorithm
        //algorithm.computeSolution(graph);
    }
}
