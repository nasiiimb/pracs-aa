package main;

import java.util.ArrayList;


/**
 * Stores the data from the model.
 */
public class Data
{
    private final ArrayList<Long> additionTime;
    private final ArrayList<Long> multiplicationTime;
    private final ArrayList<Integer> elements;

    public Data()
    {
        additionTime = new ArrayList<>();
        multiplicationTime = new ArrayList<>();
        elements = new ArrayList<>();
    }


    /**
     * Resets the data for the model.
     */
    public void resetData()
    {
        additionTime.clear();
        multiplicationTime.clear();
        elements.clear();

        for (int i=100; i<=1000; i=i+50)
        {
            elements.add(i);
        }
    }


    public int getAdditionTimeSize() { return additionTime.size(); }

    public int getMultiplicationTimeSize() { return multiplicationTime.size(); }

    public int getElementsSize() { return elements.size(); }


    public long getAdditionTime(int i) { return(additionTime.get(i)); }

    public long getMultiplicationTIme(int i) { return(multiplicationTime.get(i)); }

    public int getElement(int i) { return elements.get(i); }


    public void setAdditionTime(long t) { additionTime.add(t); }

    public void setMultiplicationTIme(long t) { multiplicationTime.add(t); }

    public void setElement(int n) { elements.add(n); }
}
