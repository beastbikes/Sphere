package com.beastbikes.logging.binding.logcat;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.beastbikes.logging.Logger;
import com.beastbikes.logging.LoggerFactory;

public class LogcatLoggerFactoryTest {

    public void testGetLogger() {
        final Logger logger = LoggerFactory.getLogger("Test");
        assertTrue(null != logger);
        assertEquals("com.beastbikes.logging.binding.logcat.LogcatLogger", logger.getClass().getName());
    }

}
