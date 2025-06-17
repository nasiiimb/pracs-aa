package model.figures;

import java.awt.*;


/**
 * Class that simulates a Triangle.
 */
public class Triangle
{
    private static final int N_VERTEX = 3;
    private final Point[] points = new Point[N_VERTEX];

    public Triangle(Point p1, Point p2, Point p3)
    {
        this.points[0] = p1;
        this.points[1] = p2;
        this.points[2] = p3;
    }


    public int[] getX() { return new int[]{points[0].x, points[1].x, points[2].x}; }

    public int[] getY() { return new int[]{points[0].y, points[1].y, points[2].y}; }


    /**
     * Draws the figure on the screen.
     *
     * @param g graphics where the figure will be drawn.
     */
    public void paint(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillPolygon(new Polygon(getX(), getY(), Triangle.N_VERTEX));
    }
}
