package gui;

import main.*;
import model.Model;

import java.awt.*;
import javax.swing.*;


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
    private final ModelPanel modelPanel;
    private final ProgressBar progressBar;


    public Window(Controller controller, Model model)
    {
        this.setTitle("Prac2");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window = new JPanel();
        window.setLayout(new BorderLayout());

        actionBar = new ActionBar(WIDTH, ACTIONBAR_HEIGHT, controller, model);
        window.add(actionBar, BorderLayout.NORTH);

        modelPanel = new ModelPanel(WIDTH, BOARD_HEIGHT, controller, model);
        window.add(modelPanel, BorderLayout.CENTER);

        progressBar = new ProgressBar(WIDTH, PROGRESSBAR_HEIGHT);
        window.add(progressBar, BorderLayout.SOUTH);

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
            case Notify.START -> progressBar.reset();

            case Notify.STOP, Notify.PROCESS_FINISHED ->
            {
                actionBar.enableAll(true);
                this.repaint();
            }

            case Notify.PAINT ->
            {
                progressBar.progress();
                this.repaint();
            }
        }
    }
}
