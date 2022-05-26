package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcasts.PublishConferenceBroadcast;
import bgu.spl.mics.Broadcasts.TickBroadcast;
import bgu.spl.mics.Events.FinishedPublishResultEvent;
import bgu.spl.mics.Events.PublishResultsEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.ConferenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.List;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConferenceInformation conferenceInformation;
    public ConferenceService(String name , ConferenceInformation conferenceInformation) {
        super(name);
        this.conferenceInformation = conferenceInformation;
    }

    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class , (PublishResultsEvent event)->{
           List<Model> models =  Cluster.getInstance().getAllModels();
            ConferenceInformation cc = Cluster.getInstance().getCurrentConference();
            cc.addResult(event.getFuture().get().getResult());
            for(Model model : models)
            {
                if (model.getResult() == Model.Result.Good) {
                    if (!model.isCheck()) {
                        model.getStudent().updatePublications();
                        cc.addModelName(model.getName());
                        model.setCheck(true);
                    }
                }
            }
            Cluster.getInstance().setConferences(cc);
                    FinishedPublishResultEvent pubRes = new FinishedPublishResultEvent(event);
                    sendEvent(pubRes);
        }
                );
        subscribeBroadcast(TickBroadcast.class , (TickBroadcast tick)-> {
                conferenceInformation.updateTick();
                if (conferenceInformation.getTick() == conferenceInformation.getDate()) {
                        int index = Cluster.getInstance().getConferences().size();
                        ConferenceInformation c = Cluster.getInstance().getCurrentConference();
                        index--;
                        while (c.getModels().size()==0 && index>0)
                        {
                            Cluster.getInstance().setConferences(c);
                            c = Cluster.getInstance().getCurrentConference();
                            index--;
                        }

                        PublishConferenceBroadcast broadcast1 = new PublishConferenceBroadcast(getName(),c.getResults());
                        Cluster.getInstance().setConferences(c);
                        sendBroadcast(broadcast1);
                        unregister(this);
                    }
        }
        );
    }
}
