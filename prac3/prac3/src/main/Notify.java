package main;

public interface Notify {
    void notify(String s);

    String START = "start";
    String STOP = "stop";
    String PAINT = "paint";
    String PROCESS_FINISHED = "processFinished";
}
