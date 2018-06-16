package logic;

import logic.graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    private static final String PREFIX = "travelling-salesman-challenge" + File.separator + "real_data" + File.separator;
    static final String DATA5 = PREFIX + "data_5.txt";
    static final String DATA10 = PREFIX + "data_10.txt";
    static final String DATA15 = PREFIX + "data_15.txt";
    static final String DATA20 = PREFIX + "data_20.txt";
    static final String DATA30 = PREFIX + "data_30.txt";
    static final String DATA40 = PREFIX + "data_40.txt";
    static final String DATA50 = PREFIX + "data_50.txt";
    static final String DATA60 = PREFIX + "data_60.txt";
    static final String DATA70 = PREFIX + "data_70.txt";
    static final String DATA100 = PREFIX + "data_100.txt";

    public static final int[] OPTIMAL_SOLUTIONS = new int[]{
            1950, 5375, 4281, Integer.MAX_VALUE,
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    private Graph graph;

    /**
     * Creates a new parser for a given datafile.
     * @param filename The path of the file, relative to the root folder.
     */
    Parser(String filename) {
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

    /**
     * Parses a new line form the datafile (other than the first line) and adds the respective edge to the already existing graph.
     * @param s The string containing the edge's information in the '<FROM> <TO> <DateOfDeparture> <PRICE>' format.
     */
    private void parseLine(String s) {
        String[] args = s.split(" ");
        if(args.length!=4)
            throw new IllegalArgumentException();

        String origin = args[0];
        String destination = args[1];
        int date = Integer.parseInt(args[2]);
        int cost = Integer.parseInt(args[3]);
        //System.out.println("From: "+origin+", To: "+destination+", Date: "+date+", Cost: "+cost);

        graph.addEdge(origin, destination, date, cost);
    }

    /**
     *
     * @return The graph generated by the parser.
     */
    public Graph getGraph() {
        return graph;
    }
}
