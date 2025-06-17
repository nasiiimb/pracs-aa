package test;

import model.FarthestPair;
import model.Point;
import model.PointGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FarthestPairTest {

    public static void main(String[] args) {
        runTest("Triangle equilater", new Point[]{
                new Point(0, 0),
                new Point(1, Math.sqrt(3)),
                new Point(2, 0)
        });

        runTest("Repetits", new Point[]{
                new Point(1, 1),
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3)
        });

        runTest("Línia recta", new Point[]{
                new Point(0, 0),
                new Point(10, 0),
                new Point(20, 0),
                new Point(30, 0)
        });

        runTest("Desbalancejats (grans)", new Point[]{
                new Point(-1e9, -1e9),
                new Point(1e9, 1e9),
                new Point(0, 0)
        });

        runTest("Aleatori 1000 punts (uniforme)", PointGenerator.generateUniform(1000, 0, 10000));
        runTest("Aleatori 5000 punts (normal)", PointGenerator.generateNormal(5000, 5000, 2000));

        runControlledTest("Punts compactes + extrems assegurats", 10000);

        System.out.println("✅ Tots els tests han passat!");
    }

    private static void runTest(String name, Point[] points) {
        Point[] real = FarthestPair.findFarthestPair(points);
        Point[] brute = bruteForceFarthest(points);

        double d1 = real[0].distanceTo(real[1]);
        double d2 = brute[0].distanceTo(brute[1]);

        assert Math.abs(d1 - d2) < 1e-6 : "❌ Test '" + name + "' ha fallat:\n"
                + "Farthest dist real = " + d1 + ", dist brute = " + d2;
    }

    private static Point[] bruteForceFarthest(Point[] points) {
        double maxDist = -1;
        Point p1 = null, p2 = null;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double d = points[i].distanceTo(points[j]);
                if (d > maxDist) {
                    maxDist = d;
                    p1 = points[i];
                    p2 = points[j];
                }
            }
        }
        return new Point[]{p1, p2};
    }

    private static void runControlledTest(String name, int numRandomPoints) {
        System.out.println("=== Test: " + name + " ===");

        List<Point> points = new ArrayList<>();
        Random rand = new Random(42); // Per fer-ho reproductible

        // Punts aleatoris dins una àrea petita
        for (int i = 0; i < numRandomPoints; i++) {
            double x = rand.nextDouble() * 1000 + 1000; // [1000, 2000]
            double y = rand.nextDouble() * 1000 + 1000;
            points.add(new Point(x, y));
        }

        // Dos punts molt allunyats
        Point pFar1 = new Point(-1e6, -1e6);
        Point pFar2 = new Point(1e6, 1e6);
        points.add(pFar1);
        points.add(pFar2);

        Point[] input = points.toArray(new Point[0]);
        Point[] result = FarthestPair.findFarthestPair(input);
        double d = result[0].distanceTo(result[1]);
        double expected = pFar1.distanceTo(pFar2);

        System.out.printf("Parell retornat: %s <-> %s (%.2f)\n", result[0], result[1], d);
        System.out.printf("Esperada: %s <-> %s (%.2f)\n", pFar1, pFar2, expected);

        assert isSamePair(result, pFar1, pFar2) : "❌ La parella retornada no és la correcta!";
    }

    private static boolean isSamePair(Point[] result, Point a, Point b) {
        return (equals(result[0], a) && equals(result[1], b)) ||
                (equals(result[0], b) && equals(result[1], a));
    }

    private static boolean equals(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) < 1e-6 && Math.abs(p1.y - p2.y) < 1e-6;
    }
}
