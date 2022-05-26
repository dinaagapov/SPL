package bgu.spl.mics.Broadcasts;


import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private long currentTick;
    private int speed;


    public TickBroadcast(long tick, int speed) {
        currentTick = tick;
        this.speed = speed;
    }

    public long getCurrentTick(){
        return currentTick;
    }
    public int getSpeed(){
        return speed;
    }

}
