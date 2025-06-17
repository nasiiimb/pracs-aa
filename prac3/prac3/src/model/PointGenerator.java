package model;

import java.util.Random;

public class PointGenerator {
    private static final Random random = new Random();

    public static Point[] generateUniform(int count, double min, double max) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            double x = min + (max - min) * random.nextDouble();
            double y = min + (max - min) * random.nextDouble();
            points[i] = new Point(x, y);
        }
        return points;
    }

    public static Point[] generateNormal(int count, double mean, double stdDev) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            double x = mean + stdDev * random.nextGaussian();
            double y = mean + stdDev * random.nextGaussian();
            points[i] = new Point(x, y);
        }
        return points;
    }

    public static Point[] generateExponential(int count, double lambda) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            double x = -Math.log(1 - random.nextDouble()) / lambda;
            double y = -Math.log(1 - random.nextDouble()) / lambda;
            points[i] = new Point(x, y);
        }
        return points;
    }
}
