package model;

import main.*;

import java.util.*;


/**
 * Simulates the search of the shortest route that connects all the cities, without repeating and coming to the start,
 * using a Branch and Bound strategy.
 */
public class BranchAndBound extends Thread
{
    private final Controller controller;
    private final int[][] matrix;
    private final int nCities;
    private int bestCost;
    private final Model.ModelResult result;

    private boolean cancel = false;

    public BranchAndBound(Controller controller, int[][] matrix, int nCities)
    {
        this.controller = controller;
        this.matrix = matrix;
        this.nCities = nCities;
        this.bestCost = Integer.MAX_VALUE;
        this.result = new Model.ModelResult();
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

        NodoEstado root = new NodoEstado(nCities);
        root.reducedMatrix = matrix;
        root.path.add(0);          // comenzar en ciudad 0
        root.vertex = 0;
        root.level = 0;
        root.cost = result.initialBound;  // cota inicial tras reducción de la matriz

        // Cola de prioridad para nodos vivos, ordenada por costo (cota estimada más baja primero)
        PriorityQueue<NodoEstado> pq = new PriorityQueue<>();
        pq.offer(root);

        while (!pq.isEmpty() && !cancel)
        {
            NodoEstado node = pq.poll();
            result.nodesExpanded++;

            // Si la cota del nodo actual ya es mayor o igual que el mejor coste encontrado, podemos podar
            if (node.cost >= bestCost)
            {
                // Todos los nodos restantes en la cola tendrán costo >= node.cost (por orden), se pueden podar
                result.nodesPruned += 1 + pq.size();
                break;
            }

            // Si el nodo actual representa una ruta que visitó todas las ciudades
            if (node.path.size() == nCities)
            {
                // Completar el ciclo volviendo a la ciudad de inicio (0)
                int lastCity = node.vertex;
                int routeCost = node.cost;

                // Añadir el coste de regresar a la ciudad 0 (puede que no esté incluido en la cota)
                routeCost += matrix[lastCity][0];
                if (routeCost < bestCost)
                {
                    bestCost = routeCost;
                    // Guardar la ruta óptima encontrada
                    result.route = new java.util.ArrayList<>(node.path);
                    result.route.add(0);
                }

                // Actualizar nodos podados: todos los restantes en la cola ya no se exploran
                result.nodesPruned += pq.size();
                break;  // ya encontramos la mejor ruta (Branch and Bound: primer óptimo es mínimo)
            }

            // Expansión de nodos hijos desde la ciudad actual
            int i = node.vertex;
            for (int j=0; (j<nCities) && !cancel; j++)
            {
                if (node.reducedMatrix[i][j] == Integer.MAX_VALUE)
                    continue;  // Ciudad j ya visitada en este camino (marcada como INF) o sin conexión

                // Generar nodo hijo para la decisión de ir de i -> j
                NodoEstado child = new NodoEstado(nCities);

                // Copiar el camino recorrido y añadir la ciudad j
                child.path = new java.util.ArrayList<>(node.path);
                child.path.add(j);
                child.level = node.level + 1;
                child.vertex = j;

                // Copiar la matriz reducida del padre para modificarla
                for (int r=0; (r<nCities) && !cancel; r++)
                    child.reducedMatrix[r] = node.reducedMatrix[r].clone();

                // Marcar la fila i y la columna j como INF (para excluir esas rutas en adelante)
                for (int k=0; (k<nCities) && !cancel; k++)
                {
                    child.reducedMatrix[i][k] = Integer.MAX_VALUE;
                    child.reducedMatrix[k][j] = Integer.MAX_VALUE;
                }

                // Evitar volver a la ciudad de origen antes de terminar recorrido
                child.reducedMatrix[j][0] = Integer.MAX_VALUE;

                // Reducir la matriz del hijo y calcular su costo (cota)
                int reductionCost = Model.reduceMatrix(child.reducedMatrix);
                int edgeCost = node.reducedMatrix[i][j];
                child.cost = node.cost + edgeCost + reductionCost;

                // Si la cota del hijo es menor que el mejor coste actual, encolar el hijo (sino, podado)
                if (child.cost < bestCost)
                    pq.offer(child);
                else
                    result.nodesPruned++;
            }
        }

        long endTime = System.currentTimeMillis();
        result.timeMs = endTime - startTime;
        result.cost = bestCost;
        controller.notify(Notify.PAINT);
    }


    private static class NodoEstado implements Comparable<NodoEstado>
    {
        private int[][] reducedMatrix;
        private List<Integer> path;
        private int cost;       // coste estimado (cota inferior) para completar la ruta desde este estado
        private int vertex;     // ciudad actual en este estado
        private int level;      // nivel de profundidad (número de aristas recorridas desde el inicio)

        public NodoEstado(int n)
        {
            this.reducedMatrix = new int[n][n];
            this.path = new ArrayList<>();
            this.cost = 0;
            this.vertex = 0;
            this.level = 0;
        }

        @Override
        public int compareTo(NodoEstado other) { return Integer.compare(this.cost, other.cost); }
    }
}
