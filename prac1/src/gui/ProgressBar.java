package gui;

import javax.swing.*;
import java.awt.*;


/**
 * Simulates the progression of the processes.
 */
public class ProgressBar extends JProgressBar
{
    private final int WIDTH;
    private final int HEIGHT;

    public ProgressBar(int width, int height)
    {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.reset();
    }

    /**
     * Increments the progress of the bar by one unit, resetting it if the end is reached.
     */
    public void progress()
    {
        int p = getValue();
        if (++p > 100)
            p = 0;

        setValue(p);
    }


    /**
     * Resets the progress of the bar, setting it to 0.
     */
    public void reset()
    {
        setValue(0);
    }
}
