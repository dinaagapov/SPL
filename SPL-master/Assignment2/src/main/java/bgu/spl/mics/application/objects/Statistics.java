package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;

public class Statistics {
    private static class StatisticsHolder{
        private static Statistics instance = new Statistics();
    }
    private Cluster cluster = Cluster.getInstance();

    public Statistics() {
    }
    public static Statistics getInstance() {
        return StatisticsHolder.instance;
    }

    public HashMap<String ,String> TrainedModelName(){
       return cluster.getStudentsTrainedModels();
    }

    public int getBatchesProcessed(){
        return cluster.getBatchesProcessed();
    }

    public int getGpuTimeUsed(){
        return cluster.getGpuTimeUsed();
    }

    public int getCpuTimeUsed(){
        return cluster.getCpuTimeUsed();
    }


}