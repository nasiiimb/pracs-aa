package gui;

import main.*;
import model.Model;

import javax.imageio.ImageIO;
import javax.swing.*;
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
    private final int WIDTH;
    private final int HEIGHT;

    private final Controller controller;
    private final Model model;
    private final GraphPanel graphPanel;

    private final JSpinner citySpinner;
    private final JCheckBox branchAndBoundCheckbox;
    private final JCheckBox bruteForceCheckbox;
    private final JButton exportImageButton;
    private final JButton exportCSVButton;

    public ActionBar(int width, int height, Controller controller, Model model, GraphPanel graphPanel)
    {
        WIDTH = width;
        HEIGHT = height;
        this.controller = controller;
        this.model = model;
        this.graphPanel = graphPanel;

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

        citySpinner = new JSpinner(new SpinnerNumberModel(5, 3, 20, 1));
        this.add(citySpinner);

        this.addSeparator();

        branchAndBoundCheckbox = new JCheckBox("Branch & Bound");
        branchAndBoundCheckbox.setSelected(false);
        this.add(branchAndBoundCheckbox);

        this.addSeparator();

        bruteForceCheckbox = new JCheckBox("Força Bruta");
        bruteForceCheckbox.setSelected(false);
        this.add(bruteForceCheckbox);

        this.addSeparator();

        exportImageButton = new JButton("Exportar imatge");
        exportImageButton.addActionListener(this);
        exportImageButton.setActionCommand(Notify.EXPORT_IMAGE);
        this.add(exportImageButton);

        this.addSeparator();

        exportCSVButton = new JButton("Exportar CSV");
        exportCSVButton.addActionListener(this);
        exportCSVButton.setActionCommand(Notify.EXPORT_CSV);
        this.add(exportCSVButton);
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
        branchAndBoundCheckbox.setEnabled(enable);
        bruteForceCheckbox.setEnabled(enable);
        exportImageButton.setEnabled(enable);
        exportCSVButton.setEnabled(enable);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Notify.START ->
            {
                int nCities = (int) citySpinner.getValue();
                model.setParams(nCities, 1000000,
                        branchAndBoundCheckbox.isSelected(), bruteForceCheckbox.isSelected());
                controller.notify(Notify.START);
            }

            case Notify.STOP -> controller.notify(Notify.STOP);

            case Notify.EXPORT_IMAGE -> exportImage();

            case Notify.EXPORT_CSV -> exportCSV();
        }
    }


    private void exportImage()
    {
        Model.ModelResult results = null;

        if (model.getBranchAndBoundResults() != null)
            results = model.getBranchAndBoundResults();
        else if (model.getBruteForceResults() != null)
            results = model.getBruteForceResults();

        if (results == null)
        {
            JOptionPane.showMessageDialog(this, "No hi ha cap graf per exportar.",
                    "Exportar imatge", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar imatge del graf");
        fileChooser.setSelectedFile(new File("graf.png"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png"))
                file = new File(file.getAbsolutePath() + ".png");

            try { exportGraphImage(file); }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(this, "Error al guardar la imatge: " + ex.getMessage(),
                        "Error d'exportació", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void exportGraphImage(File file) throws IOException
    {
        // Crear imagen y dibujar el contenido del panel en ella
        BufferedImage image = new BufferedImage(Window.BOARD_SIZE, Window.BOARD_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        graphPanel.paint(g2);
        g2.dispose();
        ImageIO.write(image, "png", file);
    }


    private void exportCSV()
    {
        Model.ModelResult results = null;

        if (model.getBranchAndBoundResults() != null)
            results = model.getBranchAndBoundResults();
        else if (model.getBruteForceResults() != null)
            results = model.getBruteForceResults();

        if (results == null)
        {
            JOptionPane.showMessageDialog(this, "No hi ha cap resultat per exportar.",
                    "Exportar CSV", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar resultats (CSV)");
        fileChooser.setSelectedFile(new File("tsp_resultats.csv"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv"))
                file = new File(file.getAbsolutePath() + ".csv");

            try (PrintWriter pw = new PrintWriter(file))
            {
                pw.println("Ruta óptima,Coste,Nodos explorados,Nodos podados,Tiempo (ms),Cota inicial");

                StringBuilder routeStr = new StringBuilder();
                for (int i = 0; i < results.route.size(); i++)
                {
                    int city = results.route.get(i) + 1;  // convertir a índice 1-base para legibilidad
                    routeStr.append(city);
                    if (i < results.route.size() - 1)
                        routeStr.append(" -> ");
                }

                String costStr = (results.cost == Integer.MAX_VALUE) ? "N/A" : String.valueOf(results.cost);
                String boundStr = (results.initialBound == Integer.MAX_VALUE) ? "N/A" : String.valueOf(results.initialBound);
                pw.printf("%s,%s,%d,%d,%d,%s%n",
                        routeStr, costStr,
                        results.nodesExpanded, results.nodesPruned,
                        results.timeMs, boundStr);
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(this, "Error al guardar CSV: " + ex.getMessage(),
                        "Error de exportación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
