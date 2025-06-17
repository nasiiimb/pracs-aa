package gui;

import model.Model;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;


/**
 * Simulates the graphic output of the model, displaying statistics.
 */
public class Stats extends JPanel
{
    private final Model model;
    private final DecimalFormat df = new DecimalFormat("#0.00");

    public Stats(int width, int height, Model model)
    {
        this.setPreferredSize(new Dimension(width, height));
        this.model = model;
        this.setBackground(Color.WHITE);
    }


    @Override
    public void repaint()
    {
        if (this.isShowing())
            super.repaint();
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 18));

        int y = this.getHeight()/2-100;
        int x = 25;
        int lineHeight = 20;

        g2d.drawString("Compression Statistics:", x, y);
        y += (int) (lineHeight * 1.5);

        g2d.drawString("Original Size: " + formatFileSize(model.getOriginalFileSize()), x, y);
        y += lineHeight;

        g2d.drawString("Compressed Size: " + formatFileSize(model.getCompressedFileSize()), x, y);
        y += lineHeight;

        g2d.drawString("Compression: " + df.format(model.getCompressionPercentage()) + " %", x, y);
        y += lineHeight;

        g2d.drawString("Mean Code Length: " + df.format(model.getMeanCodeLength()) + " bits/char", x, y);
        y += lineHeight;

        g2d.drawString("Execution Time: " + model.getFormattedExecutionTime(), x, y);
    }

    private String formatFileSize(long size)
    {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }
}