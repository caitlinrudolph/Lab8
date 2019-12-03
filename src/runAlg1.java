import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;

public class runAlg1 {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    /* define constants */
    static int numberOfTrials = 50;

    static int numVerticies = 20;
    static int maxX = 50;
    static int maxY = 50;
    static int maxEdgeCost = 60;
    static int N = 20;
    static int radius = 30;

    static String ResultsFolderPath = "/home/caitlin/Documents/Lab8/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        //testing
        GenerateRandomCostMatrix(numVerticies, maxEdgeCost);
        GenerateRandomEuclideanCostMatrix(numVerticies, maxX, maxY);
        GenerateRandomCircularGraphCostMatrix(numVerticies, N, radius);

        //direct the verification test results to file
        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        System.out.println("Running first full experiment...");
        runFullExperiment("fibFormula-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("fibFormula-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("fibFormula-Exp3.txt");

    }

    static int[][] GenerateRandomCostMatrix(int namVertices, int maxEdgeCost)
    {
        //populates a 2D array with random edge costs from 0 to maxEdgeCost
        //must be symmetric - CostMatrix[i][j] must equal CostMatrix[j][i]
        //Also keep in mind the main diagonal would be self-edges and thus irrelevant/unused (just set them to zero).
        int costMatrix[][] = new int[namVertices + 1][namVertices + 1];

        for (int i = 1; i <= namVertices; i++)
        {
            for (int j = 1; j <= namVertices; j++)
            {
                costMatrix[i][j] = new Random().nextInt(maxEdgeCost + 1);
            }
        }
        for (int i = 1; i <= namVertices; i++)
        {
            for (int j = 1; j <= namVertices; j++)
            {
                if (costMatrix[i][j] == 1 && costMatrix[j][i] == 0)
                {
                    costMatrix[j][i] = 1;
                }
            }
        }
        return costMatrix;
    }

    static void GenerateRandomEuclideanCostMatrix( int numVerticies, int maxX, int maxY){
        //generate random x, y coordinates for each vertex

        //populate the cost matrix by calculating the distance between each pair of points
    }

    static void GenerateRandomCircularGraphCostMatrix(int numVerticies, int N, int radius)
    {
        //generate the x,y coordinates of each vertex at equally spaced angles around a circle of the given radius

        //then populate the cost matrix by calculating the distance between each pair of points

        //the sequence of verticies around the circle should also be random
    }


    static void runFullExperiment(String resultsFileName) {
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#X(value)    N(size)    T(time)"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize++) {
            // progress message...
            System.out.println("Running test for input size " + inputSize + " ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            // generate a list of randomly spaced integers in ascending sorted order to use as test input
            // In this case we're generating one list to use for the entire set of trials (of a given input size)
            // but we will randomly generate the search key for each trial

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();

            TrialStopwatch.start(); // *** uncomment this line if timing trials individually
            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                fibFormula(inputSize);
            }

            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %12d  %15.2f \n", inputSize, Long.toBinaryString(inputSize).length(), averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }
}
