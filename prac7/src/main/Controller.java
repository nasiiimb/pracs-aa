package main;

import gui.Window;
import model.Model;


/**
 * Starts the application and manages interaction between the GUI (MainFrame)
 * and the model (Grafo + TSPSolver). All events go through Notify.
 */
public class Controller implements Notify
{
    private Model model;
    private Window window;

    public static void main(String[] args) { new Controller().start(); }

    private void start()
    {
        model = new Model(this);
        window = new Window(this, model);
    }


    @Override
    public void notify(String event)
    {
        switch (event)
        {
            case Notify.START ->
            {
                window.notify(Notify.START);
                model.notify(Notify.START);
            }

            case Notify.STOP ->
            {
                model.notify(Notify.STOP);
                window.notify(Notify.STOP);
            }

            case Notify.PAINT -> window.notify(Notify.PAINT);

            case Notify.PROCESS_FINISHED -> window.notify(Notify.STOP);
        }
    }
}
