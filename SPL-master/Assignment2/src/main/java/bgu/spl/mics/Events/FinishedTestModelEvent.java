package bgu.spl.mics.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.Events.TestModelEvent;

public class FinishedTestModelEvent implements Event {
    private TestModelEvent TestedModelEvent;

    public FinishedTestModelEvent (TestModelEvent event)
    {
        TestedModelEvent = event;
    }

    public TestModelEvent getTestedModelEvent() {
        return TestedModelEvent;
    }
}
