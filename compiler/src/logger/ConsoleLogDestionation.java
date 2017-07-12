package logger;

import logger.ILogDestination;

public class ConsoleLogDestionation implements ILogDestination{
    public void writeLog(String log){
        System.out.println(log);
    }

    public void writeLogNoNewLine(String log){
        System.out.print(log);
    }
}