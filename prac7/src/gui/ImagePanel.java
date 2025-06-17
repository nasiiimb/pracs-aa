package gui;

import model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;


/**
 *
 */
public class ImagePanel extends JPanel
{
    private final int width;
    private final int height;

    private final Model model;

    public ImagePanel(int width, int height, Model model)
    {
        this.width = width;
        this.height = height;
        this.model = model;

        this.setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        BufferedImage image = model.getImage();
        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double aspect = (double) image.getWidth() / image.getHeight();
        int drawW = width;
        int drawH = (int) (width / aspect);
        if (drawH > height) {
            drawH = height;
            drawW = (int) (height * aspect);
        }
        int x = (width - drawW) / 2;
        int y = (height - drawH) / 2;
        g2.drawImage(image, x+5, y+5, drawW-10, drawH-10, null);

        HashMap<String, Double> dominantColors = model.getDominantColors();
        if ((dominantColors != null) && !dominantColors.isEmpty())
        {
            int sw = 40, sh = 40;
            int gap = 10;
            int total = dominantColors.size();
            Iterator<String> it = dominantColors.keySet().iterator();
            int startX = x + (drawW - (total * sw + (total-1)*gap)) / 2;
            int sampleY = y + drawH - sh - gap;

            for (int i=0; i<total; i++)
            {
                switch (it.next())
                {
                    case "NordicForest"    -> g2.setColor(new Color(34,139,34));
                    case "TropicalJungle"  -> g2.setColor(new Color(0,128,0));
                    default                -> g2.setColor(new Color(70,130,180));
                }
                g2.fillRect(startX + i * (sw + gap), sampleY, sw, sh);
                g2.setColor(Color.WHITE);
                g2.drawRect(startX + i * (sw + gap), sampleY, sw, sh);
            }
        }
    }
}
