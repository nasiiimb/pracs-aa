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
    public final static int BOARD_SIZE = WIDTH/2;
    public final static int ACTIONBAR_HEIGHT = 50;

    private final JPanel window;
    private final ActionBar actionBar;
    private Stats stats;
    private Charts charts;
    private GraphPanel graphPanel;
    private DendrogramPanel dendrogramPanel;

    public Window(Controller controller, Model model)
    {
        this.setTitle("Prac5 - Comparador Léxic de Diccionaris");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        window = new JPanel();
        window.setBackground(Color.WHITE);
        window.setLayout(new BorderLayout());

        actionBar = new ActionBar(WIDTH, ACTIONBAR_HEIGHT, controller, model);
        window.add(actionBar, BorderLayout.NORTH);

        JPanel activities = new JPanel(new GridLayout(2, 2, 0, 0));

        stats = new Stats(BOARD_SIZE, BOARD_SIZE, model);
        activities.add(stats);

        charts = new Charts(BOARD_SIZE, BOARD_SIZE, model);
        activities.add(charts);

        graphPanel = new GraphPanel(BOARD_SIZE, BOARD_SIZE, model);
        activities.add(graphPanel);

        dendrogramPanel = new DendrogramPanel(BOARD_SIZE, BOARD_SIZE, model);
        activities.add(dendrogramPanel);

        window.add(activities, BorderLayout.CENTER);

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
            case Notify.START ->
            {
                stats.clear();
                charts.clear();
                this.repaint();

                actionBar.enableAll(false);
            }

            case Notify.STOP -> actionBar.enableAll(true);

            case Notify.PAINT ->
            {
                stats.update();
                charts.update();
                this.repaint();
            }
        }
    }
}
