
package logger;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

public class Logger {
    public static enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static LogLevel loglevel = LogLevel.INFO;
    private static boolean isEnabled = false;
    private static ArrayList<ILogDestination> logDestinations = new ArrayList<ILogDestination>();

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void enableLogging() {
        isEnabled = true;
    }

    public static void disableLogging() {
        isEnabled = false;
    }

    public static LogLevel getLogLevel() {
        return loglevel;
    }

    public static void setLogLevel(LogLevel level) {
        loglevel = level;
    }

    public static void addLogDestination(ILogDestination dest) {
        if (!logDestinations.contains(dest)) {
            logDestinations.add(dest);
        }
    }

    public static void removeLogDestination(ILogDestination dest) {
        if (logDestinations.contains(dest)) {
            logDestinations.remove(dest);
        }
    }

    public static void error(String s) {
        if (loglevel.ordinal() <= LogLevel.ERROR.ordinal()) {
            writeLog("[Error] " + s);
        }
    }

    public static void warn(String s) {
        if (loglevel.ordinal() <= LogLevel.WARN.ordinal()) {
            writeLog("[WARNING] " + s);
        }
    }

    public static void warnNoNewLine(String s) {
        if (loglevel.ordinal() <= LogLevel.WARN.ordinal()) {
            writeLogNoNewLine("[WARNING] " + s);
        }
    }

    public static void info(String s) {
        if (loglevel.ordinal() <= LogLevel.INFO.ordinal()) {
            writeLog(s);
        }
    }

    public static void infoNoNewLine(String s) {
        if (loglevel.ordinal() <= LogLevel.INFO.ordinal()) {
            writeLogNoNewLine(s);
        }
    }

    public static void debug(String s) {
        if (loglevel.ordinal() <= LogLevel.DEBUG.ordinal()) {
            writeLog("\t[DEBUG] " + s);
        }
    }

    public static void debugNoNewline(String s) {
        if (loglevel.ordinal() <= LogLevel.DEBUG.ordinal()) {
            writeLogNoNewLine("\t[DEBUG] " + s);
        }
    }

    public static void trace(String s) {
        if (loglevel.ordinal() <= LogLevel.TRACE.ordinal()) {
            writeLog("\t\t[Trace] " + s);
        }
    }

    public static void traceNoNewline(String s) {
        if (loglevel.ordinal() <= LogLevel.TRACE.ordinal()) {
            writeLogNoNewLine("\t\t[Trace] " + s);
        }
    }

    /** Logs always independent of curent log level */
    public static void log(String s) {
        writeLog(s);
    }

    public static void logNoNewline(String s) {
        writeLogNoNewLine(s);
    }

    private static void writeLog(String log) {
        if (isEnabled && logDestinations.size() > 0) {
            ListIterator<ILogDestination> interator = logDestinations.listIterator();
            while (interator.hasNext()) {
                interator.next().writeLog(log);
            }
        }
    }

    private static void writeLogNoNewLine(String log) {
        if (isEnabled && logDestinations.size() > 0) {
            ListIterator<ILogDestination> interator = logDestinations.listIterator();
            while (interator.hasNext()) {
                interator.next().writeLogNoNewLine(log);
            }
        }
    }
}