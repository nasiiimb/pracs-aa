package main.processes;

import main.Controller;
import main.Data;
import main.Matrix;
import main.Notify;

/**
 * Simulates the complexity of the multiplication of two matrices, O(n^3).
 */
public class Multiplication extends Thread implements Notify {
    private final Controller controller;
    private final Data data;

    private boolean cancel;
    private int step;
    private final int maxSteps = 2500000;
    private double k = 0;

    public Multiplication(Controller controller, Data data) {
        this.controller = controller;
        this.data = data;
    }

    @Override
    public void run() {
        cancel = false;
        step = 0;

        for (int index = 0; (index < data.getElementsSize()) && (!cancel); index++) {
            int n = data.getElement(index);
            Matrix a = new Matrix(n, n).fillRandom();
            Matrix b = new Matrix(n, n).fillRandom();
            long startTime = System.nanoTime();

            Matrix result = Matrix.multiply(a, b, controller);

            if (!cancel) {
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                data.setMultiplicationTIme(duration);

                if (k == 0 && index == 0) {
                    // Calculate the constant k
                    this.k = duration / (double) (n * n * n);
                    System.out.println("------------------------------");
                    System.out.printf("N = %d, Constant Multiplicativa de multiplicacions de matrius = %.4f\n", n, this.k);
                } else {
                    // Calculate the estimated execution time
                    double estimatedTime = this.k * (n * n * n);
                    System.out.println("Multiplicació de matrius");
                    System.out.printf("N = %d, Temps de càlcul estimat = %.4f ns\n", n, estimatedTime);
                    System.out.println("Temps de càlcul real = " + duration + " ns");
                }

                controller.notify(Notify.PAINT);
            }
        }

        controller.notify(PROCESS_FINISHED);
    }

    @Override
    public synchronized void notify(String s) {
        if (Notify.STOP.equals(s)) {
            cancel = true;
        }
    }
}