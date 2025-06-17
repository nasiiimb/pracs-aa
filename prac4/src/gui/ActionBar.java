package gui;

import main.*;
import model.Model;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

    private final JButton fileChooser;
    private final JLabel fileLabel;
    private String fileLabelText = "No file selected";

    private final JButton compress;
    private final JButton decompress;

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

        fileChooser =addButton(Notify.FILE);

        this.addSeparator();

        fileLabel = new JLabel("No file selected");
        this.add(fileLabel);

        this.addSeparator();

        compress = new JButton("COMPRESS");
        this.add(compress);

        this.addSeparator();
        decompress = new JButton("DECOMPRESS");
        this.add(decompress);
    }


    /**
     * Adds a regular button to the toolbar.
     *
     * @param actionCommand the command for the listener to get.
     */
    private JButton addButton(String actionCommand)
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
        return button;
    }


    /**
     * Sets the enabled attribute of the toolbar buttons.
     *
     * @param enable false it wanted to be disabled, true otherwise.
     */
    public void enableAll(boolean enable)
    {
        fileChooser.setEnabled(enable);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Notify.START ->
            {
                if (fileLabelText.equals("No file selected")) return;

                model.setFileName(fileLabelText);
                controller.notify(Notify.START);
            }

            case Notify.STOP -> controller.notify(Notify.STOP);

            case Notify.FILE ->
            {
                JFileChooser fc = new JFileChooser();
                File file = new File("./src");
                fc.setCurrentDirectory(file);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int opcions = fc.showDialog(this, "Choose");

                if (opcions == JFileChooser.APPROVE_OPTION)
                {
                    fileLabelText = fc.getSelectedFile().toString();
                    if (fileLabelText.endsWith(".comp"))
                    {
                        compress.setEnabled(false);
                        decompress.setEnabled(true);
                    }
                    else
                    {
                        compress.setEnabled(true);
                        decompress.setEnabled(false);
                    }

                    fileLabel.setText(fileLabelText.split("/")[fileLabelText.split("/").length-1]);
                }
            }
        }
    }
}
