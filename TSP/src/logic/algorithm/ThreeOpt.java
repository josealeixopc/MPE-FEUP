package logic.algorithm;

import logic.graph.Graph;
import logic.graph.Node;

import java.util.ArrayList;

public class ThreeOpt extends Algorithm {

    private ArrayList<Node> initialRoute;

    public ThreeOpt(Graph graph, Algorithm prevAlgorithm){
        super(prevAlgorithm.getName()+" with 3-opt", graph);
        super.bestRoute = prevAlgorithm.bestRoute;
        this.initialRoute = prevAlgorithm.bestRoute;
    }

    @Override
    public void computeSolution() {
        int bestRouteCost = getBestRouteCost();
        for(int i=1; i<initialRoute.size()-3; i++){
            for(int j=i+1; j<initialRoute.size()-2; j++){
                for(int k=j+1; k<initialRoute.size()-1; k++){
                    ArrayList<Node> route = optSwap(i, j, k);
                    int cost = graph.getRouteCost(route);
                    if(cost<0)
                        continue;
                    if(cost<bestRouteCost) {
                        bestRouteCost = cost;
                        super.bestRoute = route;
                    }
                }
            }
        }
    }

    private ArrayList<Node> optSwap(int i, int j, int k) {
        ArrayList<Node> route = new ArrayList<>(),
                section1 = new ArrayList<>(),
                section2 = new ArrayList<>();

        // part1
        for(int l=0; l<i; l++){
            route.add(initialRoute.get(l));
        }

        // part2
        for(int l=i; l<j; l++){
            section1.add(initialRoute.get(l));
        }

        // part3
        for(int l=j; l<k; l++){
            section2.add(initialRoute.get(l));
        }

        //twist in 4 different positions the rest of the course
        ArrayList<Node> route1 = new ArrayList<>(route);
        route1.addAll(reverseArray(section1));
        route1.addAll(reverseArray(section2));

        ArrayList<Node> route2 = new ArrayList<>(route);
        route2.addAll(section2);
        route2.addAll(section1);

        ArrayList<Node> route3 = new ArrayList<>(route);
        route3.addAll(section2);
        route3.addAll(reverseArray(section1));

        ArrayList<Node> route4 = new ArrayList<>(route);
        route4.addAll(reverseArray(section2));
        route4.addAll(section1);

        //get best twist
        int costR1 = graph.getRouteCost(route1);
        int costR2 = graph.getRouteCost(route2);
        int costR3 = graph.getRouteCost(route3);
        int costR4 = graph.getRouteCost(route4);
        int bestCost = Math.min(Math.max(costR1,costR2),Math.min(costR3,costR4));
        if(bestCost==costR1)
            route = route1;
        else if(bestCost==costR2)
            route = route2;
        else if(bestCost==costR3)
            route = route3;
        else route = route4;

        // add route's end
        for(int l=k; l<bestRoute.size(); l++){
            route.add(bestRoute.get(l));
        }

        return route;
    }

    private static ArrayList<Node> reverseArray(ArrayList<Node> a){
        ArrayList<Node> ret = new ArrayList<>();
        for(int i=a.size()-1; i>=0; i--)
            ret.add(a.get(i));
        return ret;
    }
}
