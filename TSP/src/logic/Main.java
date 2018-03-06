package logic;


import logic.graph.Graph;

public class Main {

    public static void main(String[] args) {

        Parser data5Parser = new Parser(Parser.DATA5);
        Graph graph = data5Parser.getGraph();

        System.out.println(graph.toString());

    }
}
