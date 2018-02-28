package logic;

import logic.graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class Parser {

    private static final String PREFIX = "travelling-salesman-challenge\\real_data\\";
    static final String DATA5 = PREFIX+"data_5.txt";

    private Graph graph;

    public Parser(String filename) {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            String line = br.readLine();
            graph = new Graph(line);

            while ((line = br.readLine()) != null) {
                parseLine(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void parseLine(String s) {
        String[] args = s.split(" ");
        if(args.length!=4)
            throw new IllegalArgumentException();

        String origin = args[0];
        String destination = args[1];
        int date = Integer.parseInt(args[2]);
        int cost = Integer.parseInt(args[3]);
        System.out.println("From: "+origin+", To: "+destination+", Date: "+date+", Cost: "+cost);

        graph.addEdge(origin, destination, date, cost);
    }

    public Graph getGraph() {
        return graph;
    }
}
