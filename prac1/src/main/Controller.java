package main;

import gui.Window;
import main.processes.*;
import java.util.ArrayList;


/**
 * Starts the application and manages the interaction between the data and the window.
 */
public class Controller implements Notify
{
    private Data data;
    private Window window;
    private ArrayList<Notify> processes;
    private int activeThreads;


    public static void main(String[] args) {new Controller().start();}

    private void start()
    {
        data = new Data();
        window = new Window(this, data);
        processes = new ArrayList<>();
    }


    /**
     * Restarts the components and processes of the model.
     */
    private void reset()
    {
        data.resetData();
        window.notify(Notify.STOP);
        processes.clear();
    }


    @Override
    public synchronized void notify(String s)
    {
        switch (s)
        {
            case Notify.START ->
            {
                if (processes.isEmpty()) reset();

                for (Notify notify : processes)
                {
                    ((Thread) notify).start();
                }
            }

            case Notify.STOP ->
            {
                for (Notify notify : processes)
                {
                    notify.notify(Notify.STOP);
                }
                reset();
            }

            case Notify.PAINT -> window.notify(Notify.PAINT);

            case Notify.PROGRESS -> window.notify(Notify.PROGRESS);

            case Notify.PROCESS_FINISHED ->
            {
                activeThreads--;
                if (activeThreads == 0)
                {
                    reset();
                }
            }

            case Notify.ADD ->
            {
                processes.add(new Addition(this, data));
                activeThreads++;
            }

            case Notify.MULT ->
            {
                processes.add(new Multiplication(this, data));
                activeThreads++;
            }
        }
    }
}
