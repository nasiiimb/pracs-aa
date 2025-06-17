package model;

import main.*;

import java.util.ArrayList;


/**
 * Handler of the different models available.
 */
public class Model implements Notify
{
    private final Controller controller;
    private ModelThread thread;

    private ArrayList<LanguageComparator> comparisons;
    private ArrayList<String> dictionaries1;
    private ArrayList<String> dictionaries2;

    public Model(Controller controller)
    {
        this.controller = controller;
        this.thread = null;

        this.comparisons  = new ArrayList<>();
        this.dictionaries1 = new ArrayList<>();
        this.dictionaries2 = new ArrayList<>();
    }


    public void setDictionaries(ArrayList<String> dictionaries1, ArrayList<String> dictionaries2)
    {
        this.dictionaries1 = dictionaries1;
        this.dictionaries2 = dictionaries2;
    }

    public ArrayList<LanguageComparator> getComparisons() { return comparisons; }

    public ArrayList<String> getDictionaries1() { return dictionaries1; }


    private class ModelThread extends Thread
    {
        private boolean cancel;

        public ModelThread()
        {
            this.cancel = false;
            this.start();
        }

        public void cancel()
        {
            this.cancel = true;
            if (!comparisons.isEmpty())
                comparisons.getLast().stop();
        }


        @Override
        public void run()
        {
            comparisons.clear();

            for (int i=0; (i<dictionaries1.size()) && !cancel; i++)
            {
                String dictionary1 = dictionaries1.get(i);

                for (int j=0; (j<dictionaries2.size()) && !cancel; j++)
                {
                    String dictionary2 = dictionaries2.get(j);
                    if (!dictionary1.equals(dictionary2))
                    {
                        LanguageComparator lc = new LanguageComparator(controller, dictionary1, dictionary2);
                        comparisons.add(lc);
                        lc.start();

                        controller.notify(Notify.PAINT);
                    }
                }

                dictionaries2.remove(dictionary1);
            }

            controller.notify(Notify.PAINT);
            controller.notify(Notify.PROCESS_FINISHED);
        }
    }


    @Override
    public void notify(String s)
    {
        switch (s)
        {
            case START -> thread = new ModelThread();

            case STOP -> thread.cancel();
        }
    }
}
