package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private GPU gpu;
    private AtomicInteger tick = new AtomicInteger();
    private int timeToProcessInTheCpu;
    private int timeToTrain;

    public DataBatch(Data data , int start_index , GPU gpu , int trained){
        this.data = data;
        this.start_index = start_index;
        this.gpu = gpu;
        timeToProcessInTheCpu = trained;
        timeToTrain = -1;
    }

    public void setTimeToProcessInTheCpu(int time){
        timeToProcessInTheCpu = time;
    }
    public void setTimeToTrain(int timeToTrain){
        this.timeToTrain = timeToTrain;

    }
    public int getTimeToTrainLeft(){
        return timeToTrain;
    }
    public int getTimeToProcessInTheCpu(){
        return timeToProcessInTheCpu;
    }
    public Data getData() {
        return data;
    }

    public int getStart_index() {
        return start_index;
    }

    public synchronized void updateTick(){
        tick.incrementAndGet();
    }
    public GPU getGPU(){
        return gpu;
    }
    public int getTick(){
        return tick.get();
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setStart_index(int start_index) {
        this.start_index = start_index;
    }
}
