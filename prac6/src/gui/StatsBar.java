package gui;

import model.Model;

import javax.swing.*;
import java.awt.*;


/**
 * Simulates the graphic output of the model, displaying the textual statistics of the model.
 */
public class StatsBar extends JPanel
{
    private final int WIDTH;
    private final int HEIGHT;

    private final Model model;

    private final JLabel name;
    private final JLabel costLabel;
    private final JLabel expandedLabel;
    private final JLabel prunedLabel;
    private final JLabel timeLabel;
    private final JLabel boundLabel;

    public StatsBar(String statsName, int width, int height, Model model)
    {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.model = model;

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(10);
        flowLayout.setHgap(20);
        this.setLayout(flowLayout);

        name = new JLabel(statsName);
        this.add(name);

        costLabel = new JLabel();
        this.add(costLabel);

        expandedLabel = new JLabel();
        this.add(expandedLabel);

        prunedLabel = new JLabel();
        this.add(prunedLabel);

        timeLabel = new JLabel();
        this.add(timeLabel);

        boundLabel = new JLabel();
        this.add(boundLabel);

        reset();
    }


    public void reset()
    {
        costLabel.setText("Coste: N/A");
        expandedLabel.setText("Nodos explorados: 0");
        prunedLabel.setText("Nodos podados: 0");
        timeLabel.setText("Tiempo: 0 ms");
        boundLabel.setText("Cota inicial: N/A");
    }


    public void displayResult()
    {
        Model.ModelResult results = null;

        if ((name.getText().equals("Branch & Bound")) && (model.getBranchAndBoundResults() != null))
            results = model.getBranchAndBoundResults();
        else if ((name.getText().equals("Força Bruta")) && (model.getBruteForceResults() != null))
            results = model.getBruteForceResults();

        if (results == null) return;

        if (results.cost == Integer.MAX_VALUE)
            costLabel.setText("Coste: -");
        else
            costLabel.setText("Coste: " + results.cost);

        expandedLabel.setText("Nodos explorados: " + results.nodesExpanded);

        prunedLabel.setText("Nodos podados: " + results.nodesPruned);

        timeLabel.setText("Tiempo: " + results.timeMs + " ms");

        if (results.initialBound == Integer.MAX_VALUE)
            boundLabel.setText("Cota inicial: -");
        else
            boundLabel.setText("Cota inicial: " + results.initialBound);
    }
}
