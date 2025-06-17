package model.figures;

import java.awt.*;


/**
 * Class that simulates a Square.
 */
public class Square
{
    private final Point point;
    private final int size;
    private final Color color;

    public Square(Point point, int size, Color color)
    {
        this.point = point;
        this.size = size;
        this.color = color;
    }


    public Point getPoint() { return point; }

    public int getSize() { return size; }

    public Color getColor() { return this.color; }


    /**
     * Draws the figure on the screen.
     *
     * @param g graphics where the figure will be drawn.
     * @param fill true if the figure has a
     */
    public void paint(Graphics g, boolean fill)
    {
        g.setColor(color);
        if (fill)
        {
            g.fillRect(point.x, point.y, size, size);
            g.setColor(Color.BLACK);
            g.drawRect(point.x, point.y, size, size);
        }
        else
            g.drawRect(point.x, point.y, size, size);
    }
}
