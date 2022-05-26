package bgu.spl.mics.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event {
    private String senderName;
    private Future<Model> future;



    public PublishResultsEvent(String sn , FinishedTestModelEvent event)
    {
        senderName = sn;
        future = event.getTestedModelEvent().getFuture();
    }

    public Future<Model> getFuture() {
        return future;
    }
    public void setFuture(Future<Model> future) {
        this.future = future;
    }
}
