package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;


/**
 * Simulates a checkbox selector button.
 */
public class ButtonSelector extends JButton
{
    private boolean selected;

    public ButtonSelector(int width, int height, String actionCommand, ActionListener actionListener)
    {
        String imgLocation = "/media/" + actionCommand + ".png";
        URL imageURL = getClass().getResource(imgLocation);
        if (imageURL != null)
        {
            ImageIcon icon = new ImageIcon(imageURL);
            this.setIcon(new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            this.setMargin(new Insets(-1, -1, 0, 0));
        }
        else System.err.println("Resource not found: " + imgLocation);

        this.setBackground(Color.RED);
        this.setOpaque(false);
        this.selected = false;
        this.setActionCommand(actionCommand);
        this.addActionListener(actionListener);
    }


    /**
     * Returns the state of the button.
     *
     * @return if it is selected.
     */
    public boolean isSelected() { return selected; }


    /**
     * Simulates the pressing of the button, selecting it if it wasn't, and deselecting it if it was.
     */
    public void press()
    {
        if (this.selected)
        {
            this.selected = false;
            this.setOpaque(false);
        }
        else
        {
            this.selected = true;
            this.setOpaque(true);
        }
    }
}
