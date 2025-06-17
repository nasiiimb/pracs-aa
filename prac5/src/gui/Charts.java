package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;


/**
 * Simulates a graphic output of the model, displaying column charts comparing one dictionary to all others.
 */
public class Charts extends JPanel
{
    private final Model model;
    private final int width, height;

    private final JTabbedPane tabbedPane;

    public Charts(int width, int height, Model model)
    {
        this.model = model;
        this.width = width;
        this.height = height;

        this.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane();
        this.add(tabbedPane, BorderLayout.CENTER);

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.WHITE);
    }

    public void clear()
    {
        tabbedPane.removeAll();
        revalidate();
        repaint();
    }

    public void update()
    {
        SwingUtilities.invokeLater(tabbedPane::removeAll);

        ArrayList<LanguageComparator> comparisons = model.getComparisons();
        if (comparisons.isEmpty()) return;

        ArrayList<String> dictionaries1 = model.getDictionaries1();

        for (String dictionary : dictionaries1)
        {
            LinkedHashMap<String, Double> distances = new LinkedHashMap<>();
            for (LanguageComparator c : comparisons)
            {
                if (dictionary.equals(c.getDictionary1()))
                    distances.put(c.getDictionary2(), c.getDistance());
                else if (dictionary.equals(c.getDictionary2()))
                    distances.put(c.getDictionary1(), c.getDistance());
            }

            if (!distances.isEmpty())
            {
                SwingUtilities.invokeLater(() ->
                        tabbedPane.addTab(dictionary, new ColumnChart(width, height, dictionary, distances)));
            }
        }

        revalidate();
        repaint();
    }
}
