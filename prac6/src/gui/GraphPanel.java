package gui;

import model.Model;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Simulates a graphic output of the model, displaying a graph of the cities and the best path that connects them.
 */
public class GraphPanel extends JPanel
{
    private final Model model;
    private final int width, height;

    public GraphPanel(int width, int height, Model model)
    {
        this.width = width;
        this.height = height;
        this.model = model;

        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.WHITE);
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Model.ModelResult results = null;

        if (model.getBranchAndBoundResults() != null)
            results = model.getBranchAndBoundResults();
        else if (model.getBruteForceResults() != null)
            results = model.getBruteForceResults();

        if (results == null) return;

        Graphics2D g2 = (Graphics2D) g;
        int n = model.getNCities();
        // Calcular posiciones de las ciudades en círculo
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = (int) (Math.min(width, height) * 0.4);  // radio del círculo de distribución
        if (radius < 10) radius = Math.min(width, height) / 2 - 10;
        Point[] coords = new Point[n];
        for (int i = 0; i < n; i++) {
            // Distribuir las ciudades en círculo
            double angle = 2 * Math.PI * i / n - Math.PI / 2;  // iniciar en la parte superior
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            coords[i] = new Point(x, y);
        }

        // Dibujar todas las aristas (líneas) del grafo en color gris claro
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (results.matrix[i][j] < Integer.MAX_VALUE) {
                    Point p1 = coords[i];
                    Point p2 = coords[j];
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        List<Integer> route = results.route;
        // Si hay una ruta óptima, dibujarla resaltada en rojo
        if (route != null && route.size() > 1) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            for (int k = 0; k < route.size() - 1; k++) {
                int cityA = route.get(k);
                int cityB = route.get(k + 1);
                Point pA = coords[cityA];
                Point pB = coords[cityB];
                g2.drawLine(pA.x, pA.y, pB.x, pB.y);
            }
        }

        // Dibujar los nodos (ciudades) encima de las aristas
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < n; i++) {
            Point p = coords[i];
            // Dibujar círculo para la ciudad
            int nodeSize = 16;
            g2.setColor(Color.YELLOW);
            g2.fillOval(p.x - nodeSize/2, p.y - nodeSize/2, nodeSize, nodeSize);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - nodeSize/2, p.y - nodeSize/2, nodeSize, nodeSize);
            // Dibujar la etiqueta de la ciudad (número)
            String label = String.valueOf(i + 1);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();
            g2.drawString(label, p.x - labelWidth/2, p.y + labelHeight/2);
        }
    }
}
