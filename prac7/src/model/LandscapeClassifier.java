package model;

import main.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 *
 */
public class LandscapeClassifier
{
    private final Controller controller;

    private boolean cancel;

    private final BufferedImage image;
    private Map<String, Double> dominantColors;
    private Map<String, Integer> colorCount;
    private String category;

    public LandscapeClassifier(Controller controller, BufferedImage image)
    {
        this.controller = controller;
        this.cancel = false;
        this.image = image;
    }

    public void start()
    {
        int w = image.getWidth(), h = image.getHeight();
        dominantColors = new HashMap<>();
        colorCount = new HashMap<>();
        colorCount.put("Black", 0);
        colorCount.put("White", 0);
        colorCount.put("Gray", 0);
        colorCount.put("Red", 0);
        colorCount.put("Orange", 0);
        colorCount.put("Yellow", 0);
        colorCount.put("Green", 0);
        colorCount.put("Cyan", 0);
        colorCount.put("Blue", 0);
        colorCount.put("Purple", 0);
        category = "Unknown";

        Map<String, Integer> counts = new HashMap<>();
        counts.put("NordicForest", 0);
        counts.put("TropicalJungle", 0);
        counts.put("Coastal", 0);


        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        List<Future<Map<Map<String, Integer>, Map<String, Integer>>>> futures = new ArrayList<>();
        int pixelCount = w * h;
        int step = pixelCount / cores;

        for (int i=0; i<cores; i++)
        {
            int start = i * step;
            int end = (i == cores - 1) ? pixelCount : (i + 1) * step;
            futures.add(executor.submit(new Worker(start, end)));
        }
        executor.shutdown();

        try
        {
            if (!executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.HOURS))
                throw new RuntimeException();
        }
        catch (InterruptedException e) { throw new RuntimeException(e); }

        for (Future<Map<Map<String, Integer>, Map<String, Integer>>> f : futures)
        {
            try
            {
                Map<Map<String, Integer>, Map<String, Integer>> map = f.get();
                for (Map<String, Integer> colorCount : map.keySet())
                {
                    for (String color : colorCount.keySet())
                    {
                        this.colorCount.put(color, this.colorCount.get(color) + colorCount.get(color));
                    }
                }

                for (Map<String, Integer> count : map.values())
                {
                    for (String category : count.keySet())
                    {
                        counts.put(category, counts.get(category) + count.get(category));
                    }
                }

            }
            catch (InterruptedException | ExecutionException e)
            { System.err.println("Error reading file: " + e.getMessage()); }
        }



        int total = counts.values().stream().mapToInt(i->i).sum();
        dominantColors = counts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() * 100.0 / total
                ));

        category = dominantColors.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        controller.notify(Notify.PAINT);
        controller.notify(Notify.PROCESS_FINISHED);
    }


    public void stop() { this.cancel = true; }

    public Map<String, Double> getDominantColors() { return dominantColors; }
    public Map<String, Integer> getColorCount() { return colorCount; }
    public String getCategory() { return category; }


    private class Worker implements Callable<Map<Map<String, Integer>, Map<String, Integer>>>
    {
        private final int start, end;

        public Worker(int start, int end)
        {
            this.start = start;
            this.end = end;
        }

        @Override
        public Map<Map<String, Integer>, Map<String, Integer>> call()
        {
            int w = image.getWidth();
            Map<String, Integer> colorCount = new HashMap<>();
            colorCount.put("Black", 0);
            colorCount.put("White", 0);
            colorCount.put("Gray", 0);
            colorCount.put("Red", 0);
            colorCount.put("Orange", 0);
            colorCount.put("Yellow", 0);
            colorCount.put("Green", 0);
            colorCount.put("Cyan", 0);
            colorCount.put("Blue", 0);
            colorCount.put("Purple", 0);

            Map<String, Integer> counts = new HashMap<>();
            counts.put("NordicForest", 0);
            counts.put("TropicalJungle", 0);
            counts.put("Coastal", 0);
            int sampleStep = 1;
            for (int i=start; (i<end) && !cancel; i+=sampleStep)
            {
                int x = i % w;
                int y = i / w;
                int rgb = image.getRGB(x, y);

                float[] hsv = Color.RGBtoHSB((rgb>>16)&0xFF, (rgb>>8)&0xFF, (rgb)&0xFF, null);
                float hue = hsv[0]*360f;
                float sat = hsv[1];
                float val = hsv[2];

                String color = classifyColor(hue, sat, val);
                colorCount.put(color, colorCount.get(color)+1);

                String cls = classifyPixel(hue, sat, val);
                if (!cls.equals("Unknown"))
                    counts.put(cls, counts.get(cls)+1);
            }

            Map<Map<String, Integer>, Map<String, Integer>> output = new HashMap<>();
            output.put(colorCount, counts);
            return output;
        }

        private String classifyColor(float hue, float sat, float val)
        {
            if (sat < 0.2)
            {
                if (val < 0.3) return "Black";
                else if (val > 0.8) return "White";
                else return "Gray";
            }

            if (val < 0.2) return "Black";

            if (hue >= 0 && hue < 15) return "Red";
            else if (hue >= 15 && hue < 45) return "Orange";
            else if (hue >= 45 && hue < 75) return "Yellow";
            else if (hue >= 75 && hue < 150) return "Green";
            else if (hue >= 150 && hue < 210) return "Cyan";
            else if (hue >= 210 && hue < 270) return "Blue";
            else if (hue >= 270 && hue < 330) return "Purple";
            return "Red";

        }

        private String classifyPixel(float hue, float sat, float val)
        {
            if (hue >= 90 && hue <= 140 && sat > 0.4)
                return "NordicForest";

            else if (hue >= 40 && hue <= 90 && sat > 0.5 && val > 0.5)
                return "TropicalJungle";

            else if ((hue >= 170 && hue <= 260 && val > 0.6))
                return "Coastal";

            return "Unknown";
        }
    }
}
