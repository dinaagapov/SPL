package Tests;

import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {


    private static Future future ;
    @Before
    public void setUp() throws Exception {
        future = new Future();
    }
    @After
    public void tearDown() throws Exception {
        future = null;
    }

    @Test
    public void get() {
        assertNull(future.get());
    }

    @Test
    public void resolve() {
        String s = "test";
        future.resolve(s);
        assertNotNull(future.get());

    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        String s = "test";
        future.resolve(s);
        assertTrue(future.isDone());

    }

    @Test
    public void get2() {
        assertEquals(future.get(200, TimeUnit.MILLISECONDS), null);
        String s = "test";
        future.resolve(s);
        assertEquals(future.get(200, TimeUnit.MILLISECONDS), true);

    }
}