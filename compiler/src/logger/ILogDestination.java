package logger;

public interface ILogDestination{
    void writeLog(String log);

    void writeLogNoNewLine(String log);
}