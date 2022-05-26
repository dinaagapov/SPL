package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.StudentService;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> trainedModels = new ArrayList<>();
    private transient List<Model> models = new ArrayList<>();



    public enum Degree {

        @SerializedName("MSc")
        MSc,
        @SerializedName("PhD")
        PhD
    }
    private transient StudentService service;

    public Student(String name , String department , Degree status){
        this.name = name;
        this.department = department;
        this.status = status;
        publications = 0;
        papersRead = 0;

    }
    public String toString(){
        String trainedModelsString = "";
        for(Model model : trainedModels)
            trainedModelsString = model.toString() + trainedModelsString;
        String output = "name:" + name + "\n"
                + "department:" + department +"\n"
                + "status:" + status +"\n"
                + "papersRead:" + papersRead +"\n"
                + "trainedModels:" + trainedModelsString;

            return output;
    }

    public void updatePublications(){
        this.publications++;
    }
    public void updatePapersRead(){
        papersRead = papersRead + 1;
    }
    public String getName() {
        return name;
    }

    public void setService(StudentService service) {
        this.service = service;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStatus(Degree status) {
        this.status = status;
    }

    public void setPublications(int publications) {
        this.publications = publications;
    }

    public void setPapersRead(int papersRead) {
        this.papersRead = papersRead;
    }

    public void HandleModel(Model newOrUpdateModel)
    {
        Model needToUpdateModel = null;
        for (Model exm:trainedModels) {
            if (exm.getName().equals(newOrUpdateModel.getName()) && exm.getStudent() == newOrUpdateModel.getStudent())
                needToUpdateModel = exm;
        }
        if (needToUpdateModel!=null)
        {
            trainedModels.remove(needToUpdateModel);
        }
        trainedModels.add(newOrUpdateModel);


    }
    public List<Model> getModels() {
        return models;
    }
    public void addModel (Model m)
    {
        models.add(m);
    }

}
