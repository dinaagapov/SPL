package bgu.spl.mics.application.objects;

import bgu.spl.mics.Events.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {




    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {
        @SerializedName("RTX3090")
        RTX3090,
        @SerializedName("RTX2080")
        RTX2080,
        @SerializedName("GTX1080")
        GTX1080
    }
    private Type type;
    private Model model;
    private Cluster cluster;
    private Queue<DataBatch> TrainingQueue;
    private TrainModelEvent trainEvent = null;
    private LinkedList<DataBatch> dataBatches = new LinkedList<>();

    private int timeToTrain;
    private int limitedBatches;
    private int numberOfBatchesToTrain;


    private AtomicInteger Trained = new AtomicInteger(0);
    private Data DataToProcess;
    private GPUService service;

    private AtomicInteger tick =  new AtomicInteger(0);
    private AtomicInteger currentIndex =  new AtomicInteger(0);


    public GPU(Type type , Cluster cluster ){
        this.cluster = cluster;
        this.type = type;
        TrainingQueue = new ConcurrentLinkedQueue<>();


    }
    public void process(TrainModelEvent event) throws InterruptedException {
        trainEvent = event;
        if(currentIndex.get() < numberOfBatchesToTrain){
            sendToCPU();
        }
        while (!trainEvent.isFinishTrain()) {
            Thread.currentThread().sleep(2000);
        }
    }

    public void prepareBatches(Data data){
        DataToProcess = data;
        numberOfBatchesToTrain = DataToProcess.getSize()/1000;
        if(type == Type.RTX2080) {
            timeToTrain = 2;
            limitedBatches = 16;
        }
        else if(type == Type.GTX1080) {
            timeToTrain = 4;
            limitedBatches = 8;
        }
        else {
            timeToTrain = 1;
            limitedBatches = 32;
        }
        for(int i = 0 ; i < data.getSize() ; i = i + 1000)
            dataBatches.add(new DataBatch(data , i ,this,0));
    }
    public void sendToCPU(){
        while(currentIndex.get() < limitedBatches + Trained.get() && currentIndex.get()<numberOfBatchesToTrain) {
            cluster.sendToCPU(dataBatches.get(currentIndex.get()));
            currentIndex.incrementAndGet();
        }
    }
    public void receiveProcessed(DataBatch db) {
        db.setTimeToTrain(timeToTrain);
        TrainingQueue.add(new DataBatch(db.getData(),db.getStart_index(),this,0));
        if(Trained.get() < numberOfBatchesToTrain) {

            Trained.incrementAndGet();
            if (Trained.get() >= numberOfBatchesToTrain)
            {
                complete();
            }
            else {
                sendToCPU();
            }
        }
        if (Trained.get() >= numberOfBatchesToTrain)
        {
            complete();
        }
    }
    public void updateTick(){
        if(!TrainingQueue.isEmpty()){
            TrainingQueue.peek().setTimeToTrain(TrainingQueue.peek().getTimeToTrainLeft() - 1);
            tick.incrementAndGet();
            if(TrainingQueue.peek().getTimeToTrainLeft() == 0) {
                TrainingQueue.poll();
            }
        }

    }

    public void complete(){
        if (numberOfBatchesToTrain !=0) {
            if (TrainingQueue.size() == numberOfBatchesToTrain) {
                cluster.addAvailableGPU(this);
                trainEvent.setFinishTrain();
                TrainingQueue = new ConcurrentLinkedQueue<>();
                currentIndex.set(0);
                Trained.set(0);
            }
        }
    }
    public AtomicInteger getCurrentIndex()
    {
        return currentIndex;
    }
    public int getTrainedNum()
    {
        return Trained.get();
    }

    public Integer trainedBatches(){
        return Trained.get();
    }
    public int getTick(){
        return tick.get();
    }
    public Queue<DataBatch> getTrainedQ()
    {
        return TrainingQueue;
    }
    public int getDataBatchesNumber()
    {
        return dataBatches.size();
    }
    public void setService(GPUService gpuService) {}
    public GPUService getService() {
        return service;
    }
    public Type getType() {
        return type;
    }
    @Override
    public String toString() {
        return "GPU{" +
                "type=" + type +
                ", model=" + model +
                ", cluster=" + cluster +
                ", TrainingQueue=" + TrainingQueue +
                ", dataBatches=" + dataBatches +
                ", timeToTrain=" + timeToTrain +
                ", Trained=" + Trained +
                ", limitedBatches=" + limitedBatches +
                ", numberOfBatchesToTrain=" + numberOfBatchesToTrain +
                ", service=" + service +
                ", currentIndex=" + currentIndex +
                '}';
    }
}
