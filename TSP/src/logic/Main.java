package logic;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        Parser data5Parser = new Parser(Parser.DATA5);

        System.out.println(data5Parser.getGraph().toString());


    }
}
