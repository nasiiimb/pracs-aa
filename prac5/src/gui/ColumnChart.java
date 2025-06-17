package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * Simulates a column chart comparing one dictionary to all others.
 */
public class ColumnChart extends JPanel
{
    private final Map<String, Double> distances;
    private final String baseLanguage;
    private final int padding;
    private final int labelPadding;
    private final int barWidth;

    public ColumnChart(int width, int height, String baseLanguage, LinkedHashMap<String, Double> distances)
    {
        this.baseLanguage = baseLanguage;
        this.distances = distances;

        this.padding = Math.max(40, Math.min(width, height) / 10);

        int availableWidth = width - (2 * padding);
        this.barWidth = Math.max(20, Math.min(60, availableWidth / (distances.size() * 2)));

        this.labelPadding = Math.max(15, barWidth / 2);

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.WHITE);
    }


    @Override
    public void repaint()
    {
        if ((this.getGraphics() != null) && this.isDisplayable())
            this.paint(this.getGraphics());
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (distances.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Find max value for scaling
        double maxValue = distances.values().stream()
                .filter(v -> !v.equals(distances.get(baseLanguage)))
                .mapToDouble(Double::doubleValue)
                .max().orElse(1.0);

        // Draw axes
        g2.drawLine(padding, height - padding, width - padding, height - padding); // x-axis
        g2.drawLine(padding, padding, padding, height - padding);                  // y-axis

        // Y-axis label
        g2.drawString("Lexical Distance to " + baseLanguage,
                padding, padding-20);

        int x = padding + labelPadding;

        for (Map.Entry<String, Double> entry : distances.entrySet()) {
            String lang = entry.getKey();
            double dist = entry.getValue();

            // Skip the base language itself
            if (lang.equals(baseLanguage)) continue;

            // Scale bar height
            int barHeight = (int) ((height - 2 * padding) * (dist / maxValue));

            // Draw bar
            g2.setColor(new Color(100, 150, 220));
            int y = height - padding - barHeight;
            g2.fillRect(x, y, barWidth, barHeight);

            // Draw bar outline
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);

            // Draw language label
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(lang);
            g2.drawString(lang,
                    x + (barWidth - labelWidth) / 2,
                    height - padding + fm.getAscent() + 5);

            // Draw distance value above bar
            String valStr = String.format("%.4f", dist);
            int valWidth = fm.stringWidth(valStr);
            g2.drawString(valStr,
                    x + (barWidth - valWidth) / 2,
                    y - 5);

            x += barWidth + labelPadding;
        }
    }
}
