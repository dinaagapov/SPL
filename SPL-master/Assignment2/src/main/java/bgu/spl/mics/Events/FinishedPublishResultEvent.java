package bgu.spl.mics.Events;

import bgu.spl.mics.Event;

public class FinishedPublishResultEvent implements Event {
    private PublishResultsEvent thePublishEvent;
    public FinishedPublishResultEvent(PublishResultsEvent event){this.thePublishEvent = event;}

    public PublishResultsEvent getThePublishEvent() {
        return thePublishEvent;
    }
}
