package Tests;

import bgu.spl.mics.application.objects.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class CPUTest {
    private static CPU cpu;
    private static Data data;
    private static GPU gpu;

    @Before
    public void setUp() throws Exception {
        Cluster cluster = Cluster.getInstance();

        cpu = new CPU(32,Cluster.getInstance());
        data = new Data(Data.Type.Images ,1000);
        gpu = new GPU(GPU.Type.RTX2080 , cluster);
    }

    @After
    public void tearDown() throws Exception {
        cpu = null;
    }
    @Test
    public void receiveDataBatch() {
        DataBatch dataBatch = new DataBatch(data,8,gpu,0);
        cpu.receiveDataBatch(dataBatch);
        assertEquals(1,cpu.numOfBathes());
        assertEquals(4,cpu.getTotalTime());
        assertEquals(dataBatch.getTimeToTrainLeft(),-1);
        assertEquals(4,dataBatch.getTimeToProcessInTheCpu());
    }
    @Test
    public void updateTick() {
        cpu.updateTick();
        assertEquals(1, cpu.getTick());
        int currentTick = cpu.getTick();
        assertEquals(currentTick + 1, 2);

    }
}