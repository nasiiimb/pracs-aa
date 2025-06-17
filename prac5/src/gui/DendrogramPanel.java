package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * Simulates a dendrogram of the model, displaying the comparisons between all pairs of dictionaries.
 */
public class DendrogramPanel extends JPanel
{
    private final Model model;

    private TreeNode root;
    private int width, height;
    private int padding = 20;
    private int labelPadding = 5;

    // computed scales
    private double xStep;    // vertical pixels per leaf
    private double yScale;   // pixels per unit of branch‐length

    public DendrogramPanel(int width, int height, Model model)
    {
        this.model = model;
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.WHITE);
    }

    private class TreeNode
    {
        TreeNode left, right;
        double length;     // branch length to children
        String label;      // only non‐null at leaves

        public TreeNode(String label) {
            this.label = label;
        }

        public TreeNode(TreeNode left, TreeNode right, double length) {
            this.left = left;
            this.right = right;
            this.length = length;
        }

        /** is this a leaf? */
        public boolean isLeaf() {
            return left == null && right == null;
        }

        private static int countLeaves(TreeNode node)
        {
            if (node.isLeaf()) return 1;
            return countLeaves(node.left) + countLeaves(node.right);
        }


        private static double getMaxDepth(TreeNode node)
        {
            if (node.isLeaf()) return 0;
            return node.length + Math.max(getMaxDepth(node.left), getMaxDepth(node.right));
        }

        private static ArrayList<TreeNode> getAllLeaves(TreeNode node)
        {
            ArrayList<TreeNode> leaves = new ArrayList<>();
            collectLeaves(node, leaves);
            return leaves;
        }

        private static void collectLeaves(TreeNode node, ArrayList<TreeNode> leaves)
        {
            if (node.isLeaf())
                leaves.add(node);
            else
            {
                if (node.left != null) collectLeaves(node.left, leaves);
                if (node.right != null) collectLeaves(node.right, leaves);
            }
        }

    }


    private TreeNode buildTreeFromComparisons(ArrayList<LanguageComparator> comparisons) {
        // First, create a list of leaf nodes for all unique languages
        ArrayList<TreeNode> nodes = new ArrayList<>();
        HashSet<String> languages = new HashSet<>();

        // Collect all unique languages
        for (LanguageComparator c : comparisons)
        {
            if (c.getDistance() > 0.0)
            {
                languages.add(c.getDictionary1());
                languages.add(c.getDictionary2());
            }
        }

        // Create leaf nodes for each language
        for (String lang : languages)
            nodes.add(new TreeNode(lang));

        if (nodes.isEmpty())
            return null;

        if (nodes.size() < 2)
            return nodes.getFirst();

        // While we have more than one node, find the closest pair and merge
        while (nodes.size() > 1) //2
        {
            double minDistance = Double.MAX_VALUE;
            int minI = 0;
            int minJ = 0;

            // Find the closest pair of nodes
            for (int i = 0; i < nodes.size(); i++)
            {
                for (int j = i + 1; j < nodes.size(); j++)
                {
                    double distance = findDistance(nodes.get(i), nodes.get(j), comparisons);
                    if (distance < minDistance)
                    {
                        minDistance = distance;
                        minI = i;
                        minJ = j;
                    }
                }
            }

            // Create a new internal node with the two closest nodes as children
            TreeNode newNode = new TreeNode(nodes.get(minI), nodes.get(minJ), minDistance);

            // Remove the two nodes and add the new one
            nodes.remove(Math.max(minI, minJ));
            nodes.remove(Math.min(minI, minJ));
            nodes.add(newNode);
        }

        // The last remaining node is the root
        return nodes.getFirst(); //return new TreeNode(nodes.get(0), nodes.get(1), 0);
    }

    private double findDistance(TreeNode node1, TreeNode node2, ArrayList<LanguageComparator> comparisons)
    {
        if (node1.isLeaf() && node2.isLeaf())
        {
            // Direct comparison between leaves
            for (LanguageComparator comp : comparisons)
            {
                if ((comp.getDictionary1().equals(node1.label) && comp.getDictionary2().equals(node2.label)) ||
                        (comp.getDictionary1().equals(node2.label) && comp.getDictionary2().equals(node1.label))) {
                    return comp.getDistance();
                }
            }
            return Double.MAX_VALUE;
        }

        // Get all leaves from both nodes
        ArrayList<TreeNode> leaves1 = TreeNode.getAllLeaves(node1);
        ArrayList<TreeNode> leaves2 = TreeNode.getAllLeaves(node2);

        // Calculate average distance between all pairs of leaves
        double totalDistance = 0;
        int comparisonsCount = 0;

        for (TreeNode leaf1 : leaves1)
        {
            for (TreeNode leaf2 : leaves2)
            {
                double distance = findDistance(leaf1, leaf2, comparisons);
                if (distance != Double.MAX_VALUE)
                {
                    totalDistance += distance;
                    comparisonsCount++;
                }
            }
        }

        return comparisonsCount > 0 ? totalDistance / comparisonsCount : Double.MAX_VALUE;
    }




    @Override
    public void repaint()
    {
        if (this.getGraphics() != null)
            this.paint(this.getGraphics());
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        ArrayList<LanguageComparator> comparisons = model.getComparisons();
        if (comparisons.isEmpty()) return;

        this.root = buildTreeFromComparisons(comparisons);
        if (root == null) return;

        int leafCount = TreeNode.countLeaves(root);
        double maxDepth = TreeNode.getMaxDepth(root);

        // Reserve space for labels at the bottom and top vertical line
        int labelHeight = g.getFontMetrics().getHeight();
        int topLineHeight = 20; // Height for the top vertical line
        int treeHeight = height - 2 * padding - labelHeight - labelPadding- topLineHeight;

        this.xStep = (width - 2.0 * padding) / leafCount;
        this.yScale = treeHeight / maxDepth;


        if (root == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // start drawing at left padding, vertical start = padding + half a yStep
        drawNode(g2, root, padding, padding);
    }


    /**
     * Recursively draw the node and its children.
     */
    private double drawNode(Graphics2D g2, TreeNode node, double x0, double y) {
        if (node.isLeaf()) {
            // leaf position: center of its slot
            double centerX = x0 + xStep / 2;

            // Draw a vertical line at the leaf
            int treeBottom = height - padding - labelPadding - g2.getFontMetrics().getHeight();
            g2.drawLine(
                    (int) centerX,
                    (int) y,
                    (int) centerX,
                    treeBottom
            );

            // draw label beneath the leaf point
            int textY = height - padding;
            String label = node.label;
            int labelWidth = g2.getFontMetrics().stringWidth(label);

            // Center the label under the leaf
            g2.drawString(label,
                    (int) (centerX - labelWidth/2),
                    textY);
            return centerX;
        }

        // Calculate the width needed for each subtree based on number of leaves
        int leftCount = TreeNode.countLeaves(node.left);
        int totalCount = TreeNode.countLeaves(node);

        // Calculate positions for children
        double yChild = y + node.length * yScale;

        // Calculate x positions ensuring proper spacing
        double leftXCenter = drawNode(g2, node.left, x0, yChild);
        double rightXCenter = drawNode(g2, node.right, x0 + leftCount * xStep, yChild);

        // Calculate parent position as the exact middle between children
        double parentX = (leftXCenter + rightXCenter) / 2.0;

        // Draw the connecting lines
        g2.drawLine((int) parentX, (int) y, (int) parentX, (int) yChild);  // vertical
        g2.drawLine((int) leftXCenter, (int) yChild, (int) rightXCenter, (int) yChild);  // horizontal

        return parentX;

    }
}
