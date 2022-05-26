package bgu.spl.mics.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.Events.TrainModelEvent;

public class FinishedTrainModelEvent implements Event {
    private TrainModelEvent theTrainedModel;

    public FinishedTrainModelEvent(TrainModelEvent event)
    {
        theTrainedModel = event;
    }

    public TrainModelEvent getTheTrainedModel() {
        return theTrainedModel;
    }
}
