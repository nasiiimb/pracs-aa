package gui;

import model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;


/**
 * Simulates the graphic output of the model, displaying the textual statistics of the model.
 */
public class StatsBar extends JPanel
{
    private final int WIDTH;
    private final int HEIGHT;

    private final Model model;

    private final JLabel statusLabel;
    private final JPanel colorsPanel;
    private final JLabel categoryLabel;

    public StatsBar(int width, int height, Model model)
    {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.model = model;

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(10);
        flowLayout.setHgap(20);
        this.setLayout(flowLayout);

        statusLabel = new JLabel("Carrega una imatge per començar.");
        this.add(statusLabel);

        categoryLabel = new JLabel("Categoria: ");
        this.add(categoryLabel);

        colorsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        this.add(colorsPanel);

        reset();
    }


    public void reset()
    {
        statusLabel.setText("Cap dades disponibles.");
        categoryLabel.setText("Categoria: ");
        colorsPanel.removeAll();
        revalidate();
        repaint();
    }


    public void displayResult()
    {
        BufferedImage image = model.getImage();

        if (image == null) return;

        HashMap<String, Double> dominantColors = model.getDominantColors();
        String category = model.getCategory();

        statusLabel.setText(String.format("NordicForest: %.1f%%  TropicalJungle: %.1f%%  Coastal: %.1f%%",
                dominantColors.getOrDefault("NordicForest", 0.0),
                dominantColors.getOrDefault("TropicalJungle", 0.0),
                dominantColors.getOrDefault("Coastal", 0.0)
        ));
        categoryLabel.setText("Categoria: " + category);

        colorsPanel.removeAll();
        for (String c : dominantColors.keySet())
        {
            JPanel swatch = new JPanel();
            //swatch.setBackground(c);
            swatch.setPreferredSize(new Dimension(20,20));
            swatch.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            colorsPanel.add(swatch);
        }
        revalidate();
        repaint();
    }
}
