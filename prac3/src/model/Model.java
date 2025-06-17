package model;

import main.*;
import model.models.*;


/**
 * Handler of the different models available.
 */
public class Model implements Notify
{
    private final Controller controller;

    private Point[] points;
    private ClosestPairBruteForce closestBruteForce;
    private ClosestPairDivideAndConquer closestDivideAndConquer;
    private FarthestPair farthest;
    private int n = 500;
    private String distribution = Notify.UNIFORME;

    private double a_brute = 0.000001;
    private double b_fast = 0.000001;

    public Model(Controller controller)
    {
        this.controller = controller;
        estimateConstants();
    }

    private void estimateConstants()
    {
        int N = 10000;
        int iterations = 15;

        double[] bruteTimes = new double[iterations];
        double[] fastTimes = new double[iterations];

        for (int i = 0; i < iterations; i++)
        {
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

        for (int i = 0; i < iterations; i++)
        {
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


    public void setConfig(int n, String dist)
    {
        this.n = n;
        this.distribution = dist;
    }


    public long estimateBruteForceTime(int n) { return Math.round(a_brute * n * n); }

    public long estimateDivideAndConquerTime(int n) { return Math.round(b_fast * n * Math.log(n)); }

    public Point[] getPoints() { return points; }

    public Point[] getClosestBruteForce()
    {
        if (closestBruteForce == null) return null;
        return closestBruteForce.getClosestPoints();
    }

    public Point[] getClosestDivideAndConquer()
    {
        if (closestBruteForce == null) return null;
        return closestDivideAndConquer.getClosestPoints();
    }

    public Point[] getFarthest()
    {
        if (closestBruteForce == null) return null;
        return farthest.getFarthestPoints();
    }


    private void start()
    {
        System.out.println("\n\n=== Comparació de temps per N = " + n + " ===");
        switch (distribution)
        {
            case Notify.UNIFORME -> points = PointGenerator.generateUniform(n, 0, 1000);
            case Notify.NORMAL -> points = PointGenerator.generateNormal(n, 500, 200);
            case Notify.EXPONENCIAL -> points = PointGenerator.generateExponential(n, 0.02);
        }

        closestBruteForce = new ClosestPairBruteForce(controller, points);
        closestDivideAndConquer = new ClosestPairDivideAndConquer(controller, points);
        farthest = new FarthestPair(controller, points);
    }


    @Override
    public void notify(String s)
    {
        switch (s)
        {
            case START -> start();

            case STOP ->
            {
                closestBruteForce.stopModel();
                closestDivideAndConquer.stopModel();
                farthest.stopModel();
            }
        }
    }
}
