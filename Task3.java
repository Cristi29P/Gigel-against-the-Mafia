// Copyright 2020
// Author: Matei Simtinică

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
@SuppressWarnings("StringBufferReplaceableByString")
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;

    private int numberOfNodes;
    private int numberOfEdges;
    private int[][] adjMatrix;

    private String result;

    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();

        reduceToTask2(task2Solver);
        extractAnswerFromTask2();

        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        Scanner scanner = new Scanner(new File(this.inFilename));

        numberOfNodes = scanner.nextInt();
        numberOfEdges = scanner.nextInt();

        adjMatrix = new int[numberOfNodes][numberOfNodes];

        for (int i = 0; i < numberOfEdges; i++) {
            int p1 = scanner.nextInt();
            int p2 = scanner.nextInt();

            adjMatrix[p1 - 1][p2 - 1] = 1;
            adjMatrix[p2 - 1][p1 - 1] = 1;
        }
    }


    public void generateComplementaryMatrix() {
        for (int i = 0; i < numberOfNodes; i++) {
            for (int j = 0; j < numberOfNodes; j++) {
                if (i != j) {
                    if (adjMatrix[i][j] == 0) {
                        adjMatrix[i][j] = 1;
                    } else {
                        adjMatrix[i][j] = 0;
                    }
                }
            }
        }
    }
    public String matrixToString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < numberOfNodes; i++) {
            for (int j = 0; j < numberOfNodes; j++) {
                if ((i < j) && (adjMatrix[i][j] == 1)) {
                    builder.append(i + 1).append(" ").append(j + 1).append(" \n");
                }
            }
        }

        return builder.toString();
    }

    public void reduceToTask2(Task task2) throws IOException, InterruptedException {
        generateComplementaryMatrix();

        for (int i = numberOfNodes; i >= 1; i--) {
            FileWriter writer = new FileWriter(task2InFilename);
            int fullNumber = numberOfNodes * (numberOfNodes - 1) /2 - numberOfEdges;
            writer.write(new StringBuilder().append(numberOfNodes).append(" ").append(fullNumber).append(" ").append(i)
                    .append(" \n").append(matrixToString()).toString());
            writer.close();
            task2.solve();
            
            Scanner scanner = new Scanner(new File(task2OutFilename));
            String firstLine = scanner.nextLine();

            if (firstLine.startsWith("True")) {
                break;
            }
        }

    }

    public void extractAnswerFromTask2() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(task2OutFilename));
        scanner.nextLine();
        StringBuilder builder = new StringBuilder();

        int []vec = new int[numberOfNodes];

        for (int i = 0; i < numberOfNodes; i++) {
            if (scanner.hasNext()) {
                int node = scanner.nextInt();
                vec[node - 1] = 1;
            }
        }

        for (int i = 0; i < numberOfNodes; i++) {
            if (vec[i] == 0) {
                builder.append(i + 1).append(" ");
            }
        }

        result = builder.append("\n").toString();
    }

    @Override
    public void writeAnswer() throws IOException {
        FileWriter writer = new FileWriter(outFilename);
        writer.write(result);
        writer.close();
    }
}
