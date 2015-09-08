package com.beastbikes.logging;

import java.io.PrintWriter;

class StandardOutputLogger extends AbstractLogger {

    private final PrintWriter stdout;

    private final PrintWriter stderr;

    StandardOutputLogger(final String name) {
        super(name);
        this.stdout = new PrintWriter(System.out, true);
        this.stderr = new PrintWriter(System.err, true);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        t.printStackTrace(this.printWithPrefix(this.stdout, "V", msg));
    }

    @Override
    public void trace(final String msg, final Object... args) {
        this.printWithPrefix(this.stdout, "V", msg, args);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        t.printStackTrace(this.printWithPrefix(this.stdout, "D", msg));
    }

    @Override
    public void debug(final String msg, final Object... args) {
        this.printWithPrefix(this.stdout, "D", msg, args);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        t.printStackTrace(this.printWithPrefix(this.stdout, "I", msg));
    }

    @Override
    public void info(final String msg, final Object... args) {
        this.printWithPrefix(this.stdout, "I", msg, args);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        t.printStackTrace(this.printWithPrefix(this.stdout, "W", msg));
    }

    @Override
    public void warn(final String msg, final Object... args) {
        this.printWithPrefix(this.stdout, "W", msg, args);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        t.printStackTrace(this.printWithPrefix(this.stderr, "E", msg));
    }

    @Override
    public void error(final String msg, final Object... args) {
        this.printWithPrefix(this.stderr, "E", msg, args);
    }

    private final PrintWriter printWithPrefix(final PrintWriter pw, final String prefix, final String msg, final Object... args) {
        pw.append(prefix).append('/').append(this.getName()).append('\t').format(msg, args).println();
        return pw;
    }

}
