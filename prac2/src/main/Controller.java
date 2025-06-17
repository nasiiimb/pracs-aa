package main;

import gui.Window;
import model.Model;
import model.figureModels.SierpinskiModel;
import model.figureModels.SquareModel;
import model.figureModels.TrominoModel;


/**
 * Starts the application and manages the interaction between the data and the window.
 */
public class Controller implements Notify {
    private Window window = null;
    private Model model = null;

    private int activeThreads;
    private int totalThreads;

    public static void main(String[] args) {
        new Controller().start();
    }

    private void start() {
        model = new Model(this);
        window = new Window(this, model);
        activeThreads = 0;
    }


    public int getActiveThreads() {
        return activeThreads;
    }



    @Override
    public synchronized void notify(String s) {
        switch (s) {
            case Notify.START -> {
                model.notify(Notify.START);
                window.notify(Notify.START);

                activeThreads = 0;
                Object m = model.getModel();
                if (m instanceof TrominoModel)
                    totalThreads = TrominoModel.THREADS;

                if (m instanceof SquareModel)
                    totalThreads = SquareModel.THREADS;

                if (m instanceof SierpinskiModel)
                    totalThreads = SierpinskiModel.THREADS;
            }

            case Notify.STOP -> {
                model.notify(Notify.STOP);
                window.notify(Notify.STOP);
                model.notify(Notify.PROCESS_FINISHED);
            }

            case Notify.PAINT -> window.notify(Notify.PAINT);

            case Notify.PROCESS_FINISHED -> {
                if (++activeThreads < totalThreads) return;
                window.notify(Notify.PROCESS_FINISHED);
                model.notify(Notify.PROCESS_FINISHED);
            }
        }
    }
}
