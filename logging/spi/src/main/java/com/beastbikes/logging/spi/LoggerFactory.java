package com.beastbikes.logging.spi;

import com.beastbikes.logging.Logger;

/**
 * The {@link Logger} factory
 *
 * @author johnsonlee
 */
public interface LoggerFactory {

    /**
     * Create a logger with the specified name
     *
     * @param name
     *           The logger name
     * @return a logger with the specified name
     */
    public Logger newLogger(final String name);

}
