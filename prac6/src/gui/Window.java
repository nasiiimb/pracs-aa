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
    public final static int BOARD_SIZE = WIDTH;
    public final static int ACTIONBAR_HEIGHT = 50;

    private final JPanel window;
    private final ActionBar actionBar;
    private final GraphPanel graphPanel;
    private final StatsBar branchAndBoundStatsBar;
    private final StatsBar bruteForceStatsBar;

    public Window(Controller controller, Model model)
    {
        this.setTitle("Prac6 - Problema del Viatjant de Comerç");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        window = new JPanel();
        window.setBackground(Color.WHITE);
        window.setLayout(new BorderLayout());

        graphPanel = new GraphPanel(BOARD_SIZE, BOARD_SIZE, model);
        window.add(graphPanel, BorderLayout.CENTER);

        actionBar = new ActionBar(WIDTH, ACTIONBAR_HEIGHT, controller, model, graphPanel);
        window.add(actionBar, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        branchAndBoundStatsBar = new StatsBar("Branch & Bound", WIDTH, ACTIONBAR_HEIGHT, model);
        statsPanel.add(branchAndBoundStatsBar);

        bruteForceStatsBar = new StatsBar("Força Bruta", WIDTH, ACTIONBAR_HEIGHT, model);
        statsPanel.add(bruteForceStatsBar);
        window.add(statsPanel, BorderLayout.SOUTH);

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

            case Notify.PAINT ->
            {
                branchAndBoundStatsBar.displayResult();
                bruteForceStatsBar.displayResult();
                this.repaint();
            }
        }
    }
}
