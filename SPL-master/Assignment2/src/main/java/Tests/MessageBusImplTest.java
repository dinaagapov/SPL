package Tests;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.example.ExampleMicroService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    MessageBusImpl mbi;

    @Before
    public void setUp() throws Exception {
        mbi= MessageBusImpl.getInstance();
    }

    @Test
    //subscribe event e to microservice m
    public void subscribe_Not_Null_Event() {
        ExampleEvent ee = new ExampleEvent("Tal Galmor");
        ExampleMicroService ems = new ExampleMicroService("Example");
        mbi.subscribeEvent(ee.getClass(),ems);
        assertNotEquals(0, mbi.NumOfClassRegistered(ee.getClass()));
    }
    @Test
    //subscribe event e to microservice m
    public void subscribe_Null_Class_Event() {
        ExampleEvent ee = new ExampleEvent("Tal Galmor");
        ExampleMicroService ems = new ExampleMicroService("Example");
        mbi.subscribeEvent(null,ems);
        assertEquals(0, mbi.NumOfClassRegistered(ee.getClass()));

    }
    @Test
    //subscribe event e to microservice m
    public void subscribe_Null_MicroService_Event() {
        ExampleEvent ee = new ExampleEvent("Tal Galmor");
        mbi.subscribeEvent(ee.getClass(),null);
        assertEquals(0, mbi.NumOfClassRegistered(ee.getClass()));

    }
    @Test
    //subscribe broadcast e to microservice m
    public void subscribe_Not_Null_Broadcast() {
        ExampleBroadcast ee = new ExampleBroadcast("Tal Galmor");
        ExampleMicroService ems = new ExampleMicroService("Example");
        mbi.subscribeBroadcast(ee.getClass(),ems);
        assertNotEquals(0, mbi.NumOfClassRegistered(ee.getClass()));

    }
    @Test
    //subscribe broadcast e to microservice m
    public void subscribe_Null_Class_Broadcast() {
        ExampleBroadcast ee = new ExampleBroadcast("Tal Galmor");
        ExampleMicroService ems = new ExampleMicroService("Example");
        mbi.subscribeBroadcast(null,ems);
        assertEquals(0, mbi.NumOfClassRegistered(ee.getClass()));

    }
    @Test
    //subscribe broadcast e to microservice m
    public void subscribe_Null_MicroService_Broadcast() {
        ExampleBroadcast ee = new ExampleBroadcast("Tal Galmor");
        mbi.subscribeBroadcast(ee.getClass(),null);
        assertEquals(0, mbi.NumOfClassRegistered(ee.getClass()));

    }

    @Test
    public void complete_Not_Null() {
        ExampleEvent e1 = new ExampleEvent("e1");
        mbi.EnterNewMessage(e1);
        mbi.complete(e1,"process");
        assertNotNull(mbi.getFutureOfEvent(e1));
    }
    @Test
    public void complete_Null() {
        ExampleEvent e1 = new ExampleEvent("e1");
        mbi.EnterNewMessage(e1);
        mbi.complete(e1,null);
        assertNull(mbi.getFutureOfEvent(e1));
    }

    @Test
    public void send_Not_Null_Broadcast() {
        ExampleBroadcast b = new ExampleBroadcast("1");
        ExampleMicroService e1 = new ExampleMicroService("e1");
        ExampleMicroService e2 = new ExampleMicroService("e2");
        mbi.register(e1);
        mbi.register(e2);
        mbi.sendBroadcast(b);
        assertTrue(mbi.NumOfMessagesInMs(e1.getId()) == 1  && mbi.NumOfMessagesInMs(e2.getId()) == 1  );

    }

    @Test
    public void send_Null_Broadcast() {
        ExampleMicroService e1 = new ExampleMicroService("e1");
        ExampleMicroService e2 = new ExampleMicroService("e2");
        mbi.register(e1);
        mbi.register(e2);
        mbi.sendBroadcast(null);
        assertTrue(mbi.NumOfMessagesInMs(e1.getId()) == 0  &&mbi.NumOfMessagesInMs(e2.getId()) == 0  );



    }

    @Test
    public void send_Not_Null_Event() {
        ExampleMicroService e1 = new ExampleMicroService("e1");
        ExampleMicroService e2 = new ExampleMicroService("e2");
        ExampleEvent ee = new ExampleEvent("ee");
        mbi.register(e1);
        mbi.register(e2);
        mbi.sendEvent(ee);
        assertTrue(mbi.NumOfMessagesInMs(e1.getId()) == 1  &&mbi.NumOfMessagesInMs(e2.getId()) == 0  );

    }
    @Test
    public void send_Null_Event() {
        ExampleMicroService e1 = new ExampleMicroService("e1");
        ExampleMicroService e2 = new ExampleMicroService("e2");
        mbi.register(e1);
        mbi.register(e2);
        mbi.sendEvent(null);
        assertTrue(mbi.NumOfMessagesInMs(e1.getId()) == 0  &&mbi.NumOfMessagesInMs(e2.getId()) == 0  );


    }

    @Test
    //allocate a massage-q for the microservice m
    public void register_NOT_NULL_Microservice() {
        ExampleMicroService ems = new ExampleMicroService("Example micro service");
        mbi.register(ems);
        assertTrue(mbi.isMicroServiceRegistered(ems.getId()));
    }
    @Test
    //allocate a massage-q for the microservice m
    public void register_Duplicate_Microservice() {
        ExampleMicroService ems1 = new ExampleMicroService("Example micro service");
        mbi.register(ems1);
        mbi.register(ems1);
        assertEquals(mbi.NumberOfRegisteredMS(), 1);
    }
    @Test
    //allocate a massage-q for the microservice m
    public void register_NULL_Microservice() {
        mbi.register(null);
        assertEquals(mbi.NumberOfRegisteredMS(), 0);
    }


    @Test
    //remove massage q allocated to m via the call register and cleans all references related to m
    public void unregister_Exists_MicroService() throws Exception {
        ExampleMicroService ems = new ExampleMicroService("Example micro service");
        mbi.register(ems);
        mbi.unregister(ems);
        assertEquals(mbi.NumberOfRegisteredMS(), 0);
    }
    @Test
    public void unregister_Null_MicroService() throws Exception {
        ExampleMicroService ems = new ExampleMicroService("Example micro service");
        mbi.unregister(ems);
        assertEquals(mbi.NumberOfRegisteredMS(), 0);

    }
    @Test
    public void unregister_Not_Exists_MicroService() throws Exception {
        ExampleMicroService ems = new ExampleMicroService("Example micro service");
        ExampleMicroService ems2 = new ExampleMicroService("Example micro service");
        mbi.register(ems);
        mbi.unregister(ems2);
        assertEquals(mbi.NumberOfRegisteredMS(), 1);
    }


    @Test
    public void awaitMessage_Without_Exception_With_Message() {
        ExampleMicroService ems = new ExampleMicroService("Example micro service");
        ExampleEvent eE = new ExampleEvent("sender");
        mbi.register(ems);
        mbi.subscribeEvent(eE.getClass(),ems);
        try {
            mbi.awaitMessage(ems);
        }
        catch (Exception e)
        {
            fail();
        }

    }
    @Test
    public void awaitMessage_Without_Exception_Without_Message() {
        ExampleMicroService ems = new ExampleMicroService("Example micro service");
        mbi.register(ems);
        try {
            mbi.awaitMessage(ems);
        }
        catch (Exception e)
        {
            fail();
        }

    }
    @Test
    public void awaitMessage_With_Exception() {
        ExampleMicroService ems = new ExampleMicroService("Example micro service");
        try {
            mbi.awaitMessage(ems);
            fail();
        }
        catch (Exception ignored)
        {
        }
        assertEquals(0, 0);
    }


}