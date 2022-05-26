package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcasts.TickBroadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Events.FinishedTestModelEvent;
import bgu.spl.mics.Events.FinishedTrainModelEvent;
import bgu.spl.mics.Events.TestModelEvent;
import bgu.spl.mics.Events.TrainModelEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link: TestModelEvent},
 * in addition to sending the {@link: DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    private Queue<Event> EventsQ = new LinkedList<Event>();
    private boolean trainModel = false;
    private final List<TestModelEvent> testModelEventList = new ArrayList<>();
    public Object TestLock = new Object();
    public GPUService(GPU gpu) {
        super("GPUService");
        this.gpu = gpu;
    }


    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class ,(TrainModelEvent event)->
                {
                    if(!trainModel) {
                        TrainModelEvent currentTrainEvent;
                        if (EventsQ.size()!=0)
                        {
                            EventsQ.add(event);
                            currentTrainEvent = (TrainModelEvent) EventsQ.remove();
                        }
                        else
                        {
                            currentTrainEvent = event;
                        }
                        GPU current = null;
                        try {
                           current = Cluster.getInstance().getCurrentGPU();
                        }
                        catch (Exception e){};
                        //current.prepareBatches(current.getData());
                        current.prepareBatches(currentTrainEvent.getModel().getData());
                        try {
                            current.process(currentTrainEvent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        trainModel = false;
                        complete(currentTrainEvent, currentTrainEvent.getModel());
                        setFuturesOfEvent(currentTrainEvent,currentTrainEvent.getFuture());
                        sendEvent(new FinishedTrainModelEvent(event));
                    }
                    else
                    {
                        EventsQ.add(event);
                    }
                }
        );
        subscribeEvent(TestModelEvent.class ,(TestModelEvent event)->
                {
                        if (!testModelEventList.contains(event)) {
                            event.TestModel();
                            testModelEventList.add(event);
                            setFuturesOfEvent(event, event.getFuture());
                            sendEvent(new FinishedTestModelEvent(event));
                        }


                }


        );
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast event) ->
                {
                    gpu.updateTick();

                }
        );

    }

}
