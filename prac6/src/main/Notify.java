package main;


public interface Notify
{
    /**
     * Method used for the communications between the components of the MVC structure.
     *
     * @param s the String used to send a command.
     */
    public void notify(String s);

    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String PAINT = "paint";
    public static final String PROCESS_FINISHED = "processFinished";
    public static final String EXPORT_IMAGE = "exportImage";
    public static final String EXPORT_CSV = "exportCSV";
}
