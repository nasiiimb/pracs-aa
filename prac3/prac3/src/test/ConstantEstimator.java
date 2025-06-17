package test;

import model.*;

public class ConstantEstimator {

    public static void main(String[] args) {
        int[] sizes = {1000, 2000, 4000, 8000, 16000};
        int iterations = 5;

        System.out.println("Estimant constants per cada algorisme:\n");

        for (int N : sizes) {
            double totalTimeBrute = 0;
            double totalTimeFast = 0;

            for (int i = 0; i < iterations; i++) {
                Point[] points = PointGenerator.generateUniform(N, 0, 1000);

                // Brute Force (paral·lel)
                long start = System.currentTimeMillis();
                ClosestPairBruteForce.findClosestPair(points);
                long end = System.currentTimeMillis();
                totalTimeBrute += (end - start);

                // Divide & Conquer
                start = System.currentTimeMillis();
                ClosestPairDivideAndConquer.findClosestPair(points);
                end = System.currentTimeMillis();
                totalTimeFast += (end - start);
            }

            double avgTimeBrute = totalTimeBrute / iterations;
            double avgTimeFast = totalTimeFast / iterations;

            double a = avgTimeBrute / (N * (double)N);
            double b = avgTimeFast / (N * Math.log(N));

            System.out.printf("N = %d\n", N);
            System.out.printf("  Brute Force: T(N) ≈ a·N^2, a = %.10f\n", a);
            System.out.printf("  Divide & Venceràs: T(N) ≈ b·NlogN, b = %.10f\n\n", b);
        }
    }
}
