package bgu.spl.mics.application;



import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.Student.Degree;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;


import java.io.*;

import java.util.LinkedList;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */

public class CRMSRunner {


    public static LinkedList<Student> Students;
    public static Cluster cluster = Cluster.getInstance();
    public static LinkedList<Thread> threads = new LinkedList<>();
    public static LinkedList<MicroService> MicroServices = new LinkedList<>();
    public static LinkedList<ConferenceInformation> ConferenceInformationList;
    public static LinkedList<Model> models;
    public static LinkedList<GPU> GPUS;
    public static LinkedList<CPU> CPUS;
    private static int duration;
    private static int tickTime;


    public static void main(String[] args) throws IOException {
        //file reading
        System.out.println("Start program");
        File input = new File(args[0]);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();

            Students = new LinkedList<>();
            ConferenceInformationList = new LinkedList<>();
            models = new LinkedList<>();
            GPUS = new LinkedList<>();
            CPUS = new LinkedList<>();

            //Students
            JsonArray jsonArrayStudents = fileObject.get("Students").getAsJsonArray();
            for (JsonElement student : jsonArrayStudents) {
                JsonObject studentObj = student.getAsJsonObject();
                String name = studentObj.get("name").getAsString();
                String department = studentObj.get("department").getAsString();
                String statusString = studentObj.get("status").getAsString();
                Degree status = Degree.valueOf(statusString);
                JsonArray jsonArrayModel = studentObj.get("models").getAsJsonArray();
                Student student1 = new Student(name, department, status);
                Students.add(new Student(name, department, status));


                //Models
                for (JsonElement model : jsonArrayModel) {
                    JsonObject modelObj = model.getAsJsonObject();
                    String nameModel = modelObj.get("name").getAsString();
                    String typeString = modelObj.get("type").getAsString();
                    Data.Type type = Data.Type.valueOf(typeString);
                    int size = modelObj.get("size").getAsInt();
                    //Data
                    Data data = new Data(type, size);
                    models.add(new Model(nameModel, data, student1));
                }


            }

            // divide the models between students
            for (Model m : models)
            {
                for (Student s : Students)
                {
                    if (m.getStudent().getName().equals(s.getName()))
                        s.addModel(m);
                }
            }
            Cluster.getInstance().setAllModels(models);
            //GPU
            JsonArray GPUsArray = fileObject.get("GPUS").getAsJsonArray();
            for (int i = 0; i < GPUsArray.size(); i++) {
                String typeStringg = GPUsArray.get(i).getAsString();
                GPU.Type typeGPU = GPU.Type.valueOf(typeStringg);
                GPUS.add(new GPU(typeGPU, cluster));
            }

            //CPU
            JsonArray CPUsArray = fileObject.get("CPUS").getAsJsonArray();
            for (int i = 0; i < CPUsArray.size(); i++)
                CPUS.add(new CPU(CPUsArray.get(i).getAsInt(), cluster));


            // ConferenceInformation
            JsonArray jsonArrayConferences = fileObject.get("Conferences").getAsJsonArray();
            for (JsonElement conference : jsonArrayConferences) {
                JsonObject ConferenceObj = conference.getAsJsonObject();
                String nameConference = ConferenceObj.get("name").getAsString();
                int date = ConferenceObj.get("date").getAsInt();
                ConferenceInformationList.add(new ConferenceInformation(nameConference, date));

            }
            //tickTime
            tickTime = fileObject.get("TickTime").getAsInt();

            //Duration
            duration = fileObject.get("Duration").getAsInt();

            start();
            for(int i = 0 ; i < threads.size() ; i++)
                threads.get(i).start();
            for(int i = 0 ; i < threads.size() ; i++)
                threads.get(i).join();
            threads.clear();



            //NEED TO CHECK IF PROGRAM END , AND THAN PRINT ALL

            /*
            for(Student s : Students)
                System.out.println(s.toString());
            System.out.println(Students.size() + " students size");
            for(Model m : models)
                System.out.println(m.toString());
            System.out.println(models.size() + " models size");
            for(GPU G : GPUS)
                System.out.println(G.toString());
            System.out.println(GPUS.size() + " GPUS size");

            for(CPU G : CPUS)
                System.out.println(G.toString());
            System.out.println(CPUS.size() + " CPUS size");


            for(ConferenceInformation c :ConferenceInformationList)
                System.out.println(c.toString());
              System.out.println("tickTime :" + tickTime);
              System.out.println("duration :" + duration);
 */
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void start() throws IOException {
        //start countdown
        //new CountDown(duration);


        TimeService timeService = new TimeService(tickTime, duration);
        MicroServices.add(timeService);
        cluster.addCPUs(CPUS);
        cluster.addGPUs(GPUS);
        cluster.addConferences(ConferenceInformationList);



        for (Student student : Students) {
            StudentService studentService = new StudentService(student.getName(), student);
            student.setService(studentService);
            MicroServices.add(studentService);
        }
        for (GPU gpu : GPUS) {
            GPUService gpuService = new GPUService(gpu);
            gpu.setService(gpuService);
            MicroServices.add(gpuService);
        }
        for (CPU cpu : CPUS) {
            CPUService cpuService = new CPUService("CPUservice" , cpu);
            cpu.setService(cpuService);
            MicroServices.add(cpuService);
        }
        for (ConferenceInformation con : ConferenceInformationList) {
            ConferenceService conferenceService = new ConferenceService(con.getName(), con);
            con.setService(conferenceService);
            MicroServices.add(conferenceService);
        }

        for (int i = 0; i < MicroServices.size(); i++) {
            Thread thread = new Thread(MicroServices.get(i));
            threads.add(thread);
        }

    }




    public static void exit() throws IOException {
        System.out.println("End program - results");
        for (Model m : Cluster.getInstance().getAllModels())
        {
            if (m.getStudent().getPublications()>0 ||m.getStudent().getPapersRead()>0 )
            {
                for (Student s : Students)
                {
                    if (s.getName().equals(m.getStudent().getName())) {
                        if (m.getStudent().getPublications()>0)
                            s.updatePublications();
                        if(m.getStudent().getPapersRead()>0)
                            s.setPapersRead(m.getStudent().getPapersRead());
                    }
                }
            }
        }
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter("output.json");
            String studentString = "Students: ";
            gson.toJson(studentString,writer);
            for(Student student : Students)
                gson.toJson(student, writer);
            String conferenceString = "Conferences: ";
            gson.toJson(conferenceString,writer);
            for(ConferenceInformation conference : ConferenceInformationList)
                gson.toJson(conference, writer);


            String s0 = "BatchesProcessed: ";
            gson.toJson(s0,writer);
            gson.toJson(cluster.getBatchesProcessed(),writer);


            String s1 = "CPU_TimeUsed: ";
            gson.toJson(s1,writer);
            gson.toJson(cluster.getCpuTimeUsed(),writer);


            String s2 = "GPU_TimeUsed :";
            gson.toJson(s2,writer);
            gson.toJson(cluster.getGpuTimeUsed(),writer);

            writer.flush();
            writer.close();
        }
        catch (Exception e) {}
        System.exit(0);
    }

}