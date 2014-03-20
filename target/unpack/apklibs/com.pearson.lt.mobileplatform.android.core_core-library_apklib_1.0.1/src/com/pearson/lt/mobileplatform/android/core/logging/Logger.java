package com.pearson.lt.mobileplatform.android.core.logging;

import android.util.Log;

import java.util.Locale;

public final class Logger {

    public static LogLevel LOG_LEVEL = LogLevel.INFO;

    private Logger() {
    }

    /**
     * Checks if the current log level is loggable.
     *
     * @param level The level to check against Logger.LOG_LEVEL.
     * @return true if this level is loggable; otherwise false.
     */
    public static boolean isLoggable(LogLevel level) {
        if (LOG_LEVEL == null || LOG_LEVEL == LogLevel.SUPPRESS) {
            return false;
        } else if (LOG_LEVEL == LogLevel.SHOW_ALL) {
            return true;
        }

        return (LOG_LEVEL.getValue() <= level.getValue());
    }

    /**
     * Enables all log levels for output.
     */
    public static void enableAll() {
        LOG_LEVEL = LogLevel.SHOW_ALL;
    }

    /**
     * Disables all log levels for output.
     */
    public static void disableAll() {
        LOG_LEVEL = LogLevel.SUPPRESS;
    }

    /**
     * Send a {@link #VERBOSE} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (isLoggable(LogLevel.VERBOSE)) {
            Log.v(tag, buildMessage(msg));
        }
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void v(String tag, String msg, Throwable tr) {
        if (isLoggable(LogLevel.VERBOSE)) {
            Log.v(tag, buildMessage(msg), tr);
        }
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (isLoggable(LogLevel.DEBUG)) {
            Log.d(tag, buildMessage(msg));
        }
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (isLoggable(LogLevel.DEBUG)) {
            Log.d(tag, buildMessage(msg), tr);
        }
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (isLoggable(LogLevel.INFO)) {
            Log.i(tag, buildMessage(msg));
        }
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (isLoggable(LogLevel.INFO)) {
            Log.i(tag, buildMessage(msg), tr);
        }
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (isLoggable(LogLevel.WARN)) {
            Log.w(tag, buildMessage(msg));
        }
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (isLoggable(LogLevel.WARN)) {
            Log.w(tag, buildMessage(msg), tr);
        }
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param tr An exception to log
     */
    public static void w(String tag, Throwable tr) {
        if (isLoggable(LogLevel.WARN)) {
            Log.w(tag, tr);
        }
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (isLoggable(LogLevel.ERROR)) {
            Log.e(tag, buildMessage(msg));
        }
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (isLoggable(LogLevel.ERROR)) {
            Log.e(tag, buildMessage(msg), tr);
        }
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen.
     * The error will always be logged at level ASSERT with the call stack.
     * Depending on system configuration, a report may be added to the
     * {@link android.os.DropBoxManager} and/or the process may be terminated
     * immediately with an error dialog.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    public static void wtf(String tag, String msg) {
        if (isLoggable(LogLevel.WTF)) {
            Log.wtf(tag, buildMessage(msg));
        }
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, String)}, with an exception to log.
     *
     * @param tag Used to identify the source of a log message.
     * @param tr  An exception to log.
     */
    public static void wtf(String tag, Throwable tr) {
        if (isLoggable(LogLevel.WTF)) {
            Log.wtf(tag, tr);
        }
    }

    /**
     * Formats the caller's provided message and prepends useful info like
     * calling thread ID and method name.
     */
    private static String buildMessage(String message) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        // Walk up the stack looking for the first caller outside of VolleyLog.
        // It will be at least two frames up, so start there.
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(Logger.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s",
                Thread.currentThread().getId(), caller, message);
    }

    public enum LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR, WTF, SUPPRESS, SHOW_ALL;

        private int getValue() {
            return 1 << ordinal();
        }

        @Override
        public String toString() {
            return name() + "(" + getValue() + ")";
        }
    }


}
