
package utils;

public class Logger {

    public static enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static LogLevel loglevel = LogLevel.INFO;
    private static boolean isEnabled = false;

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

    public static void setLogLevel(LogLevel loglevel) {
        loglevel = loglevel;
    }

    public static void error(String s) {
        if (loglevel.ordinal() >= LogLevel.ERROR.ordinal()  && isEnabled)
            System.out.println(s);
    }

       public static void warn(String s) {
        if (loglevel.ordinal() >= LogLevel.WARN.ordinal()  && isEnabled)
            System.out.println(s);
    }

    public static void info(String s) {
        if (loglevel.ordinal() >= LogLevel.INFO.ordinal()  && isEnabled)
            System.out.println(s);
    }

    public static void debug(String s) {
        if (loglevel.ordinal() >= LogLevel.DEBUG.ordinal()  && isEnabled)
            System.out.println("\t[DEBUG] " + s);
    }

    /** Logs always independent of curent log level */
    public static void log(String s)
    {
        System.out.println(s);
    }

    public static void logNoNewline(String s, LogLevel level)
    {
        if(loglevel.ordinal() >= level.ordinal()  && isEnabled){
            switch(loglevel){
                case TRACE:
                case DEBUG:
                case WARN:
                case ERROR: 
                    System.out.print("\t[" + loglevel.name() + "] " + s);
                break;
                default: System.out.println(s);
                    break;
            }
        }     
    }
}