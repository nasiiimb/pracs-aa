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
    public final static int WIDTH = 768;
    public final static int BOARD_SIZE = WIDTH;
    public final static int ACTIONBAR_HEIGHT = 50;

    private final JPanel window;
    private final ActionBar actionBar;
    private final ImagePanel imagePanel;
    private final ColumnChart columnChart;
    private final StatsBar statsBar;

    public Window(Controller controller, Model model)
    {
        this.setTitle("Prac6 - Problema del Viatjant de Comerç");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        window = new JPanel();
        window.setBackground(Color.WHITE);
        window.setLayout(new BorderLayout());

        actionBar = new ActionBar(WIDTH, ACTIONBAR_HEIGHT, controller, model);
        window.add(actionBar, BorderLayout.NORTH);

        imagePanel = new ImagePanel(BOARD_SIZE, BOARD_SIZE, model);
        window.add(imagePanel, BorderLayout.WEST);

        columnChart = new ColumnChart(BOARD_SIZE, BOARD_SIZE, model);
        window.add(columnChart, BorderLayout.EAST);

        statsBar = new StatsBar(WIDTH, ACTIONBAR_HEIGHT, model);
        window.add(statsBar, BorderLayout.SOUTH);

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
                statsBar.displayResult();
                this.repaint();
            }
        }
    }
}
