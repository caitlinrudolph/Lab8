import java.util.ArrayList;
import java.util.Collections;

public class Path{

    ArrayList<Integer> vertices;
    public double cost;

    public Path()
    {
        vertices = new ArrayList<>();
    }

    public Path(int numberVertices)
    {
        vertices = new ArrayList<>();
        for(Integer i = 0; i<numberVertices; i++)
        {
            vertices.add(i);
        }
    }

    public Path(ArrayList<Integer> vertices, double cost) {
        this.vertices = vertices;
        this.cost = cost;
    }

    public void printPath() {
        System.out.print("{ " + vertices.get(0));
        for (int i = 1; i < vertices.size(); i++)
        {
            System.out.print(", " + vertices.get(i));
        }
        System.out.println(" } Cost = " + cost);
    }

    //  Rotates the path so that it begins and ends at 0
    public void rotatePath()
    {
        int locationOfZero = vertices.indexOf(0);
        Collections.rotate(vertices, locationOfZero*(-1));
        vertices.add(0);
    }

    public double calculatePathCost(double[][] matrix) {
        double cost = 0;
        for (int i = 0; i<vertices.size()-1; i++)
        {
            cost += matrix[vertices.get(i)][vertices.get(i+1)];
        }
        this.cost = Math.floor(cost*100)/100;
        return this.cost;
    }
}