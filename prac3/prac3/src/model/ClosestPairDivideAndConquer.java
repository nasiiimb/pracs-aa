package model;

import java.util.*;

public class ClosestPairDivideAndConquer {

    public static Point[] findClosestPair(Point[] points) {
        Point[] pointsSortedByX = points.clone();
        Arrays.sort(pointsSortedByX, Comparator.comparingDouble(p -> p.x));
        return closestPairRec(pointsSortedByX, 0, points.length - 1);
    }

    private static Point[] closestPairRec(Point[] pts, int l, int r) {
        if (r - l <= 3) {
            double minDist = Double.POSITIVE_INFINITY;
            Point p1 = null, p2 = null;
            for (int i = l; i <= r; i++) {
                for (int j = i + 1; j <= r; j++) {
                    double d = pts[i].distanceTo(pts[j]);
                    if (d < minDist) {
                        minDist = d;
                        p1 = pts[i];
                        p2 = pts[j];
                    }
                }
            }
            return new Point[]{p1, p2};
        }

        int mid = (l + r) / 2;
        Point[] leftPair = closestPairRec(pts, l, mid);
        Point[] rightPair = closestPairRec(pts, mid + 1, r);

        double dLeft = leftPair[0].distanceTo(leftPair[1]);
        double dRight = rightPair[0].distanceTo(rightPair[1]);
        double d = Math.min(dLeft, dRight);
        Point[] bestPair = dLeft <= dRight ? leftPair : rightPair;

        List<Point> strip = new ArrayList<>();
        for (int i = l; i <= r; i++) {
            if (Math.abs(pts[i].x - pts[mid].x) < d) {
                strip.add(pts[i]);
            }
        }
        strip.sort(Comparator.comparingDouble(p -> p.y));

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < d; j++) {
                double dist = strip.get(i).distanceTo(strip.get(j));
                if (dist < d) {
                    d = dist;
                    bestPair = new Point[]{strip.get(i), strip.get(j)};
                }
            }
        }
        return bestPair;
    }
}
