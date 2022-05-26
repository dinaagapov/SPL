package Tests;

import bgu.spl.mics.application.objects.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class GPUTest {
    private static GPU gpu;
    private static Data data;

    @Before
    public void setUp() throws Exception {
        Cluster cluster = Cluster.getInstance();
        Data data = new Data(Data.Type.Images ,1000);
        Student Dina = new Student("Dina" , "department" , Student.Degree.PhD);
        Model model = new Model("TrainingModel" , data , Dina );
        gpu = new GPU(GPU.Type.RTX2080 , cluster);
    }

    @After
    public void tearDown() throws Exception {
        gpu = null;
    }

    @Test
    public void prepareBatch() {
        data = new Data(Data.Type.Images,2000);
        gpu.prepareBatches(data);
        assertEquals(gpu.getDataBatchesNumber(),2);

    }
    @Test
    public void sendUnProcessData() {
        gpu.sendToCPU();
    }
    @Test
    public void receiveProcessData() {
        DataBatch processedData = new DataBatch(data,0,gpu,0);
        gpu.receiveProcessed(processedData);
        assertEquals(gpu.getTrainedQ().size(),1);

    }
    @Test
    public void receiveTick() {
        int tickCurrent = gpu.getTick();
        System.out.println(tickCurrent);
        DataBatch processedData = new DataBatch(data,0,gpu,0);
        gpu.getTrainedQ().add(processedData);
        gpu.updateTick();
        System.out.println(gpu.getTick());
        assertEquals(tickCurrent+1  , gpu.getTick() );

    }


}