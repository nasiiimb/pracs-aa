package gui;

import model.Point;

import javax.swing.*;
import java.awt.*;

public class PointPanel extends JPanel {

    private Point[] points = new Point[0];
    private Point[] closest = null;
    private Point[] farthest = null;

    public void setPoints(Point[] points) {
        this.points = points;
        repaint();
    }

    public void setClosestPair(Point p1, Point p2) {
        this.closest = new Point[]{p1, p2};
        repaint();
    }

    public void setFarthestPair(Point p1, Point p2) {
        this.farthest = new Point[]{p1, p2};
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (points == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double margin = 40;
        double width = getWidth() - 2 * margin;
        double height = getHeight() - 2 * margin;

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (Point p : points) {
            if (p.x < minX) minX = p.x;
            if (p.x > maxX) maxX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.y > maxY) maxY = p.y;
        }
        double scaleX = width / (maxX - minX + 1);
        double scaleY = height / (maxY - minY + 1);

        // Dibuixa tots els punts
        g2.setColor(Color.BLACK);
        for (Point p : points) {
            int x = (int) (margin + (p.x - minX) * scaleX);
            int y = (int) (getHeight() - margin - (p.y - minY) * scaleY);
            g2.fillOval(x - 3, y - 3, 6, 6);
        }

        // Parella més propera
        if (closest != null) {
            g2.setColor(Color.GREEN);
            drawLine(g2, closest[0], closest[1], margin, minX, minY, scaleX, scaleY);
        }

        // Parella més llunyana
        if (farthest != null) {
            g2.setColor(Color.RED);
            drawLine(g2, farthest[0], farthest[1], margin, minX, minY, scaleX, scaleY);
        }
    }

    private void drawLine(Graphics2D g2, Point a, Point b, double margin,
                          double minX, double minY, double scaleX, double scaleY) {
        int x1 = (int) (margin + (a.x - minX) * scaleX);
        int y1 = (int) (getHeight() - margin - (a.y - minY) * scaleY);
        int x2 = (int) (margin + (b.x - minX) * scaleX);
        int y2 = (int) (getHeight() - margin - (b.y - minY) * scaleY);
        g2.drawLine(x1, y1, x2, y2);
    }
}
