package gui;

import main.Notify;

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

    private final ArrayList<ButtonSelector> selectorButtons;

    public ActionBar(int width, int height, ActionListener windowListener)
    {
        WIDTH = width;
        HEIGHT = height;
        selectorButtons = new ArrayList<>();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT+10));
        this.setFloatable(false);

        addButton(Notify.START, "start_button.png", windowListener);
        addButton(Notify.STOP, "stop_button.png", windowListener);

        this.addSeparator();

        ButtonSelector add = new ButtonSelector(HEIGHT, HEIGHT, "add", "add.png", Color.GREEN);
        add.addActionListener(this);
        selectorButtons.add(add);
        this.add(add);

        ButtonSelector mult = new ButtonSelector(HEIGHT, HEIGHT, "mult", "mult.png", Color.RED);
        mult.addActionListener(this);
        selectorButtons.add(mult);
        this.add(mult);
    }


    /**
     * Returns the selectorButtons of the toolbar.
     *
     * @return it's selector buttons.
     */
    public ArrayList<ButtonSelector> getSelectorButtons() {return selectorButtons;}


    /**
     * Adds a regular button to the toolbar.
     *
     * @param actionCommand the command for the listener to get.
     * @param imgName source of the image that will be added to the button.
     * @param windowListener listener of the window.
     */
    private void addButton(String actionCommand, String imgName, ActionListener windowListener)
    {
        JButton button = new JButton();

        String imgLocation = "/media/" + imgName;
        URL imageURL = getClass().getResource(imgLocation);
        if (imageURL != null)
        {
            ImageIcon icon = new ImageIcon(imageURL);
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_SMOOTH)));
            button.setMargin(new Insets(-1, -1, 0, 0));
        }
        else System.err.println("Resource not found: " + imgLocation);

        button.addActionListener(windowListener);
        button.setActionCommand(actionCommand);
        this.add(button);
    }


    /**
     * Sets to true or false the param enabled of the selectorButtons.
     * @param enable true if enabled, false otherwise.
     */
    public void setSelectorButtonsEnabled(boolean enable)
    {
        for (ButtonSelector b : selectorButtons)
        {
            b.setEnabled(enable);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Notify.ADD, Notify.MULT -> ((ButtonSelector) e.getSource()).press();
        }
    }
}
