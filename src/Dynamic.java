import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Dynamic{
    CostMatrix costMatrix;
    double matrix[][];
    Path solutionTable[][];

    public static void main(String[] args) {

        boolean one = VerificationOne();
        boolean two = VerificationTwo();

        if (one == true) {
            System.out.println("Verification Test One Successful");
        }
        if (two == true){
            System.out.println("Verification Test Two Successful");
        }

        runFullExperiment("Dynamic-Exp1-ThrowAway.txt");
        runFullExperiment("Dynamic-Exp2.txt");
        runFullExperiment("Dynamic-Exp3.txt");
    }

    public static boolean VerificationOne()
    {
        //circular
        CostMatrix costMatrix = GenerateCircularGraphCostMatrix(12, 100);
        Path expectedPath = costMatrix.correctForCircular;
        Dynamic d = new Dynamic();
        Path actualPath = d.TSP(costMatrix);
        Boolean result = expectedPath.vertices.equals(actualPath.vertices);
        //Collections.reverse(expectedPath.vertices);
        //Boolean reversedCheck = expectedPath.vertices.equals(actualPath.vertices);

        return result;
    }

    public static boolean VerificationTwo()
    {
        //static matrix
        CostMatrix matrix = new CostMatrix(6);
        matrix.matrix[0][1] = 2;
        matrix.matrix[0][2] = 8;
        matrix.matrix[0][3] = 12;
        matrix.matrix[0][4] = 6;
        matrix.matrix[0][5] = 1;
        matrix.matrix[1][0] = 2;
        matrix.matrix[1][2] = 15;
        matrix.matrix[1][3] = 6;
        matrix.matrix[1][4] = 4;
        matrix.matrix[1][5] = 2;
        matrix.matrix[2][0] = 8;
        matrix.matrix[2][1] = 15;
        matrix.matrix[2][3] = 6;
        matrix.matrix[2][4] = 20;
        matrix.matrix[2][5] = 3;
        matrix.matrix[3][0] = 9;
        matrix.matrix[3][1] = 6;
        matrix.matrix[3][2] = 6;
        matrix.matrix[3][4] = 4;
        matrix.matrix[3][5] = 3;
        matrix.matrix[4][0] = 6;
        matrix.matrix[4][1] = 4;
        matrix.matrix[4][2] = 20;
        matrix.matrix[4][3] = 4;
        matrix.matrix[4][5] = 7;
        matrix.matrix[5][0] = 1;
        matrix.matrix[5][1] = 2;
        matrix.matrix[5][2] = 3;
        matrix.matrix[5][3] = 3;
        matrix.matrix[5][4] = 7;

        ArrayList<Integer> expectedPath = new ArrayList<>(Arrays.asList(0, 5, 2, 3, 4, 1, 0));
        ArrayList<Integer> reversedExpectedPath = new ArrayList<>(Arrays.asList(0, 1, 4, 3, 2, 5, 0));
        Dynamic   d = new Dynamic();
        Path actualPath = d.TSP(matrix);

        if (reversedExpectedPath == actualPath.vertices){
            return true;

        }
        else {
            return false;
        }
    }
    /* define constants */
    static int numberOfTrials = 15;
    private static final int MAXINPUTSIZE = 15;
    static String ResultsFolderPath = "/home/caitlin/Documents/Lab8/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;


    private static void runFullExperiment(String resultsFileName) {
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return;
        }

        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial
        double lastAverageTime = -1;
        double doublingRatio = 0;

        resultsWriter.println("#Number of Vertices  AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        for (int inputSize = 4; inputSize <= MAXINPUTSIZE; inputSize++) {
            System.out.println("Running test for string size " + inputSize + " ... ");
            System.out.print("    Running trial batch...");
            System.gc();
            long batchElapsedTime = 0;
            for (long trial = 0; trial < numberOfTrials; trial++) {
                System.out.print("    Generating test data...");
                CostMatrix testMatrix = GenerateRandomCostMatrix(inputSize, 100);
                Dynamic d = new Dynamic();
                System.gc();
                System.out.println("...done.");
                TrialStopwatch.start();
                Path p = d.TSP(testMatrix);
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();
                p.printPath();

                System.gc();
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
            if (lastAverageTime != -1) {
                doublingRatio = averageTimePerTrialInBatch / lastAverageTime;
            }
            lastAverageTime = averageTimePerTrialInBatch;

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f\n", inputSize, averageTimePerTrialInBatch);
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static CostMatrix GenerateRandomCostMatrix(int vertices, int maxEdgeCost )
    {
        CostMatrix matrix = new CostMatrix(vertices);
        matrix.fillRandomMatrix(maxEdgeCost);
        return matrix;
    }

    public static CostMatrix GenerateCircularGraphCostMatrix(int vertices, int radius )
    {
        CostMatrix matrix = new CostMatrix(vertices);
        matrix.fillCircularMatrix(radius);
        return matrix;
    }

    public Path TSP(CostMatrix costMatrix)
    {
        this.costMatrix = costMatrix;
        this.matrix = costMatrix.matrix;
        solutionTable = new Path[costMatrix.numberVertices][(int)Math.pow(2,costMatrix.numberVertices)];


        ArrayList<Integer> tourNodes = new ArrayList<>();
        for(int i = 1; i< costMatrix.numberVertices; i++)
        {
            tourNodes.add(i);
        }

        Path path = recursiveTSP(0,tourNodes);

        path.vertices.add(0);
        path.vertices.add(0,0);
        return path;
    }

    Path recursiveTSP(int startNode, ArrayList<Integer> tourNodes) {

        if (solutionTable[startNode][setToInteger(tourNodes)] != null) {
            Path path = solutionTable[startNode][setToInteger(tourNodes)];
            return new Path((ArrayList<Integer>) path.vertices.clone(), path.cost);
        }
        if (tourNodes.size() == 1) {

            double cost = matrix[startNode][tourNodes.get(0)] + matrix[tourNodes.get(0)][0];
            solutionTable[startNode][setToInteger(tourNodes)] = new Path(tourNodes,cost);
            return new Path(tourNodes,cost);
        }
        else {
            Path bestSoFar = new Path();
            bestSoFar.cost = Double.POSITIVE_INFINITY;
            for (Integer node : tourNodes)
            {
                ArrayList<Integer> restOfTheNodes = (ArrayList<Integer>) tourNodes.clone();
                restOfTheNodes.remove(node);
                Path tempPath = recursiveTSP(node, restOfTheNodes);
                tempPath.cost += matrix[startNode][node];

                if (tempPath.cost < bestSoFar.cost)
                {
                    bestSoFar.vertices = (ArrayList<Integer>) tempPath.vertices.clone();
                    bestSoFar.vertices.add(0,node);
                    bestSoFar.cost = tempPath.cost;
                }
            }

            solutionTable[startNode][setToInteger(tourNodes)] = bestSoFar;
            return new Path(bestSoFar.vertices, bestSoFar.cost);
        }
    }

    static int setToInteger(ArrayList<Integer> set)
    {
        int number = 0;
        for(Integer node:set)
        {
            number += Math.pow(2, node-1);
        }
        return number;
    }
}