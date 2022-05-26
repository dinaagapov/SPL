package bgu.spl.mics.Events;


import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.GPUService;

public class TestModelEvent implements Event {

    private Model.Result result;
    private Future<Model> future = null;
    Student student;
    public TestModelEvent(Student student ,Future<Model> future )
    {
        this.student = student;
        this.future = future;
    }
    public Model.Result getResult()
    {
        return result;
    }

    public Future<Model> getFuture() {
        return future;
    }
    public void setFuture(Future<Model> future) {
        this.future = future;
    }
    public void TestModel()
    {
        int odd;
        if (student.getStatus()== Student.Degree.MSc)
            odd = 6; //0.6
        else
            odd = 8; //0.8
        if ((int)(Math.random()*10) <=odd)
            result = Model.Result.Good;
        else
            result = Model.Result.Bad;

        future.get().setResult(result);
    }
}
