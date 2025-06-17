package model;


/**
 * Overrides the 2D point implementation, where the coordinates are saved as doubles.
 */
public class Point
{
    public double x;
    public double y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates the distance between this point and the given one.
     *
     * @param other the other point to calculate the distance.
     * @return the distance in between.
     */
    public double distanceTo(Point other)
    {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}
