package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * Simulates the graphic output of the model, displaying the textual statistics of each comparison.
 */
public class Stats extends JPanel
{
    private final Model model;
    private final JTextArea textArea;

    public Stats(int width, int height, Model model)
    {
        this.model = model;
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 24));

        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void clear()
    {
        textArea.setText("");
        revalidate();
        repaint();
    }

    public void update()
    {
        textArea.removeAll();

        ArrayList<LanguageComparator> comparisons = model.getComparisons();
        if (comparisons.isEmpty()) return;

        StringBuilder sb = new StringBuilder().append("\n  Comparisons:\n");
        for (LanguageComparator c : comparisons)
        {
            sb.append("\t").
                append(c.getDictionary1()).append(" - ").
                append(c.getDictionary2()).append(": ").
                append(String.format("%.4f", c.getDistance())).append("\t\n");
        }
        textArea.setText(sb.toString());

        revalidate();
        repaint();
    }
}
