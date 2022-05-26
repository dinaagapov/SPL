package bgu.spl.mics.example;

import bgu.spl.mics.MicroService;

public class ExampleMicroService extends MicroService {
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public ExampleMicroService(String name) {
        super(name);
    }

    /**
     * this method is called once when the event loop starts.
     */
    @Override
    protected void initialize() {

    }
}
