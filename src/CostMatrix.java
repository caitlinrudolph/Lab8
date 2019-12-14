import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CostMatrix {

    int numberVertices;
    public double matrix[][];
    Point[] vertices;
    Path correctForCircular;

    public CostMatrix(int numberVertices) {
        this.numberVertices = numberVertices;
        matrix = new double[numberVertices][numberVertices];
    }

    public void fillRandomMatrix(int maxEdgeCost)
    {
        for(int i = 0; i<numberVertices; i++)
        {
            for (int j = i+1; j < numberVertices; j++)
            {
                double rand = randomInteger(1, maxEdgeCost);
                matrix[i][j] = rand;
                matrix[j][i] = rand;
            }
        }
    }

    public static int randomInteger(int min, int max)
    {
        return (int) (min + Math.random()*(max-min));
    }

    public void fillEuclideanMatrix(int maxCoordinate)
    {
        vertices = new Point[numberVertices];
        for (int i = 0; i<numberVertices; i++)
        {
            vertices[i] = new Point(randomInteger(0, maxCoordinate), randomInteger(0, maxCoordinate));
        }
        pointToPointDistancesToMatrix();
    }

    private void pointToPointDistancesToMatrix() {
        for(int i = 0; i<numberVertices; i++)
        {
            for (int j = i+1; j < numberVertices; j++)
            {
                double distance = vertices[i].distance(vertices[j]);
                matrix[i][j] = Math.floor(distance*100)/100;
                matrix[j][i] = Math.floor(distance*100)/100;
            }
        }
    }

    public void fillCircularMatrix(int radius)
    {
        double angle = 360/numberVertices;
        double currentAngle = 0;
        ArrayList<Vertex> sortedVertices = new ArrayList<>();
        for (int i = 0; i<numberVertices; i++)
        {
            double radian = currentAngle*Math.PI/180;
            double x = Math.cos(radian)*radius;
            double y = Math.sin(radian)*radius;
            System.out.println("Vertex " + i + ": (" + Math.round(x) + ", " + Math.round(y) +")");
            sortedVertices.add(new Vertex((int) Math.round(x), (int)Math.round(y), i));
            currentAngle += angle;
        }
        Collections.shuffle(sortedVertices);
        vertices = new Point[numberVertices];
        correctForCircular = new Path(numberVertices);
        for(int i = 0; i<numberVertices; i++)
        {
            vertices[i] = sortedVertices.get(i).getPoint();
            int number = sortedVertices.get(i).getNumber();
            correctForCircular.vertices.set(number, i);
        }
        pointToPointDistancesToMatrix();
        correctForCircular.rotatePath();
        correctForCircular.calculatePathCost(matrix);
    }

    public void printMatrix()
    {
        System.out.print("   ");
        for(int i = 0; i< numberVertices; i++)
        {
            System.out.printf("%5d   ", i);
        }
        System.out.println();
        String line = "-";
        System.out.println(line.repeat(numberVertices*8));
        for (int i = 0; i< numberVertices; i++)
        {
            System.out.printf("%2d| ", i);
            for(int j = 0; j<numberVertices;j++)
            {
                System.out.printf(" %05.1f |", matrix[i][j]);
            }
            System.out.println();
        }
    }
    public class Vertex extends Point {

        int number;
        public Vertex(int x, int y, int number) {
            super(x, y);
            this.number = number;
        }
        public int getNumber() {
            return number;
        }
        public Point getPoint() {
            return super.getLocation();
        }
    }
}
