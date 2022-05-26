package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status {PreTrained, Training, Trained , Tested}
    public enum Result {Good, Bad, None }

    private String name;
    private Data data;
    private transient Student student;
    private Status status;
    private Result result;
    private transient boolean check = false;
    public Model(String name , Data data , Student student) {
        this.name = name;
        this.data = data;
        this.student = student;
        status = Status.PreTrained;
        result = Result.None;
    }


    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Result result) {
        this.result = result;
    }
    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name +
                ", data=" + data.toString() +"/n" +
                ", status=" + status + "/n" +
                ", result=" + result +
                '}';
    }

}
