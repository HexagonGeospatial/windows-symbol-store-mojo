package org.tapley.windows.symbolstore;

import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class CommandRunner {

    final List<String> commandList;
    int exitValue;
    Process process;
    String output;
    Runtime runtime;
    
    CommandRunner(List<String> commandList) {
        if(commandList == null || commandList.isEmpty()) {
            throw new IllegalArgumentException("commandList cannot be null or empty");
        }
        this.runtime = Runtime.getRuntime();
        this.commandList = commandList;
        this.exitValue = 0xf0000000;
    }
    
    public void run() {
        try {
            String[] commandArray = new String[commandList.size()];
            commandList.toArray(commandArray);
            process = runtime.exec(commandArray);
            
            while(process.isAlive()) {
                try {
                    process.waitFor();
                } catch (InterruptedException ex) {
                }
            }
            
            output = IOUtils.toString(process.getInputStream());
            exitValue = process.exitValue();
        } catch (IOException ex) {
            exitValue = 0xffffffff;
            throw new IllegalStateException(String.format("Unable to run command '%s'", commandList.get(0)), ex);
        }
    }
    
    public void terminate() {
        if(process != null) {
            process.destroy();
        }
    }
    
    public int getExitValue() {
        return exitValue;
    }
    
    public String getOutput() {
        return output;
    }
}
