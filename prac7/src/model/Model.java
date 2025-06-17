package model;

import main.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Handler of the different models available.
 */
public class Model implements Notify
{
    private final Controller controller;
    private ModelThread thread;

    private LandscapeClassifier landscapeClassifier;
    private BufferedImage image;

    public Model(Controller controller)
    {
        this.controller = controller;
        this.thread = null;
        this.landscapeClassifier = null;
        this.image = null;
    }


    public void setImage(BufferedImage image) { this.image = image; }

    public BufferedImage getImage() { return image; }

    public HashMap<String, Double> getDominantColors()
    {
        if (landscapeClassifier == null) return null;
        return (HashMap<String, Double>) landscapeClassifier.getDominantColors();
    }

    public HashMap<String, Integer> getColorCount()
    {
        if (landscapeClassifier == null) return null;
        return (HashMap<String, Integer>) landscapeClassifier.getColorCount();
    }

    public String getCategory()
    {
        if (landscapeClassifier == null) return null;
        return landscapeClassifier.getCategory();
    }


    private class ModelThread extends Thread
    {
        public ModelThread() { this.start(); }

        public void cancel()
        {
            landscapeClassifier.stop();
        }


        @Override
        public void run()
        {
            landscapeClassifier = new LandscapeClassifier(controller, image);
            landscapeClassifier.start();
        }
    }


    @Override
    public void notify(String s)
    {
        switch (s)
        {
            case START -> thread = new ModelThread();

            case STOP ->
            {
                if (thread == null) return;
                thread.cancel();
            }
        }
    }
}
