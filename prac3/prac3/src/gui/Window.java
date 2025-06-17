package gui;

import main.Notify;
import model.Model;
import model.Point;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Notify notify;
    private final Model model;
    private final PointPanel board;

    public Window(Notify notify, Model model) {
        this.notify = notify;
        this.model = model;

        setTitle("Prac3 - Parelles de Punts");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ActionBar actionBar = new ActionBar(notify, model);
        add(actionBar, BorderLayout.NORTH);

        board = new PointPanel();
        add(board, BorderLayout.CENTER);

        setVisible(true);
    }

    public void repaintBoard() {
        board.setPoints(model.getPoints());
        board.setClosestPair(model.getClosest()[0], model.getClosest()[1]);
        board.setFarthestPair(model.getFarthest()[0], model.getFarthest()[1]);
        board.repaint();
    }
}