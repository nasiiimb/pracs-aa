package test;

import model.ClosestPairBruteForce;
import model.ClosestPairDivideAndConquer;
import model.Point;
import model.PointGenerator;

import java.util.Random;

public class ClosestPairTest {

    public static void main(String[] args) {
        runTest("Triangle equilater", new Point[]{
                new Point(0, 0),
                new Point(1, Math.sqrt(3)),
                new Point(2, 0)
        });

        runTest("Punts molt junts", new Point[]{
                new Point(0, 0),
                new Point(0.000001, 0.000001),
                new Point(1000, 1000)
        });

        runTest("Repetits", new Point[]{
                new Point(1, 1),
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3)
        });

        runTest("Coord. molt grans", new Point[]{
                new Point(1e9, 1e9),
                new Point(-1e9, -1e9),
                new Point(5e8, -1e9)
        });

        runTest("Aleatori 1000 punts", PointGenerator.generateUniform(1000, 0, 10000));

        runTest("Aleatori 5000 punts", PointGenerator.generateNormal(5000, 5000, 1000));

        System.out.println("✅ Tots els tests han passat!");
    }

    private static void runTest(String name, Point[] points) {
        Point[] brute = ClosestPairBruteForce.findClosestPair(points);
        Point[] fast = ClosestPairDivideAndConquer.findClosestPair(points);

        double d1 = brute[0].distanceTo(brute[1]);
        double d2 = fast[0].distanceTo(fast[1]);

        assert Math.abs(d1 - d2) < 1e-6 : "❌ Test '" + name + "' ha fallat:\n"
                + "Brute dist = " + d1 + ", Divide dist = " + d2;
    }
}
