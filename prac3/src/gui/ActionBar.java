package gui;

import main.*;
import model.Model;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


/**
 * Simulates a toolbar with components for the user to interact.
 */
public class ActionBar extends JToolBar implements ActionListener
{
    private final int WIDTH;
    private final int HEIGHT;

    private final Controller controller;
    private final Model model;

    private final JTextField nField;
    private final JComboBox<String> selector;

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
        Font font = new Font("Dialog", Font.PLAIN, 18);

        this.addSeparator();

        addButton(Notify.START);
        addButton(Notify.STOP);

        this.addSeparator();

        JLabel text = new JLabel("N: ");
        text.setFont(font);
        add(text);

        nField = new JTextField("", 5);
        nField.setFont(font);
        add(nField);

        this.addSeparator();

        selector = new JComboBox<>(new String[]{
                Notify.UNIFORME.toUpperCase(),
                Notify.NORMAL.toUpperCase(),
                Notify.EXPONENCIAL.toUpperCase()});
        selector.setFont(font);
        add(selector);

        this.addSeparator();
        this.addSeparator();

        JLabel estimationLabel = new JLabel("Temps estimat: ? ms");
        estimationLabel.setFont(font);
        nField.getDocument().addDocumentListener(new DocumentListener()
        {
            private void updateEstimation()
            {
                try
                {
                    int n = Integer.parseInt(nField.getText());
                    long t1 = model.estimateBruteForceTime(n);
                    long t2 = model.estimateDivideAndConquerTime(n);
                    String bruteStr = (t1 >= 1000) ? (t1 / 1000) + "s" : t1 + "ms";
                    String fastStr = (t2 >= 1000) ? (t2 / 1000) + "s" : t2 + "ms";
                    estimationLabel.setText("Temps → Brute: " + bruteStr + " | Divide: " + fastStr);
                }
                catch (NumberFormatException e)
                {
                    estimationLabel.setText("Temps estimat: ? ms");
                }
            }

            public void insertUpdate(DocumentEvent e) { updateEstimation(); }
            public void removeUpdate(DocumentEvent e) { updateEstimation(); }
            public void changedUpdate(DocumentEvent e) { updateEstimation(); }
        });
        add(estimationLabel);
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
        nField.setEnabled(enable);
        selector.setEnabled(enable);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Notify.START ->
            {
                int n;
                try
                {
                    n = Integer.parseInt(nField.getText());
                    if (n < 2) throw new NumberFormatException();
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(null,
                            "Introdueix un valor vàlid per N.\nEl valor ha de ser major que 1.");
                    return;
                }

                String dist = (String) selector.getSelectedItem();
                model.setConfig(n, dist.toLowerCase());
                controller.notify(Notify.START);
            }

            case Notify.STOP -> controller.notify(Notify.STOP);
        }
    }
}
