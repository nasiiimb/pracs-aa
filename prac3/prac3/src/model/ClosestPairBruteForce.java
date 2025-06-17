package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ClosestPairBruteForce {

    private static class Worker implements Callable<Result> {
        private final Point[] points;
        private final int start;
        private final int end;

        public Worker(Point[] points, int start, int end) {
            this.points = points;
            this.start = start;
            this.end = end;
        }

        @Override
        public Result call() {
            double minDist = Double.POSITIVE_INFINITY;
            Point p1 = null, p2 = null;

            for (int i = start; i < end; i++) {
                for (int j = i + 1; j < points.length; j++) {
                    double d = points[i].distanceTo(points[j]);
                    if (d < minDist) {
                        minDist = d;
                        p1 = points[i];
                        p2 = points[j];
                    }
                }
            }
            return new Result(p1, p2, minDist);
        }
    }

    private static class Result {
        Point p1, p2;
        double dist;

        public Result(Point p1, Point p2, double dist) {
            this.p1 = p1;
            this.p2 = p2;
            this.dist = dist;
        }
    }

    public static Point[] findClosestPair(Point[] points) {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        List<Future<Result>> futures = new ArrayList<>();

        int step = points.length / cores;
        for (int i = 0; i < cores; i++) {
            int start = i * step;
            int end = (i == cores - 1) ? points.length : (i + 1) * step;
            futures.add(executor.submit(new Worker(points, start, end)));
        }

        Result best = null;
        for (Future<Result> f : futures) {
            try {
                Result r = f.get();
                if (best == null || r.dist < best.dist) {
                    best = r;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        return new Point[]{best.p1, best.p2};
    }
}
