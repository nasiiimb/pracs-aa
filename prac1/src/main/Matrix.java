package main;

import java.util.Random;


/**
 * A Matrix class that uses a single array to store elements.
 * Provides methods for initializing, adding, multiplying, and printing matrices.
 */
public class Matrix
{
    private final int width;
    private final int height;
    private final double[] data;

    /**
     * Constructs a Matrix with the specified width (columns) and height (rows).
     *
     * @param width  the number of columns in the matrix
     * @param height the number of rows in the matrix
     */
    public Matrix(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.data = new double[width * height];
    }


    /**
     * Sets the value at the specified row and column.
     *
     * @param row   the row index (0-indexed)
     * @param col   the column index (0-indexed)
     * @param value the value to set at the specified position
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public void set(int row, int col, double value)
    {
        if ((row < 0 || row >= height) || (col < 0 || col >= width))
        {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        data[row * width + col] = value;
    }


    /**
     * Retrieves the value at the specified row and column.
     *
     * @param row the row index (0-indexed)
     * @param col the column index (0-indexed)
     * @return the value at the specified position
     * @throws IndexOutOfBoundsException if the row or column is out of bounds
     */
    public double get(int row, int col)
    {
        if ((row < 0 || row >= height) || (col < 0 || col >= width))
        {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return data[row * width + col];
    }


    /**
     * Fills the matrix with random values.
     */
    public Matrix fillRandom()
    {
        Random rand = new Random();
        for (int i = 0; i < data.length; i++)
        {
            data[i] = rand.nextDouble()*100 - 50;
        }
        return this;
    }


    /**
     * Adds two matrices element-wise.
     *
     * @param a the first matrix
     * @param b the second matrix
     * @return a new Matrix that is the element-wise sum of matrices 'a' and 'b'
     * @throws IllegalArgumentException if the dimensions of the matrices do not match
     */
    public static Matrix add(Matrix a, Matrix b, Controller controller)
    {
        int step = 0;
        int maxSteps = 250000;

        if ((a.width != b.width) || (a.height != b.height))
        {
            throw new IllegalArgumentException("Matrix dimensions must match for addition");
        }

        Matrix result = new Matrix(a.width, a.height);
        for (int i = 0; i < a.data.length; i++)
        {
            result.data[i] = a.data[i] + b.data[i];
            if (++step > maxSteps)
            {
                step = 0;
                controller.notify(Notify.PROGRESS);
            }
        }
        return result;
    }


    /**
     * Multiplies two matrices.
     *
     * @param a the first matrix
     * @param b the second matrix
     * @return a new Matrix that is the product of matrices 'a' and 'b'
     * @throws IllegalArgumentException if the number of columns of matrix 'a' is not equal to the number of rows of matrix 'b'
     */
    public static Matrix multiply(Matrix a, Matrix b, Controller controller)
    {
        if (a.width != b.height)
        {
            throw new IllegalArgumentException("Matrix dimensions not compatible for multiplication");
        }

        int step = 0;
        int maxSteps = 2500000;

        Matrix result = new Matrix(b.width, a.height);
        for (int i = 0; i < a.height; i++)
        {
            for (int j = 0; j < b.width; j++)
            {
                double sum = 0;
                for (int k = 0; k < a.width; k++)
                {
                    sum += a.get(i, k) * b.get(k, j);
                    if (++step > maxSteps)
                    {
                        step = 0;
                        controller.notify(Notify.PROGRESS);
                    }
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }


    /**
     * Returns a string representation of the matrix.
     *
     * @return a string representing the matrix elements in row-major order
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                sb.append(String.format("%.4f", get(i, j))).append("\t");

            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
