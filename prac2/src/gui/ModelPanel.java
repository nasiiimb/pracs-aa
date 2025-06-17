package gui;

import main.*;
import model.Model;
import model.figureModels.TrominoModel;
import model.figures.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;


/**
 * Simulates the graphic output of the model.
 */
public class ModelPanel extends JPanel implements MouseListener
{
    private final int WIDTH;
    private final int HEIGHT;

    private final Controller controller;
    private final Model model;

    public ModelPanel(int width, int height, Controller controller, Model model)
    {
        WIDTH = width;
        HEIGHT = height;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);

        this.controller = controller;
        this.model = model;
        this.addMouseListener(this);
    }


    @Override
    public void repaint()
    {
        if (this.getGraphics() != null)
            this.paint(this.getGraphics());
    }


    @Override
    public void paint(Graphics g)
    {
        super.paintComponent(g);

        if (model.getModel() instanceof TrominoModel trominoModel)
        {
            String text = "Press anywhere in the screen to select where the empty tile will be placed";
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("TimesRoman", Font.BOLD, 24));

            int x = WIDTH/2 - (g.getFont().getSize()*text.length()/2 /2);
            g.drawString(text, x, HEIGHT/2);
        }

        ArrayList<Object> poligonos = model.getPolygons();
        if ((poligonos == null) || (poligonos.isEmpty()))
            return;

        for (int i=0; i<poligonos.size(); i++)
        {
            if (poligonos.get(i) instanceof Tromino tromino)
                tromino.paint(g);

            if (poligonos.get(i) instanceof Square square)
                square.paint(g, square.getColor().equals(Color.BLACK));

            if (poligonos.get(i) instanceof Triangle triangle)
                triangle.paint(g);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (!model.isRunning()) return;

        if (model.getModel() instanceof TrominoModel trominoModel)
            trominoModel.start(e.getX(), e.getY());
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
