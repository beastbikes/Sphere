package com.beastbikes.logging.binding.logcat;

import com.beastbikes.logging.Logger;
import com.beastbikes.logging.spi.LoggerFactory;

/**
 * The logcat logger factory
 *
 * @author johnsonlee
 */
public class LogcatLoggerFactory implements LoggerFactory {

    @Override
    public Logger newLogger(final String name) {
        return new LogcatLogger(name);
    }

}
