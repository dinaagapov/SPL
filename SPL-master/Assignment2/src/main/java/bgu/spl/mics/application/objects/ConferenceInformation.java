package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConferenceInformation {

    private String name;
    private transient int date;
    private LinkedList<String> modelsNames = new LinkedList<>();
    private transient int tick;
    private transient List Results = new ArrayList<Model.Result>();
    private transient LinkedList<Model> models = new LinkedList<>();
    private transient ConferenceService service;

    public ConferenceInformation(String name , int date){
        this.name = name;
        this.date = date;
    }

    public void setService(ConferenceService service) {
        this.service = service;
    }
    public void  updateTick(){
        tick = tick + 1;
    }
    public int getTick(){
        return tick;
    }
    public LinkedList<Model> getModels(){
        return models;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDate() {
        return date;
    }

    public LinkedList<Model> getModel(){
        return models;
    }
    public void setDate(int date) {
        this.date = date;
    }

    public void addResult(Model.Result m)
    {
        if (m == Model.Result.Good && !Results.contains(m))
            Results.add(m);
    }
    public void addModelName(String s){
        modelsNames.add(s);
    }

    public List getResults() {
        return Results;
    }

    @Override
    public String toString() {
        return "ConfrenceInformation{" +
                "name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
