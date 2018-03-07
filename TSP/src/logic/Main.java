package logic;

import logic.algorithm.Algorithm;
import logic.algorithm.Greedy;

import java.io.File;

import logic.graph.Graph;

public class Main {

    public static void main(String[] args) {

        Parser data5Parser = new Parser(Parser.DATA5);
        Graph graph = data5Parser.getGraph();

        System.out.println(graph.toString());

        Algorithm algorithm = new Greedy(); //this is the line that changes with the algorithm
        //algorithm.computeSolution(graph);
    }
}
