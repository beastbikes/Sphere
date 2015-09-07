package com.beastbikes.logging.binding.logcat;

import android.util.Log;

import com.beastbikes.logging.AbstractLogger;
import com.beastbikes.logging.Logger;

/**
 * Android logcat implementation for {@link Logger} interface
 *
 * @author johnsonlee
 */
class LogcatLogger extends AbstractLogger {

    LogcatLogger(final String name) {
        super(name);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        Log.v(this.getName(), msg, t);
    }

    @Override
    public void trace(final String msg, final Object... args) {
        Log.v(this.getName(), String.format(msg, args));
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        Log.d(this.getName(), msg, t);
    }

    @Override
    public void debug(final String msg, final Object... args) {
        Log.d(this.getName(), String.format(msg, args));
    }

    @Override
    public void info(final String msg, final Throwable t) {
        Log.i(this.getName(), msg, t);
    }

    @Override
    public void info(final String msg, final Object... args) {
        Log.i(this.getName(), String.format(msg, args));
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        Log.w(this.getName(), msg, t);
    }

    @Override
    public void warn(final String msg, final Object... args) {
        Log.w(this.getName(), String.format(msg, args));
    }

    @Override
    public void error(final String msg, final Throwable t) {
        Log.e(this.getName(), msg, t);
    }

    @Override
    public void error(final String msg, final Object... args) {
        Log.e(this.getName(), String.format(msg, args));
    }

}
