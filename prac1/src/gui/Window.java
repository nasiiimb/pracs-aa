package gui;

import main.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Simulates the graphic output of the model.
 */
public class Window extends JFrame implements Notify, ActionListener
{
    private final int WIDTH = 600;
    private final int HEIGHT = 800;
    private final int ACTIONBAR_HEIGHT = 50;
    private final int PROGRESSBAR_HEIGHT = 30;

    private final Controller controller;
    private final Data data;

    private final JPanel window;
    private final ActionBar actionBar;
    private final LineChart lineChart;
    private final ProgressBar progressBar;


    public Window(Controller controller, Data data)
    {
        this.controller = controller;
        this.data = data;

        this.setTitle("Prac1");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window = new JPanel();
        window.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        window.setLayout(new BorderLayout());

        actionBar = new ActionBar(WIDTH, ACTIONBAR_HEIGHT, this);
        window.add(actionBar, BorderLayout.NORTH);

        progressBar = new ProgressBar(WIDTH, PROGRESSBAR_HEIGHT);
        window.add(progressBar, BorderLayout.SOUTH);

        lineChart = new LineChart(WIDTH, HEIGHT-ACTIONBAR_HEIGHT-PROGRESSBAR_HEIGHT, controller, data);
        window.add(lineChart, BorderLayout.CENTER);

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
            case Notify.PAINT -> lineChart.paint();

            case Notify.PROGRESS -> progressBar.progress();

            case Notify.STOP -> actionBar.setSelectorButtonsEnabled(true);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Notify.START ->
            {
                controller.notify(Notify.STOP);
                progressBar.reset();

                for (ButtonSelector b : actionBar.getSelectorButtons())
                {
                    if (b.isSelected())
                        controller.notify(b.getActionCommand());
                }

                actionBar.setSelectorButtonsEnabled(false);
                controller.notify(Notify.START);
            }

            case Notify.STOP -> controller.notify(Notify.STOP);
        }
    }
}
