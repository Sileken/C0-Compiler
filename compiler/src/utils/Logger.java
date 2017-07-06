
package utils;

public class Logger{

    private static boolean DEBUG = false;

    public static void setDebugEnabled(boolean enabled)
    {
        DEBUG = enabled;
    }

    public static void log(String s)
    {
        if(DEBUG)
            System.out.println(s);
    }

    public static void logNoNewline(String s)
    {
        if(DEBUG)
            System.out.print(s);
    }

    public static void debug(String s)
    {
        if(DEBUG)
            System.out.println("  [DEBUG] " + s);
    }

}