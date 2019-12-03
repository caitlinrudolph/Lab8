import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class BruteForce
{
    private int numberOfNodes;
    private Stack<Integer> stack;

    public BruteForce()
    {
        stack = new Stack<Integer>();
    }

    public void tsp(int costMatrix[][])
    {
        numberOfNodes = costMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
        System.out.print(1 + "\t");

        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = 1;
            min = Integer.MAX_VALUE;
            while (i <= numberOfNodes)
            {
                if (costMatrix[element][i] > 1 && visited[i] == 0)
                {
                    if (min > costMatrix[element][i])
                    {
                        min = costMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
                System.out.print(dst + "\t");
                minFlag = false;
                continue;
            }
            stack.pop();
        }
    }
}
