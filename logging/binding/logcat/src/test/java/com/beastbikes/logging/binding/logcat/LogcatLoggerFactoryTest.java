package com.beastbikes.logging.binding.logcat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.beastbikes.logging.Logger;
import com.beastbikes.logging.LoggerFactory;

public class LogcatLoggerFactoryTest extends TestCase {

    public static Test suite() {
        return new TestSuite(LogcatLoggerFactoryTest.class);
    }

    public LogcatLoggerFactoryTest(final String testName) {
        super(testName);
    }

    public void testGetLogger() {
        final Logger logger = LoggerFactory.getLogger("Test");
        assertTrue(null != logger);
        assertEquals("com.beastbikes.logging.binding.logcat.LogcatLogger", logger.getClass().getName());
    }

}
