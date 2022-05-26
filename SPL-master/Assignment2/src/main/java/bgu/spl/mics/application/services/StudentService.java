package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcasts.PublishConferenceBroadcast;
import bgu.spl.mics.Broadcasts.TickBroadcast;
import bgu.spl.mics.Events.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    Student student;
    private boolean init = false;
    public Queue<Model> allModelToTrain = new LinkedList<>();
    private AtomicBoolean canSendNewModel = new AtomicBoolean(false);
    public StudentService(String name, Student s) {
        super(name);
        student = s;
    }


    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) ->
                {
                    if (!init)
                    {
                        allModelToTrain.addAll(student.getModels());
                        if (allModelToTrain.size()>0)
                        {
                            TrainModelEvent trainModelEvent = new TrainModelEvent(student.getName(),allModelToTrain.poll());
                            trainModelEvent.setFuture(sendEvent(trainModelEvent));
                        }
                        init = true;
                    }

                }
        );
        subscribeBroadcast(PublishConferenceBroadcast.class, (PublishConferenceBroadcast event) ->
                {
                    if (event.getAggregatedResults().size()>0) {
                        for (int i=0; i<event.getAggregatedResults().size();i++) {
                            student.setPapersRead(student.getPapersRead() + 1);
                        }
                    }
                }
        );
        subscribeEvent(FinishedTrainModelEvent.class ,(FinishedTrainModelEvent event)->
                {
                    canSendNewModel.set(true);
                    addNewTrainModel();
                    event.getTheTrainedModel().getFuture().get().setStatus(Model.Status.Trained);
                    student.HandleModel(event.getTheTrainedModel().getFuture().get());
                    Cluster.getInstance().addTrainModel(event.getTheTrainedModel().getFuture().get());
                    TestModelEvent testModelEvent = new TestModelEvent(student, event.getTheTrainedModel().getFuture());
                    sendEvent(testModelEvent);

                }
        );
        subscribeEvent(FinishedTestModelEvent.class ,(FinishedTestModelEvent event)->
                {
                    student.HandleModel(getFuture(event.getTestedModelEvent()).get());
                    event.getTestedModelEvent().getFuture().get().setStatus(Model.Status.Tested);
                    PublishResultsEvent pubEvent= new PublishResultsEvent(student.getName() ,event);
                    sendEvent(pubEvent);
                }

        );
        subscribeEvent(FinishedPublishResultEvent.class ,(FinishedPublishResultEvent event)->
                {
                    while (event.getThePublishEvent().getFuture().get().getStatus() != Model.Status.Tested)
                    {
                        try{
                            wait();
                        }
                        catch ( InterruptedException ignored){}
                    }
                    event.getThePublishEvent().getFuture().get().getStudent().updatePublications();
                }

        );
    }
    public synchronized void addNewTrainModel()
    {
        if (canSendNewModel.get())
        {
            if (allModelToTrain.size()>0) {
                TrainModelEvent trainModelEvent = new TrainModelEvent(student.getName(), allModelToTrain.poll());
                trainModelEvent.setFuture(sendEvent(trainModelEvent));
                canSendNewModel.set(false);
            }
        }
    }
}
