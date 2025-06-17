package model;

import main.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;


/**
 * Simulates the comparison of two dictionaries, calculating the distance between them.
 */
public class LanguageComparator
{
    private final Controller controller;

    private boolean cancel;

    private final String dictionary1, dictionary2;
    private double distance;

    public LanguageComparator(Controller controller, String dictionary1, String dictionary2)
    {
        this.controller = controller;
        this.cancel = false;
        this.dictionary1 = dictionary1;
        this.dictionary2 = dictionary2;
        this.distance = 0;
    }

    public void stop() { this.cancel = true; }

    public double getDistance() { return distance; }

    public String getDictionary1() { return dictionary1; }

    public String getDictionary2() { return dictionary2; }


    public void start()
    {
        System.out.println(dictionary1 + " vs " + dictionary2);
        String file1 = "src/model/languages/" + dictionary1 + ".dic";
        String file2 = "src/model/languages/" + dictionary2 + ".dic";

        long startTime = System.currentTimeMillis();

        List<String> dict1 = readFile(file1);
        List<String> dict2 = readFile(file2);

        double dictionary1Avg = compareDictionaries(dict1, dict2);
        double dictionary2Avg = compareDictionaries(dict2, dict1);

        distance = Math.sqrt(Math.pow(dictionary1Avg, 2) + Math.pow(dictionary2Avg, 2));
        System.out.println("Distance: " + distance);

        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + " ms");
        controller.notify(Notify.PAINT);
    }


    private List<String> readFile(String fileName)
    {
        List<String> words = new ArrayList<>();
        FileReader fileReader;
        BufferedReader bufferedReader;

        try
        {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            try
            {
                String line = bufferedReader.readLine();
                while (line != null)
                {
                    words.add(line);
                    line = bufferedReader.readLine();
                }
            }
            catch (IOException ex) { System.err.println("Error reading file: " + ex.getMessage()); }
            finally
            {
                try
                {
                    bufferedReader.close();
                    fileReader.close();
                } catch (IOException ex) { System.err.println("Error closing input stream: " + ex.getMessage()); }
            }
        }
        catch (FileNotFoundException ex) { System.err.println("Error opening file reader: " + ex.getMessage()); }

        words.sort(Comparator.comparingInt(String::length));
        return words;
    }


    private double compareDictionaries(List<String> dictA, List<String> dictB)
    {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        List<Future<List<Integer>>> futures = new ArrayList<>();
        int step = dictA.size() / cores;

        for (int i=0; i<cores; i++)
        {
            int start = i * step;
            int end = (i == cores - 1) ? dictA.size() : (i + 1) * step;
            futures.add(executor.submit(new Worker(dictA, dictB, start, end)));
        }
        executor.shutdown();

        try
        {
            if (!executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.HOURS))
                throw new RuntimeException();
        }
        catch (InterruptedException e) { throw new RuntimeException(e); }

        int sumDistances = 0;
        for (Future<List<Integer>> f : futures)
        {
            try
            {
                List<Integer> distances = f.get();
                for (int distance : distances)
                    sumDistances += distance;
            }
            catch (InterruptedException | ExecutionException e)
            { System.err.println("Error reading file: " + e.getMessage()); }
        }

        System.out.print(sumDistances + " / " + dictA.size() + " = ");
        double dictionaryAvg = sumDistances / (double) dictA.size();
        System.out.println(dictionaryAvg);

        return dictionaryAvg;
    }


    private class Worker implements Callable<List<Integer>>
    {
        private final List<String> dictA, dictB;
        private final int start, end;

        public Worker(List<String> dictA, List<String> dictB, int start, int end)
        {
            this.dictA = dictA;
            this.dictB = dictB;
            this.start = start;
            this.end = end;
        }

        @Override
        public List<Integer> call()
        {
            List<Integer> distances = new ArrayList<>();
            for (int i=start; (i<end) && !cancel; i++)
            {
                String wordA = dictA.get(i);
                int best = 100;
                boolean exit = false;
                for (int j=0; (j<dictB.size()) && !cancel && !exit; j++)
                {
                    String wordB = dictB.get(j);
                    if (Math.abs(wordA.length() - wordB.length()) <= best)
                        best = Math.min(best, computeWithMax(wordA, wordB, best));
                    else
                    {
                        if (wordA.length() < wordB.length())
                            exit = true;
                    }
                }

                distances.add(best);
            }

            return distances;
        }


        private int computeWithMax(String s, String t, int maxDist)
        {
            if (s.length() > t.length())
                return computeWithMax(t, s, maxDist);

            int n = s.length();
            int m = t.length();

            int prev[] = new int[m + 1];
            int curr[] = new int[m + 1];

            for (int j = 0; j <= m; j++)
                prev[j] = j;

            for (int i = 1; i <= n; i++)
            {
                curr[0] = i;
                int from = Math.max(1, i - maxDist);
                int to = Math.min(m, i + maxDist);

                if (from > 1)
                    curr[from - 1] = maxDist + 1;

                for (int j = from; j <= to; j++)
                {
                    int cost = s.charAt(i-1) == t.charAt(j-1) ? 0 : 1;
                    curr[j] = Math.min(
                            Math.min(prev[j] + 1,     // deletion
                                    curr[j-1] + 1),   // insertion
                            prev[j-1] + cost  // substitution
                    );
                }

                boolean anyBelow = false;
                for (int j=from; j<=to; j++)
                {
                    if (curr[j] <= maxDist)
                    {
                        anyBelow = true;
                        break;
                    }
                }
                if (!anyBelow) return maxDist + 1;

                int[] tmp = prev; prev = curr; curr = tmp;
            }
            return prev[m];
        }
    }
}
