package bgu.spl.mics.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model> {
    private String studentName;
    private Model model;
    private Future<Model> future;
    private boolean finishTrain = false;


    public TrainModelEvent(String studentName, Model newModel){
        this.studentName = studentName;
        this.model=newModel;
    }

    public String getSenderName(){
        return studentName;
    }

    public Model getModel() {
        return model;
    }

    public void setFuture(Future<Model> future) {
        this.future = future;
    }

    public Future<Model> getFuture() {
        return future;
    }

    public void setFinishTrain()
    {
        finishTrain = true;
    }

    public boolean isFinishTrain() {
        return finishTrain;
    }
}
