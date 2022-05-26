package bgu.spl.mics.Broadcasts;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.List;

public class PublishConferenceBroadcast implements Broadcast {
    private String senderName;
    private List<Model> aggregatedResults;

    public PublishConferenceBroadcast(String theSender, List<Model> agrees)
    {
        senderName = theSender;
        aggregatedResults = agrees;
    }

    public List<Model> getAggregatedResults()
    {
        return aggregatedResults;
    }

    public String getSenderName() {
        return senderName;
    }
}
