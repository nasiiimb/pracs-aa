package gui;

import model.Model;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * Simulates a column chart comparing one dictionary to all others.
 */
public class ColumnChart extends JPanel
{
    private final Model model;

    private final int width, height;

    public ColumnChart(int width, int height, Model model)
    {
        this.width = width;
        this.height = height;
        this.model = model;

        this.setPreferredSize(new Dimension(width, height));
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

        HashMap<String, Integer> distances = model.getColorCount();
        if ((distances == null) || distances.isEmpty()) return;

        int padding = Math.max(40, Math.min(width, height) / 10);
        int availableWidth = width - (2 * padding);

        int totalBars = 10;
        int barWidth = availableWidth / (totalBars + 1);
        int spacing = barWidth / totalBars;


        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int maxValue = distances.values().stream().max(Comparator.naturalOrder()).orElse(0);
        double maxLog = Math.log(maxValue + 1);

        g2.setColor(Color.BLACK);
        g2.drawLine(padding, height - padding, width - padding, height - padding);
        g2.drawLine(padding, padding, padding, height - padding);

        g2.drawString("TITLE", padding, padding-20);

        int chartHeight = height - 2 * padding;
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.LIGHT_GRAY);

        int maxPower = (int) Math.floor(Math.log10(maxValue));

        for (int power = 0; power <= maxPower; power++)
        {
            int gridValue = (int) Math.pow(10, power);

            double gridLogValue = Math.log(gridValue + 1);
            double gridRatio = gridLogValue / maxLog;
            int gridY = height - padding - (int) (gridRatio * chartHeight);

            if (gridY >= padding && gridY < height - padding)
            {
                if (gridValue > 1)
                    g2.drawLine(padding, gridY, width - padding, gridY);

                g2.setColor(Color.BLACK);
                String valueLabel = String.valueOf(gridValue);
                int labelWidth = fm.stringWidth(valueLabel);
                g2.drawString(valueLabel, padding - labelWidth - 5, gridY + fm.getAscent() / 2);
                g2.setColor(Color.LIGHT_GRAY);
            }
        }


        int x = padding + spacing;
        for (Map.Entry<String, Integer> entry : distances.entrySet())
        {
            String color = entry.getKey();
            int dist = entry.getValue();

            double logValue = Math.log(dist + 1); // +1 to handle zero values
            int barHeight = (int) ((height - 2 * padding) * (logValue / maxLog));

            int y = height - padding - barHeight;

            switch (color)
            {
                case "Red" -> g2.setColor(Color.RED);
                case "Green" -> g2.setColor(Color.GREEN);
                case "Blue" -> g2.setColor(Color.BLUE);
                case "Yellow" -> g2.setColor(Color.YELLOW);
                case "Orange" -> g2.setColor(Color.ORANGE);
                case "Cyan" -> g2.setColor(Color.CYAN);
                case "Purple" -> g2.setColor(Color.MAGENTA);
                case "Black" -> g2.setColor(Color.BLACK);
                case "White" -> g2.setColor(Color.WHITE);
                case "Gray" -> g2.setColor(Color.GRAY);
                default -> g2.setColor(Color.LIGHT_GRAY);
            }
            g2.fillRect(x, y, barWidth, barHeight);

            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);

            int labelWidth = fm.stringWidth(color);
            g2.drawString(color,
                    x + (barWidth - labelWidth) / 2,
                    height - padding + fm.getAscent() + 5);

            String valStr = String.valueOf(dist);
            int valWidth = fm.stringWidth(valStr);
            g2.drawString(valStr,
                    x + (barWidth - valWidth) / 2,
                    y - 5);

            x += barWidth + spacing;
        }
    }
}