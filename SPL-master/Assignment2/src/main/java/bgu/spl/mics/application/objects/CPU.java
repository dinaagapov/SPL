package bgu.spl.mics.application.objects;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.application.services.CPUService;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private Cluster cluster;
    private Queue<DataBatch> dataBatches = new ConcurrentLinkedQueue<>();
    private int tick ;
    private int totalTime;
    private CPUService cpuService;

    public CPU(int cores ,Cluster cluster){
        this.cores = cores;
        this.cluster = cluster;
        tick = 0;
        totalTime = 0;
    }
    public int timeProcessing(DataBatch dataBatch){
        int timeProcessing = 32/cores;
        Data.Type type = dataBatch.getData().getType();
        if(type == Data.Type.Images)
            timeProcessing = timeProcessing * 4;
        else if(type == Data.Type.Text)
            timeProcessing = timeProcessing * 2;
        else
            timeProcessing = timeProcessing * 1;
        return timeProcessing;
    }
    public void updateTotal(int timeProcessing){
        this.totalTime = timeProcessing;

    }

    public synchronized void receiveDataBatch(DataBatch dataBatch){
        dataBatches.add(dataBatch);
        updateTotal(timeProcessing(dataBatch) + getTotalTime());
        dataBatch.setTimeToProcessInTheCpu(timeProcessing(dataBatch));
    }
    public  void updateTick(){
       tick = tick + 1;
      if(totalTime != 0) {
          updateTotal(getTotalTime() + 1);
          if (!dataBatches.isEmpty()) {
              dataBatches.peek().setTimeToProcessInTheCpu(dataBatches.peek().getTimeToProcessInTheCpu() - 1);
              if (dataBatches.peek().getTimeToProcessInTheCpu() - 1 <= 0)
                  sendToGPU(dataBatches.poll());
          }
      }
    }

    private void sendToGPU(DataBatch dataBatch) {
        cluster.sendToGPU(dataBatch);
    }

    public int getTotalTime(){
        return totalTime;
    }

    public int getTick(){
        return tick;
    }
    public int numOfBathes()
    {
        return dataBatches.size();
    }
    public void setService(CPUService cpuService) {
        this.cpuService = cpuService;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "cores=" + cores +
                ", cluster=" + cluster +
                ", dataBatches=" + dataBatches +
                ", tick=" + tick +
                ", totalTime=" + totalTime +
                ", cpuService=" + cpuService +
                '}';
    }
}
