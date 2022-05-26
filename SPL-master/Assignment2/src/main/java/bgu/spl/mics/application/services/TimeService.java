package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcasts.TickBroadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
    public static Timer timer;
    public static int duration;
    public static int CurrentTick = 0;
    public static int speed;

    public TimeService(int speed, int duration) {
        super("TimeService");
        TimeService.speed =speed;
        TimeService.duration =duration;
        timer = new Timer();
        timer.schedule(new DisplayCountdown(), 0, 1000);
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class,(TickBroadcast tick)-> {
            if(duration>0) {
                try {
                    Thread.currentThread().sleep(speed);
                    DisplayCountdown.setSeconds(speed);
                    sendBroadcast(new TickBroadcast((long)CurrentTick, speed));
                } catch (InterruptedException ignore) {}
            }
            if(duration <= 0){
                terminate();
                DisplayCountdown.setSeconds(speed);
            }
        });
        sendBroadcast(new TickBroadcast(duration,speed));
    }


}
class DisplayCountdown extends TimerTask {
    /**
     * The action to be performed by this timer task.
     */
    public DisplayCountdown()
    {

    }
    @Override
    public void run() {
        //System.out.println("Seconds left " + TimeService.duration);

    }
    public static synchronized void setSeconds(int speed) throws IOException {
        if (TimeService.duration <= 0) {
            CRMSRunner.exit();
            TimeService.timer.cancel();
        }
        TimeService.CurrentTick = TimeService.CurrentTick+1;
        TimeService.duration = TimeService.duration-1;
    }
}