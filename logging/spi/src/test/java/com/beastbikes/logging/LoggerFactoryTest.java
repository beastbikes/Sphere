package com.beastbikes.logging;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LoggerFactoryTest extends TestCase {

    public static Test suite() {
        return new TestSuite(LoggerFactoryTest.class);
    }

    public LoggerFactoryTest(final String testName) {
        super(testName);
    }

    public void testGetLogger() {
        final PrintStream stdout = System.out;
        final PrintStream stderr = System.err;
        final ByteArrayOutputStream bufout = new ByteArrayOutputStream();
        final ByteArrayOutputStream buferr = new ByteArrayOutputStream();
        final PrintStream logout = new PrintStream(bufout);
        final PrintStream logerr = new PrintStream(buferr);
        System.setOut(logout);
        System.setErr(logerr);

        final Logger logger = LoggerFactory.getLogger("Test");
        assertTrue(null != logger);
        assertEquals("Test", logger.getName());

        bufout.reset();
        logger.trace("Hello, Sphere!");
        assertEquals("V/Test\tHello, Sphere!", bufout.toString());

        bufout.reset();
        logger.trace("Hello, %s!", "Sphere");
        assertEquals("V/Test\tHello, Sphere!", bufout.toString());

        bufout.reset();
        logger.info("Hello, Sphere!");
        assertEquals("I/Test\tHello, Sphere!", bufout.toString());

        bufout.reset();
        logger.info("Hello, %s!", "Sphere");
        assertEquals("I/Test\tHello, Sphere!", bufout.toString());

        bufout.reset();
        logger.warn("Hello, Sphere!");
        assertEquals("W/Test\tHello, Sphere!", bufout.toString());

        bufout.reset();
        logger.warn("Hello, %s!", "Sphere");
        assertEquals("W/Test\tHello, Sphere!", bufout.toString());

        buferr.reset();
        logger.error("Hello, Sphere!");
        assertEquals("E/Test\tHello, Sphere!", buferr.toString());

        buferr.reset();
        logger.error("Hello, %s!", "Sphere");
        assertEquals("E/Test\tHello, Sphere!", buferr.toString());

        System.setOut(stdout);
        System.setErr(stderr);
    }

}
