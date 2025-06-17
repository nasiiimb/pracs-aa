package model;

import main.*;

import java.util.List;


/**
 * Simulates the search of the shortest route that connects all the cities, without repeating and coming to the start,
 * using a Brute Force strategy.
 */
public class BruteForce extends Thread
{
    private final Controller controller;
    private final int[][] matrix;
    private final int nCities;
    private int bestCost;
    private final Model.ModelResult result;

    private boolean cancel = false;

    public BruteForce(Controller controller, int[][] matrix, int nCities)
    {
        this.controller = controller;
        this.matrix = matrix;
        this.nCities = nCities;
        this.result = new Model.ModelResult();
        this.bestCost = Integer.MAX_VALUE;
        this.start();
    }

    public Model.ModelResult getResult() { return result; }

    public void cancel() { this.cancel = true; }


    @Override
    public void run()
    {
        long startTime = System.currentTimeMillis();

        result.matrix = matrix;
        result.route = null;
        result.cost = Integer.MAX_VALUE;
        result.nodesExpanded = 0;
        result.nodesPruned = 0;
        result.timeMs = 0;
        result.initialBound = Model.reduceMatrix(matrix);

        boolean[] visited = new boolean[nCities];
        visited[0] = true;
        java.util.List<Integer> currentPath = new java.util.ArrayList<>();
        currentPath.add(0);
        bestCost = Integer.MAX_VALUE;

        bruteForceDFS(0, 1, 0, visited, currentPath);

        long endTime = System.currentTimeMillis();
        result.timeMs = endTime - startTime;
        result.cost = bestCost;
        controller.notify(Notify.PAINT);
    }


    private void bruteForceDFS(int currentCity, int count, int currentCost, boolean[] visited, List<Integer> currentPath)
    {
        if (cancel) return;

        // Contar este nodo (estado) como explorado
        result.nodesExpanded++;

        // Si se han visitado todas las ciudades, completar la ruta de regreso al origen
        if (count == nCities)
        {
            int totalCost = currentCost + matrix[currentCity][0];
            if (totalCost < bestCost)
            {
                bestCost = totalCost;
                result.route = new java.util.ArrayList<>(currentPath);
                result.route.add(0);  // completar ciclo volviendo al inicio
            }
            return;
        }

        // Recorrer recursivamente las opciones para la siguiente ciudad no visitada
        for (int j=0; (j<nCities) && !cancel; j++)
        {
            if (!visited[j])
            {
                visited[j] = true;
                currentPath.add(j);
                bruteForceDFS(j, count + 1, currentCost + matrix[currentCity][j], visited, currentPath);
                // backtracking: deshacer movimiento
                visited[j] = false;
                currentPath.removeLast();
            }
        }
    }
}
