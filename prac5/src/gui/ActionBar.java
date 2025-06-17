package gui;

import main.*;
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

    private final CheckComboBox selector1;
    private final CheckComboBox selector2;

    public ActionBar(int width, int height, Controller controller, Model model)
    {
        WIDTH = width;
        HEIGHT = height;
        this.controller = controller;
        this.model = model;

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT+10));
        this.setFloatable(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        this.setLayout(flowLayout);

        this.addSeparator();

        addButton(Notify.START);
        addButton(Notify.STOP);

        this.addSeparator();

        String[] options = {"all", "es_ES", "ca_CA", "en_GB", "de_DE", "fr_FR", "it_IT", "nl_NL", "cs_CZ", "tr_TR", "hu_HU"};
        selector1 = new CheckComboBox(options, "Language1");
        selector1.setPreferredSize(new Dimension(HEIGHT*3, HEIGHT));
        selector1.addActionListener(this);
        this.add(selector1);

        this.addSeparator();

        selector2 = new CheckComboBox(options, "Language2");
        selector2.setPreferredSize(new Dimension(HEIGHT*3, HEIGHT));
        this.add(selector2);
    }


    /**
     * Adds a regular button to the toolbar.
     *
     * @param actionCommand the command for the listener to get.
     */
    private void addButton(String actionCommand)
    {
        JButton button = new JButton();
        button.addActionListener(this);
        button.setActionCommand(actionCommand);

        String imgLocation = "/media/" + actionCommand + ".png";
        URL imageURL = getClass().getResource(imgLocation);
        if (imageURL != null)
        {
            ImageIcon icon = new ImageIcon(imageURL);
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_SMOOTH)));
            button.setMargin(new Insets(-1, -1, 0, 0));
        }
        else System.err.println("Resource not found: " + imgLocation);

        this.add(button);
    }


    /**
     * Sets the enabled attribute of the toolbar buttons.
     *
     * @param enable false it wanted to be disabled, true otherwise.
     */
    public void enableAll(boolean enable)
    {
        selector1.setEnabled(enable);
        selector2.setEnabled(enable);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Notify.START ->
            {
                model.setDictionaries(selector1.getSelectedItems(), selector2.getSelectedItems());
                controller.notify(Notify.START);
            }

            case Notify.STOP -> controller.notify(Notify.STOP);

            case "Language1" ->
            {
                ArrayList<String> selected = selector1.getSelectedItems();
                selector2.setSelectedItems(selected);
            }
        }
    }
}
