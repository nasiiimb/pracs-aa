package model;

import main.Notify;

public class Model {

    private final Notify notify;
    private Point[] points;
    private Point[] closest;
    private Point[] farthest;
    private int n = 500;
    private String distribution = "Uniforme";

    private double a_brute = 0.000001; // inicialització provisional
    private double b_fast = 0.000001;

    public Model(Notify notify) {
        this.notify = notify;
        estimateConstants();
    }

    private void estimateConstants() {
        int N = 10000;
        int iterations = 15;

        double[] bruteTimes = new double[iterations];
        double[] fastTimes = new double[iterations];

        for (int i = 0; i < iterations; i++) {
            Point[] sample = PointGenerator.generateUniform(N, 0, 1000);

            long start = System.currentTimeMillis();
            ClosestPairBruteForce.findClosestPair(sample);
            long end = System.currentTimeMillis();
            bruteTimes[i] = end - start;

            start = System.currentTimeMillis();
            ClosestPairDivideAndConquer.findClosestPair(sample);
            end = System.currentTimeMillis();
            fastTimes[i] = end - start;
        }

        double bruteSum = 0;
        double fastSum = 0;

        double minBrute = Double.MAX_VALUE, maxBrute = Double.MIN_VALUE;
        double minFast = Double.MAX_VALUE, maxFast = Double.MIN_VALUE;

        for (int i = 0; i < iterations; i++) {
            bruteSum += bruteTimes[i];
            fastSum += fastTimes[i];
            if (bruteTimes[i] < minBrute) minBrute = bruteTimes[i];
            if (bruteTimes[i] > maxBrute) maxBrute = bruteTimes[i];
            if (fastTimes[i] < minFast) minFast = fastTimes[i];
            if (fastTimes[i] > maxFast) maxFast = fastTimes[i];
        }

        bruteSum -= (minBrute + maxBrute);
        fastSum -= (minFast + maxFast);

        double avgBrute = bruteSum / (iterations - 2);
        double avgFast = fastSum / (iterations - 2);

        a_brute = avgBrute / (N * (double) N);
        b_fast = avgFast / (N * Math.log(N));

        System.out.printf("Constants estimades → a = %.10f | b = %.10f\n", a_brute, b_fast);
    }

    public void setConfig(int n, String dist) {
        this.n = n;
        this.distribution = dist;
    }

    public void generateAndCompute() {
        switch (distribution) {
            case "Uniforme" -> points = PointGenerator.generateUniform(n, 0, 1000);
            case "Normal" -> points = PointGenerator.generateNormal(n, 500, 200);
            case "Exponencial" -> points = PointGenerator.generateExponential(n, 0.02);
        }

        long startBrute = System.currentTimeMillis();
        Point[] brute = ClosestPairBruteForce.findClosestPair(points);
        long endBrute = System.currentTimeMillis();

        long startFast = System.currentTimeMillis();
        closest = ClosestPairDivideAndConquer.findClosestPair(points);
        long endFast = System.currentTimeMillis();

        long startFar = System.currentTimeMillis();
        farthest = FarthestPair.findFarthestPair(points);
        long endFar = System.currentTimeMillis();

        System.out.println("=== Comparació de temps per N = " + n + " ===");
        System.out.println("Brute Force: " + (endBrute - startBrute) + " ms");
        System.out.println("Divide & Venceràs: " + (endFast - startFast) + " ms");
        System.out.println("Parell llunyà: " + (endFar - startFar) + " ms\n");

        notify.notify(Notify.PROCESS_FINISHED);
    }

    public long estimateBruteForceTime(int n) {
        return Math.round(a_brute * n * n);
    }

    public long estimateDivideAndConquerTime(int n) {
        return Math.round(b_fast * n * Math.log(n));
    }

    public Point[] getPoints() {
        return points;
    }

    public Point[] getClosest() {
        return closest;
    }

    public Point[] getFarthest() {
        return farthest;
    }
}
