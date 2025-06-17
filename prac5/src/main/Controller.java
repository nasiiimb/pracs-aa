package main;

import gui.Window;
import model.Model;


/**
 * Starts the application and manages the interaction between the data and the window.
 */
public class Controller implements Notify
{
    private Window window;
    private Model model;

    public static void main(String[] args) { new Controller().start(); }

    private void start()
    {
        model = new Model(this);
        window = new Window(this, model);
    }

    @Override
    public void notify(String s)
    {
        switch (s)
        {
            case Notify.START ->
            {
                window.notify(Notify.START);
                model.notify(Notify.START);
            }

            case Notify.STOP ->
            {
                window.notify(Notify.STOP);
                model.notify(Notify.STOP);
            }

            case Notify.PAINT -> window.notify(Notify.PAINT);

            case Notify.PROCESS_FINISHED -> window.notify(Notify.STOP);
        }
    }
}
