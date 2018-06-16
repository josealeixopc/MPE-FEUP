package logic.algorithm;

import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;

public abstract class KOpt extends Algorithm {

    protected ArrayList<Node> initialRoute;
    protected int kOptIterations;

    KOpt(String name, Graph graph) {
        super(name, graph);
    }

    @Override
    public void printResults(){
        if(bestRoute==null || bestRoute.size()==0){
            System.out.println("Best route is not defined!");
            return;
        }

        boolean firstPrint = true;
        System.out.print("Route(");
        for(Node node: bestRoute){
            if(firstPrint){
                firstPrint = false;
                System.out.print(node.getName());
            } else System.out.print(", "+node.getName());
        }
        System.out.println(")");
        System.out.println("Cost = "+getBestRouteCost());
        System.out.println("Number of iterations = " + super.numOfIterations + " (+"+this.kOptIterations+")");
        System.out.println("Number of nodes = " + super.graph.getNodesAmount());
    }

    public int getkOptIterations() {
        return kOptIterations;
    }
}
