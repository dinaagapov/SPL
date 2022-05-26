package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcasts.TickBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

/**
 * CPU service is responsible for handling the {@link: DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    CPU cpu ;
    public CPUService(String name ,CPU cpu) {
        super("CPUService");
        this.cpu = cpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast event) ->
                {
                        cpu.updateTick();
                }
        );
    }
}
