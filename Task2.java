// Copyright 2020
// Author: Matei Simtinică

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    private int numberOfFamilies;
    private int maxSize;
    private String result;
    private int numberOfClauses = 0;

    private int[][] adjMatrix;

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
        maxSize = scanner.nextInt();

        adjMatrix = new int[numberOfFamilies][numberOfFamilies];

        for (int i = 0; i < numberOfEdges; i++) {
            int p1 = scanner.nextInt();
            int p2 = scanner.nextInt();

            adjMatrix[p1 - 1][p2 - 1] = 1;
            adjMatrix[p2 - 1][p1 - 1] = 1;
        }

    }

    public String generateLongClause(int sizeOfClique, int numberOfFam) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i <= numberOfFam; i++) {
            builder.append((sizeOfClique - 1) * numberOfFam + i).append(" ");
        }

        return builder.append("0\n").toString();
    }

    public String generateShortClause(int family, int sizeOfClique) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i <= sizeOfClique; i++) {
            for (int j = i + 1; j <= sizeOfClique; j++) {
                builder.append(-(numberOfFamilies * (i - 1) + family)).append(" ").append(-(numberOfFamilies * (j - 1) + family)).append(" 0\n");
                numberOfClauses++;
            }
        }

        return builder.toString();
    }

    public String generateSmallExclusionClause(int nodA, int nodB) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < maxSize - 1; i++) {
            for (int j = i + 1; j < maxSize; j++) {
                builder.append(-(i * numberOfFamilies + nodA)).append(" ").append(-(j * numberOfFamilies + nodB)).append(" 0\n");
                numberOfClauses++;
            }
        }
        return builder.toString();
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        int numberOfVariables = maxSize * numberOfFamilies;
        numberOfClauses = 0;

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= maxSize; i++) {
            sb.append(generateLongClause(i, numberOfFamilies));
            numberOfClauses++;
        }

        for (int i = 1; i <= numberOfFamilies; i++) {
            sb.append(generateShortClause(i, maxSize));
        }

        for (int i = 0; i < numberOfFamilies; i++) {
            for (int j = 0; j < numberOfFamilies; j++) {
                if ((i < j) && (adjMatrix[i][j] == 0)) {
                    sb.append(generateSmallExclusionClause(i + 1, j + 1))
                            .append(generateSmallExclusionClause(j + 1, i + 1));
                }
            }
        }

        FileWriter writer = new FileWriter(oracleInFilename);
        writer.write("p cnf " + numberOfVariables + " " + numberOfClauses + "\n" + sb.toString());
        writer.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        Scanner scanner = new Scanner(new File(oracleOutFilename));
        StringBuilder builder = new StringBuilder();
        builder.append(scanner.nextLine()).append(" \n");
        if (scanner.hasNext()) {
            int numberOfVariables = scanner.nextInt();
            for (int i = 0; i < numberOfVariables; i++) {
                int aux2 = scanner.nextInt();
                if (aux2 > 0) {
                    int modulo = aux2 % numberOfFamilies;
                    if (modulo == 0) {
                        modulo = numberOfFamilies;
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
