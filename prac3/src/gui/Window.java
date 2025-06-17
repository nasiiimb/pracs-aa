package gui;

import main.*;
import model.Model;

import javax.swing.*;
import java.awt.*;


/**
 * Simulates the graphic output of the model.
 */
public class Window extends JFrame implements Notify
{
    public final static int WIDTH = 1024;
    public final static int BOARD_HEIGHT = WIDTH;
    public final static int ACTIONBAR_HEIGHT = 50;
    public final static int PROGRESSBAR_HEIGHT = 30;

    private final JPanel window;
    private final ActionBar actionBar;
    private final PointPanel pointPanel;

    public Window(Controller controller, Model model)
    {
        this.setTitle("Prac3 - Parelles de Punts");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        window = new JPanel();
        window.setLayout(new BorderLayout());

        actionBar = new ActionBar(WIDTH, ACTIONBAR_HEIGHT, controller, model);
        window.add(actionBar, BorderLayout.NORTH);

        pointPanel = new PointPanel(WIDTH, BOARD_HEIGHT, model);
        window.add(pointPanel, BorderLayout.CENTER);

        this.add(window);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    @Override
    public void notify(String s)
    {
        switch (s)
        {
            case Notify.START -> actionBar.enableAll(false);

            case Notify.STOP -> actionBar.enableAll(true);

            case Notify.PAINT -> this.repaint();
        }
    }
}
