package model;

import main.*;

import java.util.*;


/**
 * Handler of the different models available.
 */
public class Model implements Notify
{
    private final Controller controller;
    private ModelThread thread;

    private BranchAndBound branchAndBoundModel;
    private BruteForce bruteForceModel;

    private int nCities;
    private int maxDistance;
    private boolean startBranchAndBound;
    private boolean startBruteForce;

    public Model(Controller controller)
    {
        this.controller = controller;
        this.thread = null;

        this.branchAndBoundModel = null;
        this.bruteForceModel = null;

        this.nCities = 0;
        this.maxDistance = 0;
        this.startBranchAndBound = false;
        this.startBruteForce = false;
    }


    public void setParams(int nCities, int maxDistance, boolean branchAndBound, boolean bruteForce)
    {
        this.nCities = nCities;
        this.maxDistance = maxDistance;
        this.startBranchAndBound = branchAndBound;
        this.startBruteForce = bruteForce;
    }

    public int getNCities() { return nCities; }

    public ModelResult getBranchAndBoundResults()
    {
        if (branchAndBoundModel == null) return null;
        return branchAndBoundModel.getResult();
    }

    public ModelResult getBruteForceResults()
    {
        if (bruteForceModel == null) return null;
        return bruteForceModel.getResult();
    }


    private class ModelThread extends Thread
    {
        public ModelThread()
        {
            this.start();
        }

        public void cancel()
        {
            if (branchAndBoundModel != null)
                branchAndBoundModel.cancel();

            if (bruteForceModel != null)
                bruteForceModel.cancel();
        }


        @Override
        public void run()
        {
            int[][] matrix = new int[nCities][nCities];
            Random rand = new Random();
            // Generar distancias simétricas aleatorias para cada par de ciudades
            for (int i = 0; i < nCities; i++)
            {
                for (int j = i; j < nCities; j++)
                {
                    if (i == j)
                        matrix[i][j] = Integer.MAX_VALUE;
                    else
                    {
                        int cost = rand.nextInt(1, maxDistance);
                        matrix[i][j] = cost;
                        matrix[j][i] = cost;
                    }
                }
            }

            if (startBranchAndBound)
                branchAndBoundModel = new BranchAndBound(controller, matrix, nCities);

            if (startBruteForce)
                bruteForceModel = new BruteForce(controller, matrix, nCities);

            try
            {
                if (branchAndBoundModel != null)
                    branchAndBoundModel.join();

                if (bruteForceModel != null)
                    bruteForceModel.join();
            }
            catch (InterruptedException e) { throw new RuntimeException(e); }

            controller.notify(Notify.PAINT);
            controller.notify(Notify.PROCESS_FINISHED);
        }
    }


    public static int reduceMatrix(int[][] matrix)
    {
        int reductionCost = 0;
        int n = matrix.length;

        // Reducción de filas
        for (int i = 0; i < n; i++)
        {
            int rowMin = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++)
            {
                if (matrix[i][j] < rowMin)
                    rowMin = matrix[i][j];
            }

            if (rowMin != 0 && rowMin < Integer.MAX_VALUE)
            {
                // Restar el mínimo de la fila a todos sus elementos (excepto INF)
                for (int j = 0; j < n; j++)
                {
                    if (matrix[i][j] < Integer.MAX_VALUE)
                        matrix[i][j] -= rowMin;
                }
                reductionCost += rowMin;
            }
        }

        // Reducción de columnas
        for (int j = 0; j < n; j++)
        {
            int colMin = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++)
            {
                if (matrix[i][j] < colMin)
                    colMin = matrix[i][j];
            }

            if (colMin != 0 && colMin < Integer.MAX_VALUE)
            {
                // Restar el mínimo de la columna a todos sus elementos
                for (int i = 0; i < n; i++)
                {
                    if (matrix[i][j] < Integer.MAX_VALUE)
                        matrix[i][j] -= colMin;
                }
                reductionCost += colMin;
            }
        }

        return reductionCost;
    }


    public static class ModelResult
    {
        public int[][] matrix;
        public List<Integer> route;    // Ruta óptima (secuencia de ciudades, incluyendo vuelta al inicio)
        public int cost;              // Cost total de la ruta óptima
        public int nodesExpanded;     // Número de nodos explorados (expandidos) en la búsqueda
        public int nodesPruned;       // Número de nodos podados (no explorados gracias a la poda)
        public long timeMs;           // Tiempo de ejecución en milisegundos
        public int initialBound;      // Cota inicial calculada por reducción de matriz
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
