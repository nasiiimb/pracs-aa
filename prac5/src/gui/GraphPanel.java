package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * Simulates a graphic output of the model, displaying a graph of the comparisons between all pairs of dictionaries.
 */
public class GraphPanel extends JPanel
{
    private final Model model;

    private final int width, height;

    private static final int R = 20; // radius of each node circle

    public GraphPanel(int width, int height, Model model)
    {
        this.width = width;
        this.height = height;
        this.model = model;

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.WHITE);
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        ArrayList<LanguageComparator> comparisons = model.getComparisons();
        if (comparisons.isEmpty()) return;

        // 2) build unique nodes
        Map<String,Node> nodeMap = new LinkedHashMap<>();
        for (LanguageComparator c : comparisons)
        {
            if (c.getDistance() > 0.0)
            {
                nodeMap.computeIfAbsent(c.getDictionary1(), Node::new);
                nodeMap.computeIfAbsent(c.getDictionary2(), Node::new);
            }
        }
        ArrayList<Node> nodes = new ArrayList<>(nodeMap.values());

        // 3) build edges
        ArrayList<Edge> edges = new ArrayList<>();
        for (LanguageComparator c : comparisons)
        {
            Node na = nodeMap.get(c.getDictionary1());
            Node nb = nodeMap.get(c.getDictionary2());
            double d = c.getDistance();
            if (d > 0.0)
                edges.add(new Edge(na, nb, d));
        }

        circularLayout(nodes, width, height);


        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1) draw edges first
        g2.setStroke(new BasicStroke(2));
        for (Edge e : edges)
        {
            int x1 = e.from.x, y1 = e.from.y;
            int x2 = e.to.x,   y2 = e.to.y;
            g2.drawLine(x1, y1, x2, y2);

            // Calculate edge direction vector
            double dx = x2 - x1;
            double dy = y2 - y1;
            double len = Math.sqrt(dx*dx + dy*dy);
            // Normalize and get perpendicular vector for offset
            double offsetX = -dy/len * 15; // Offset perpendicular to the edge
            double offsetY = dx/len * 15;

            // draw weight at midpoint with offset
            int mx = (x1 + x2)/2 + (int)offsetX;
            int my = (y1 + y2)/2 + (int)offsetY;
            String s = String.format("%.4f", e.weight);
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(s);
            int sh = fm.getAscent();

            // Draw white background for edge label
            g2.setColor(Color.WHITE);
            g2.fillRect(mx - sw/2 - 2, my - sh - 2, sw + 4, sh + 4);

            g2.setColor(Color.BLUE);
            g2.drawString(s, mx - sw/2, my);
            g2.setColor(Color.BLACK);
        }

        // 2) draw nodes on top
        for (Node n : nodes)
        {
            int x = n.x - R, y = n.y - R;
            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, 2*R, 2*R);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, 2*R, 2*R);

            // draw label with white background
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(n.id);
            int sh = fm.getAscent();

            // Draw white background for node label
            g2.setColor(Color.WHITE);
            g2.fillRect(n.x - sw/2 - 2, n.y + sh/2 - sh - 2, sw + 4, sh + 4);

            g2.setColor(Color.BLACK);
            g2.drawString(n.id, n.x - sw/2, n.y + sh/2);
        }
    }


    private class Node
    {
        String id;
        int x, y;
        Node(String id) { this.id = id; }
    }

    private class Edge
    {
        Node from, to;
        double weight;
        Edge(Node from, Node to, double weight)
        {
            this.from = from;
            this.to   = to;
            this.weight = weight;
        }
    }

    // --- Layout helper: place all nodes evenly on a circle ---
    private void circularLayout(Collection<Node> nodes, int width, int height)
    {
        int n = nodes.size();
        double cx = width  / 2.0;
        double cy = height / 2.0;
        double r  = Math.min(width, height) * 0.4;
        int i = 0;
        for (Node node : nodes) {
            double theta = 2*Math.PI * i++ / n;
            node.x = (int)Math.round(cx + r*Math.cos(theta));
            node.y = (int)Math.round(cy + r*Math.sin(theta));
        }
    }
}
