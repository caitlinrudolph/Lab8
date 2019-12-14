import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BruteForce {

    /* define constants */
    static int numberOfTrials = 10;
    private static final int MAXINPUTSIZE = 10;
    static String ResultsFolderPath = "/home/caitlin/Documents/Lab8/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        boolean one = VerificationOne();
        boolean two = VerificationTwo();

        if (one == true){
            System.out.println("Verification Test One Successful");
        }
        if (two == true)
        {
            System.out.println("Verification Test Two Successful");
        }

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("BruteForce-Exp1-ThrowAway.txt");
        runFullExperiment("BruteForce-Exp2.txt");
        runFullExperiment("BruteForce-Exp3.txt");
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

        resultsWriter.println("#Number of Vertices  AverageTime "); // # marks a comment in gnuplot data
        resultsWriter.flush();

        for (int inputSize = 4; inputSize <= MAXINPUTSIZE; inputSize++) {
            System.out.println("Running test for string size " + inputSize + " ... ");
            System.out.print("    Running trial batch...");
            System.gc();
            long batchElapsedTime = 0;
            for (long trial = 0; trial < numberOfTrials; trial++) {
                System.out.print("    Generating test data...");
                CostMatrix testMatrix = GenerateRandomCostMatrix(inputSize, 100);
                BruteForce b = new BruteForce();
                System.gc();
                System.out.println("...done.");
                TrialStopwatch.start();
                Path p = b.TSP(testMatrix);
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


     public  static boolean VerificationOne()
     {
         //circular
         CostMatrix costMatrix = GenerateCircularGraphCostMatrix(12, 100);
         Path expectedPath = costMatrix.correctForCircular;
         Path actualPath = BruteForce.TSP(costMatrix);

         Boolean result = expectedPath.vertices.equals(actualPath.vertices);

         return result;
     }

     public static boolean VerificationTwo()
     { //static matrix
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
         Path actualPath = BruteForce.TSP(matrix);

         //reversedExpectedPath.equals(actualPath.vertices));
         Boolean result = expectedPath.equals(actualPath.vertices);

         return result;
     }



    public static Path TSP(CostMatrix costMatrix)
    {
        int[] path = new int[costMatrix.numberVertices+1];
        for(int i = 0; i< costMatrix.numberVertices; i++)
        {
            path[i] = i;
        }
        path[costMatrix.numberVertices] = 0;
        int[] currentShortest = path.clone();
        double cost = calculateArrayPathCost(path, costMatrix);
        int[] indexes = new int[costMatrix.numberVertices];
        Arrays.fill(indexes, 1);
        int i = 0;
        while (i < costMatrix.numberVertices)
        {
            if (indexes[i] < i) {
                swap(path, i % 2 != 0 ?  1: indexes[i], i);
                if (calculateArrayPathCost(path, costMatrix) < cost)
                {
                    currentShortest = path.clone();
                    cost = calculateArrayPathCost(path, costMatrix);
                }
                indexes[i]++;
                i = 0;
            }
            else {
                indexes[i] = 1;
                i++;
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        for(int j = 0; j < currentShortest.length; j++)
        {
            list.add( currentShortest[j]);
        }
        return new Path(list, cost);
    }

    private static double calculateArrayPathCost(int[] path, CostMatrix costMatrix) {
        double cost = 0;
        for ( int i = 0; i<path.length-1; i++)
        {
            cost += costMatrix.matrix[path[i]][path[i+1]];
        }
        return cost;
    }

    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

}