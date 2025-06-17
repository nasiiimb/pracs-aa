package gui;

import main.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * Simulates the representation of a line chart.
 */
public class LineChart extends JPanel
{
    private final int WIDTH;
    private final int HEIGHT;

    private final Controller parent;
    private final Data data;

    public LineChart(int width, int height, Controller parent, Data data)
    {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);

        this.parent = parent;
        this.data = data;
    }


    /**
     * Paints the chart with the data of the model.
     */
    public void paint()
    {
        if (this.getGraphics() != null)
            paintComponent(this.getGraphics());
    }


    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setFont(new Font("default", Font.BOLD, 12));

        int w = WIDTH - 1;
        int h = HEIGHT - 10;
        int startMargin = 60;
        int endMargin = 20;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        g.drawLine(startMargin, endMargin, startMargin, h - startMargin);
        g.drawLine(startMargin, h - startMargin, w - endMargin, h - startMargin);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform defaultAt = g2d.getTransform();
        AffineTransform at = new AffineTransform();
        at.rotate(- Math.PI / 2);
        g2d.setTransform(at);
        g2d.drawString("Time", -startMargin/2-endMargin-(h-startMargin-endMargin)/2, startMargin/2);
        g2d.setTransform(defaultAt);

        g.drawString("n Elements", w/2, h-startMargin/4);


        if (data == null) return;

        int dataSize = Math.max(data.getAdditionTimeSize(), data.getMultiplicationTimeSize());
        int maxElement = 0;
        for (int i = 0; i < dataSize; i++)
        {
            if (data.getElement(i) > maxElement)
            {
                maxElement = data.getElement(i);
            }
        }

        long maxTime;
        int px, py, pax, pay, max;
        maxTime = 0;
        for (int i = 0; i < data.getAdditionTimeSize(); i++)
        {
            if (data.getAdditionTime(i) > maxTime)
            {
                maxTime = data.getAdditionTime(i);
            }
        }
        for (int i = 0; i < data.getMultiplicationTimeSize(); i++)
        {
            if (data.getMultiplicationTIme(i) > maxTime)
            {
                maxTime = data.getMultiplicationTIme(i);
            }
        }

        if (dataSize == 0) return;

        // X-axis (elements)
        max = data.getElement(dataSize-1)/100;
        for (int i=1; i<=max; i++)
        {
            g.setColor(Color.LIGHT_GRAY);
            pax = px = startMargin*2/3 + (i*100) * (w - endMargin*4) / maxElement;
            pay = endMargin;
            py = h-startMargin-1;
            g.drawLine(pax, pay, px, py);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(data.getElement((i-1)*2)), px-10, py+(startMargin/4));
        }

        // Y-axis (seconds)
        max = (int) (maxTime/Math.pow(10, 8));
        for (int i=0; i<=max+1; i++)
        {
            g.setColor(Color.LIGHT_GRAY);
            pax = startMargin+1;
            px = w-endMargin;
            pay = py = (h - startMargin*3/2) - ((int) (i * Math.pow(10, 8) * (h - endMargin*7) / maxTime));
            g.drawLine(pax, pay, px, py);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(i), startMargin*3/4, py+5);
        }

        // Addition
        pax = startMargin;
        pay = h - startMargin;
        for (int i = 0; i < data.getAdditionTimeSize(); i++)
        {
            g.setColor(Color.GREEN);
            px = startMargin*2/3 + data.getElement(i) * (w - endMargin*4) / maxElement;
            py = (h - startMargin*3/2) - ((int) (data.getAdditionTime(i) * (h - endMargin*7) / maxTime));
            g.fillOval(px - 3, py - 3, 7, 7);
            if (i > 0) g.drawLine(pax, pay, px, py);
            g.setColor(Color.BLACK);
            g.drawOval(px - 3, py - 3, 7, 7);
            pax = px;
            pay = py;
        }

        // Multiplication
        pax = startMargin;
        pay = h - startMargin;
        for (int i = 0; i < data.getMultiplicationTimeSize(); i++)
        {
            g.setColor(Color.RED);
            px = startMargin*2/3 + data.getElement(i) * (w - endMargin*4) / maxElement;
            py = (h - startMargin*3/2) - ((int) (data.getMultiplicationTIme(i) * (h - endMargin*7) / maxTime));
            g.fillOval(px - 3, py - 3, 7, 7);
            if (i > 0) g.drawLine(pax, pay, px, py);
            g.setColor(Color.BLACK);
            g.drawOval(px - 3, py - 3, 7, 7);
            pax = px;
            pay = py;
        }
    }
}
