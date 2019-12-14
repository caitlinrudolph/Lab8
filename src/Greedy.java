import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Greedy {

    /* define constants */
    static int numberOfTrials = 15;
    private static final int MAXINPUTSIZE = 10;
    static String ResultsFolderPath = "/home/caitlin/Documents/Lab8/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {
        boolean one = VerificationOne();
        boolean two = VerificationTwo();

        if (one == true) {
            System.out.println("Verification Test One Successful");
        }
        if(two==true) {
            System.out.println("Verification Test Two Successful");
        }
        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("Greedy-Exp1-ThrowAway.txt");
        runFullExperiment("Greedy-Exp2.txt");
        runFullExperiment("Greedy-Exp3.txt");

    }

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

        resultsWriter.println("#Number of Vertices  AverageTime  Solution Quality Ratio"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        for (int inputSize = 4; inputSize <= MAXINPUTSIZE; inputSize++) {
            System.out.println("Running test for string size " + inputSize + " ... ");
            System.out.print("    Running trial batch...");
            System.gc();
            long batchElapsedTime = 0;
            double qualityRatio = 0;
            for (long trial = 0; trial < numberOfTrials; trial++) {
                System.out.print("    Generating test data...");
                CostMatrix testMatrix = GenerateRandomCostMatrix(inputSize, 100);
                Greedy g = new Greedy();
                System.gc();
                System.out.println("...done.");
                TrialStopwatch.start();
                Path testPath = BruteForce.TSP(testMatrix);
                Path exactPath = g.TSP(testMatrix);
                qualityRatio += testPath.cost / exactPath.cost;
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();
                testPath.printPath();
                exactPath.printPath();
                System.gc();
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
            double SQR = qualityRatio/(double)numberOfTrials;
            if (lastAverageTime != -1) {
                doublingRatio = averageTimePerTrialInBatch / lastAverageTime;
            }
            lastAverageTime = averageTimePerTrialInBatch;

            /* print data for this size of input */
            resultsWriter.printf("%12d  %5.2f %15.2f\n", inputSize, SQR, averageTimePerTrialInBatch);
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
    public static boolean VerificationOne()
    {
        //circular
        CostMatrix costMatrix = GenerateCircularGraphCostMatrix(40, 100);
        Path expectedPath = costMatrix.correctForCircular;
        Path actualPath = Greedy.TSP(costMatrix);
        Boolean result     = expectedPath.vertices.equals(actualPath.vertices);
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

        ArrayList<Integer> expectedPath = new ArrayList<>(Arrays.asList(0, 5, 1, 4, 3, 2, 0));
        if (expectedPath == Greedy.TSP(matrix).vertices){
            return true;
        }
        else
        {
            return false;
        }


    }
    public static Path TSP(CostMatrix costMatrix)
    {
        Path path = new Path();

        ArrayList<Integer> toVisit = new ArrayList<>();
        for(int i = 1; i<costMatrix.numberVertices; i++)
        {
            toVisit.add(i);
        }
        int currentVertex = 0;
        path.vertices.add(currentVertex);
        for( int i = 1; i < costMatrix.numberVertices; i++)
        {
            int indexOfNext = 0;
            int nextVertex = toVisit.get(indexOfNext);
            for(int j = 1; j<toVisit.size(); j++)
            {
                if(costMatrix.matrix[currentVertex][toVisit.get(j)] < costMatrix.matrix[currentVertex][nextVertex])
                {
                    indexOfNext = j;
                    nextVertex = toVisit.get(j);
                }
            }
            path.vertices.add(nextVertex);
            path.cost += costMatrix.matrix[currentVertex][nextVertex];
            toVisit.remove(indexOfNext);
            currentVertex = nextVertex;
        }
        path.vertices.add(0);
        path.cost += costMatrix.matrix[currentVertex][0];

        return path;
    }








}

