package gui;

import main.Controller;
import main.Notify;
import model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;


/**
 * Simulates a toolbar with components for the user to interact.
 */
public class ActionBar extends JToolBar implements ActionListener
{
    private final int WIDTH;
    private final int HEIGHT;

    private final Controller controller;
    private final Model model;

    private final JSlider slider;
    private final ArrayList<ButtonSelector> modelButtons;

    public ActionBar(int width, int height, Controller controller, Model model)
    {
        WIDTH = width;
        HEIGHT = height;
        this.controller = controller;
        this.model = model;

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT + 10));
        this.setFloatable(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        this.setLayout(flowLayout);

        this.addSeparator();

        addButton(Notify.START);
        addButton(Notify.STOP);

        this.addSeparator();

        slider = new JSlider(2, 7, 4);
        slider.setMajorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setOpaque(false);
        this.add(slider);

        this.addSeparator();

        modelButtons = new ArrayList<>();
        modelButtons.add(new ButtonSelector(HEIGHT, HEIGHT, Notify.TROMINO, this));
        modelButtons.add(new ButtonSelector(HEIGHT, HEIGHT, Notify.SQUARE, this));
        modelButtons.add(new ButtonSelector(HEIGHT, HEIGHT, Notify.TRIANGLE, this));
        for (ButtonSelector button : modelButtons)
            this.add(button);
        modelButtons.get(0).press();
    }


    /**
     * Adds a regular button to the toolbar.
     *
     * @param actionCommand the command for the listener to get.
     */
    private void addButton(String actionCommand) {
        JButton button = new JButton();
        button.addActionListener(this);
        button.setActionCommand(actionCommand);

        String imgLocation = "/media/" + actionCommand + ".png";
        URL imageURL = getClass().getResource(imgLocation);
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_SMOOTH)));
            button.setMargin(new Insets(-1, -1, 0, 0));
        } else System.err.println("Resource not found: " + imgLocation);

        this.add(button);
    }


    /**
     * Sets the enabled attribute of the toolbar buttons.
     *
     * @param enable false it wanted to be disabled, true otherwise.
     */
    public void enableAll(boolean enable) {
        slider.setEnabled(enable);
        for (ButtonSelector button : modelButtons) {
            button.setEnabled(enable);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case Notify.START -> {
                controller.notify(Notify.STOP);
                model.setDepth(slider.getValue());
                enableAll(false);
                controller.notify(Notify.START);
            }

            case Notify.STOP -> controller.notify(Notify.STOP);

            case Notify.TROMINO, Notify.SQUARE, Notify.TRIANGLE -> {
                for (ButtonSelector button : modelButtons) {
                    if (button.isSelected())
                        button.press();
                }

                switch (e.getActionCommand()) {
                    case Notify.TROMINO -> modelButtons.get(0).press();

                    case Notify.SQUARE -> modelButtons.get(1).press();

                    case Notify.TRIANGLE -> modelButtons.get(2).press();
                }

                this.repaint();
                model.setFigure(e.getActionCommand());
            }
        }
    }
}
