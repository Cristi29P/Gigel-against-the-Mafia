// Copyright 2020
// Author: Matei Simtinică

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {
    private int numberOfFamilies;
    private int numberOfColors;
    private String result;

    private final ArrayList<Edge> edges = new ArrayList<>();

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

        numberOfFamilies = scanner.nextInt();
        int numberOfEdges = scanner.nextInt();
        numberOfColors = scanner.nextInt();

        for (int i = 0; i < numberOfEdges; ++i) {
           edges.add(new Edge(scanner.nextInt(), scanner.nextInt()));
        }
    }

    public String generateLongClause(int family, int colors) {
        StringBuilder sb = new StringBuilder();

        for (int i = ((family - 1) * colors + 1); i <= family * colors; ++i) {
            sb.append(i).append(" ");
        }
        return sb.append("0\n").toString();
    }

    public String generateShortClause(int family, int colors) {
        StringBuilder sb = new StringBuilder();

        for (int i = ((family - 1) * colors + 1); i <= family * colors; ++i) {
            for (int j = i + 1; j <= family * colors; ++j) {
                sb.append(-i).append(" ").append(-j).append(" 0\n");
            }
        }
        return sb.toString();
    }

    public String generateExclusionClause(Edge edge, int colors) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < colors; i++) {
            sb.append(-((edge.p1 - 1) * colors + 1 + i)).append(" ")
                    .append(-((edge.p2 - 1) * colors + 1 + i)).append(" 0\n");
        }

        return sb.toString();
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        int numberOfVariables = numberOfColors * numberOfFamilies;
        int numberOfClauses = numberOfFamilies + numberOfVariables + edges.size() * numberOfColors;

        StringBuilder sb = new StringBuilder("p cnf");

        sb.append(" ").append(numberOfVariables).append(" ").append(numberOfClauses).append("\n");

        for (int i = 1; i <= numberOfFamilies; i++) {
            sb.append(generateLongClause(i, numberOfColors)).append(generateShortClause(i, numberOfColors));
        }

        for(Edge edge : edges) {
            sb.append(generateExclusionClause(edge, numberOfColors));
        }

        FileWriter writer = new FileWriter(oracleInFilename);
        writer.write(sb.toString());
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        Scanner scanner = new Scanner(new File(oracleOutFilename));
        StringBuilder builder = new StringBuilder();
        builder.append(scanner.nextLine()).append(" \n");
        if (scanner.hasNext()) {
            int numberOfVariable = scanner.nextInt();
            for (int i = 0; i < numberOfVariable; i++) {
                int aux2 = scanner.nextInt();
                if (aux2 > 0) {
                    int modulo = aux2 % numberOfColors;
                    if (modulo == 0) {
                        modulo = numberOfColors;
                    }
                    builder.append(modulo).append(" ");
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
