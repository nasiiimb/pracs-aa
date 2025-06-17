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

    private final JPanel window;
    private final ActionBar actionBar;
    private final TreePanel treePanel;
    private final Stats stats;

    public Window(Controller controller, Model model)
    {
        this.setTitle("Prac4 - Compressor d'Arxius");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        window = new JPanel();
        window.setLayout(new BorderLayout());

        actionBar = new ActionBar(WIDTH, ACTIONBAR_HEIGHT, controller, model);
        window.add(actionBar, BorderLayout.NORTH);

        treePanel = new TreePanel(WIDTH/2, BOARD_HEIGHT, model);
        window.add(treePanel, BorderLayout.EAST);

        stats = new Stats(WIDTH/2, BOARD_HEIGHT, model);
        window.add(stats, BorderLayout.WEST);

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
                this.repaint();
                treePanel.updateEncoderDisplay();
            }
        }
    }
}
