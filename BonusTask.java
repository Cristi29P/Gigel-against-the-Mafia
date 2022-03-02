// Copyright 2020
// Author: Matei Simtinică

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Bonus Task
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class BonusTask extends Task {
    private int numberOfNodes;
    private int numberOfEdges;
    private final ArrayList<Edge> edges = new ArrayList<>();
    private String result;

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        Scanner scanner = new Scanner(new File(this.inFilename));
        numberOfNodes = scanner.nextInt();
        numberOfEdges = scanner.nextInt();

        for (int i = 0; i < numberOfEdges; i++) {
            edges.add(new Edge(scanner.nextInt(), scanner.nextInt()));
        }
    }


    @Override
    public void formulateOracleQuestion() throws IOException {
        int v = numberOfNodes;
        int f = numberOfEdges + numberOfNodes;
        int softSum = numberOfNodes + 1;

        StringBuilder builder = new StringBuilder("p wcnf " + v + " " + f + " " + softSum + "\n");

        for (int i = 1; i <= numberOfNodes; i++) {
            builder.append("1 ").append(-i).append(" 0\n");
        }

        for (Edge edge : edges) {
            builder.append(softSum).append(" ").append(edge.p1).append(" ").append(edge.p2).append(" 0\n");
        }

        FileWriter writer = new FileWriter(oracleInFilename);
        writer.write(builder.toString());
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        Scanner scanner = new Scanner(new File(oracleOutFilename));
        StringBuilder builder = new StringBuilder();

        int totalVariables = scanner.nextInt();
        scanner.nextInt();

        for (int i = 0 ; i < totalVariables; i++) {
            if (scanner.hasNext()) {
                int p = scanner.nextInt();
                if (p > 0) {
                    builder.append(p).append(" ");
                }
            }
        }
        result = builder.toString();
    }

    @Override
    public void writeAnswer() throws IOException {
        FileWriter writer = new FileWriter(outFilename);
        writer.write(result);
        writer.close();
    }
}
