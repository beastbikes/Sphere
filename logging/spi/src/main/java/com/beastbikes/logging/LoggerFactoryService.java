package com.beastbikes.logging;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.beastbikes.logging.spi.LoggerFactory;

class LoggerFactoryService {

    private static LoggerFactoryService instance;

    public static final synchronized LoggerFactoryService getInstance() {
        if (null == instance) {
            instance = new LoggerFactoryService();
        }

        return instance;
    }

    private final ServiceLoader<LoggerFactory> loader;

    private LoggerFactoryService() {
        this.loader = ServiceLoader.load(LoggerFactory.class);
    }

    public final Logger getLogger(final String name) {
        Logger logger = null;

        final Iterator<LoggerFactory> i = this.loader.iterator();
        while (i.hasNext()) {
            if (null != (logger = i.next().newLogger(name))) {
                return logger;
            }
        }

        return new StandardOutputLogger(name);
    }

}
