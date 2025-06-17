package gui;

import main.*;
import model.Model;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;


/**
 * Simulates a toolbar with components for the user to interact.
 */
public class ActionBar extends JToolBar implements ActionListener
{
    private final int width;
    private final int height;

    private final Controller controller;
    private final Model model;

    private final JButton fileChooser;
    private final JLabel fileLabel;
    private String fileLabelText = "No file selected";

    public ActionBar(int width, int height, Controller controller, Model model)
    {
        this.width = width;
        this.height = height;
        this.controller = controller;
        this.model = model;

        this.setPreferredSize(new Dimension(this.width, this.height +10));
        this.setFloatable(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        this.setLayout(flowLayout);

        this.addSeparator();

        addButton(Notify.START);
        addButton(Notify.STOP);

        this.addSeparator();

        fileChooser = addButton(Notify.FILE);

        this.addSeparator();

        fileLabel = new JLabel("No file selected");
        this.add(fileLabel);
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
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(height, height, Image.SCALE_SMOOTH)));
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

                BufferedImage image;
                try { image = ImageIO.read(new File(fileLabelText)); }
                catch (IOException ex) { throw new RuntimeException(ex); }
                
                model.setImage(image);
                controller.notify(Notify.START);
            }

            case Notify.STOP -> controller.notify(Notify.STOP);

            case Notify.FILE ->
            {
                JFileChooser fc = new JFileChooser();
                File file = new File("./images");
                fc.setCurrentDirectory(file);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                        "Image files", "jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif");
                fc.setFileFilter(imageFilter);


                int opcions = fc.showDialog(this, "Choose");

                if (opcions == JFileChooser.APPROVE_OPTION)
                {
                    fileLabelText = fc.getSelectedFile().toString();
                    fileLabel.setText(fileLabelText.split("/")[fileLabelText.split("/").length-1]);
                }
            }
        }
    }
}
