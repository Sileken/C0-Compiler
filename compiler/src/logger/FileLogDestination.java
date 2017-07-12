package logger;

import logger.ILogDestination;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class FileLogDestination implements ILogDestination {
    private String fileName;
    private boolean isFirstLog = true;

    public FileLogDestination(String fileName) {
        this.fileName = fileName;
    }

    public void writeLog(String log) {
        String out = log;

        if (!isFirstLog) {
            out = "\n" + out;    
        }

        this.writeFile(out);
        this.isFirstLog = false;
    }

    public void writeLogNoNewLine(String log) {
        this.writeFile(log);
    }

    private void writeFile(String log) {
        try (FileWriter fw = new FileWriter(fileName + ".log", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.print(log);
        } catch (IOException ex) {

        }
    }
}