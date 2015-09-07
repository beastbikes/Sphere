package com.beastbikes.logging;

public abstract class LoggerFactory {

    public static final Logger getLogger(final Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static final Logger getLogger(String name) {
        return LoggerFactoryService.getInstance().getLogger(name);
    }

    private LoggerFactory() {
    }

}
