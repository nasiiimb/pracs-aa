package main;

import gui.Window;
import model.Model;
import model.Point;

import javax.swing.*;

public class Controller implements Notify {

    private Window window;
    private Model model;

    public static void main(String[] args) {
        new Controller().start();
    }

    private void start() {
        model = new Model(this);
        window = new Window(this, model);
    }

    @Override
    public void notify(String s) {
        switch (s) {
            case Notify.START -> {
                model.generateAndCompute();
                window.repaintBoard();
            }
            case Notify.STOP -> System.exit(0);
            case Notify.PAINT -> window.repaintBoard();
            case Notify.PROCESS_FINISHED -> window.repaintBoard();
        }
    }
}
