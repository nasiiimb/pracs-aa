package model.figures;

import java.awt.*;
import java.util.ArrayList;


/**
 * Class that simulates a Tromino, a figure formed by 3 squares in an L shape.
 */
public class Tromino
{
    private final int N_SQUARES = 3;
    private final ArrayList<Square> squares;

    public Tromino() { squares = new ArrayList<>(N_SQUARES); }


    /**
     * Adds a square to the tromino, limited to 3.
     */
    public void add(Square square)
    {
        if (squares.size() < N_SQUARES)
            squares.add(square);
    }


    /**
     * Draws the figure on the screen.
     *
     * @param g graphics where the figure will be drawn.
     */
    public void paint(Graphics g)
    {
        for (Square square : squares)
            square.paint(g, true);

        Square c1 = squares.get(0);
        Point p1 = c1.getPoint();
        Square c2 = squares.get(1);
        Point p2 = c2.getPoint();
        Square c3 = squares.get(2);
        Point p3 = c3.getPoint();

        int size = c1.getSize();
        g.setColor(c1.getColor());

        if (p1.getY() == p2.getY())
        {
            g.fillRect(p2.x-1, p2.y+1, 2, size-1);
            g.fillRect(p3.x+1, p3.y-1, size-1, 2);
        }
        else
        {
            g.fillRect(p3.x-1, p3.y+1, 2, size-1);
            g.fillRect(p1.x+1, p1.y+size-1, size-1, 2);
        }
    }
}
