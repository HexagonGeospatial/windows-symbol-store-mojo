package org.tapley.windows.symbolstore;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SymbolStoreActionCLI implements ISymbolStoreAction {

    public List<String> getAddCommand(File symStorePath, File repositoryPath, String symbolsPath, String applicationName, String applicationVersion, String comment, boolean recursive, boolean compress, boolean verboseOutput) {
        List<String> commandList = new ArrayList<>();
        
        commandList.add("\"" + symStorePath.getAbsolutePath() + "\"");
        commandList.add("add");
        
        if(recursive) {
            commandList.add("/r");
        }
        
        if(verboseOutput) { 
            commandList.add("/o");
        }
        
        commandList.add("/s");
        commandList.add(repositoryPath.getAbsolutePath());
        
        commandList.add("/f");
        commandList.add(symbolsPath);
        
        if(applicationName != null && !applicationName.isEmpty()) {
            commandList.add("/t");
            commandList.add(applicationName);
        }
        
        if(applicationVersion != null && !applicationVersion.isEmpty()) {
            commandList.add("/v");
            commandList.add(applicationVersion);
        }
        
        if(comment != null && !comment.isEmpty()) {
            commandList.add("/c");
            commandList.add(comment);
        }
        
        if(compress) {
            commandList.add("/compress");
        }
        
        return commandList;
    }
    
    @Override
    public String addPath(File symStorePath, File repositoryPath, String symbolsPath, String applicationName, String applicationVersion, String comment, boolean recursive, boolean compress, boolean verboseOutput) throws SymbolStoreException {
        try {
            return runCommand(getAddCommand(symStorePath, repositoryPath, symbolsPath, applicationName, applicationVersion, comment, recursive, compress, verboseOutput));
        } catch(Exception ex) {
            throw new SymbolStoreException("Unable to add symbols from " + symbolsPath, ex);
        }
    }

    private List<String> getDeleteCommand(File symStorePath, File repositoryPath, String transactionId) {
        List<String> commandList = new ArrayList<>();
        
        commandList.add(symStorePath.getAbsolutePath());
        commandList.add("del");
        
        commandList.add("/i");
        commandList.add(transactionId);
        
        commandList.add("/s");
        commandList.add(repositoryPath.getAbsolutePath());
        
        return commandList;
    }
    
    private String runCommand(List<String> commandList) {
        CommandRunner runner = new CommandRunner(commandList);
        
        runner.run();
        
        if(runner.getExitValue() != 0) {
            throw new IllegalStateException(String.format("Command '%s' exited with code %d.  Output was '%s'", commandList, runner.getExitValue(), runner.getOutput()));
        }
        
        return runner.getOutput();
    }
    
    @Override
    public String deleteTransaction(File symStorePath, File repositoryPath, String transactionId) throws SymbolStoreException {
        try {
            return runCommand(getDeleteCommand(symStorePath, repositoryPath, transactionId));
        } catch(Exception ex) {
            throw new SymbolStoreException("Unable to delete transaction " + transactionId, ex);
        }
    }

    private File getServerListPath(File repositoryPath) {
        return new File(repositoryPath + File.separator + "000Admin", "server.txt");
    }
    
    public TransactionEntry constructFromStringList(List<String> items) {
        TransactionEntry entry = new TransactionEntry();
        entry.setTransactionId(items.get(0));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        entry.setInsertingTime(LocalDateTime.parse(String.format("%s %s", items.get(3), items.get(4)), formatter));
        
        entry.setApplicationName(items.get(5));
        entry.setApplicationVersion(items.get(6));
        entry.setComment(items.get(7));
        
        return entry;
    }
    
    @Override
    public List<TransactionEntry> getActiveTransactionList(File symStorePath, File repositoryPath) throws SymbolStoreException {
        try {
            List<TransactionEntry> transactionEntries = new ArrayList<>();
            File serverTxt = getServerListPath(repositoryPath);
            
            if(serverTxt.exists()) {
            
                try (Stream<String> stream = Files.lines(Paths.get(serverTxt.getAbsolutePath()))) {
                   stream.forEach((line) -> {
                       List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
                       if(items != null && items.size() == 8) {
                           transactionEntries.add(constructFromStringList(items));
                       }
                   });
                }
            }
            return transactionEntries;
        } catch(Exception ex) {
            throw new SymbolStoreException(String.format("Unable to parse transaction list from symbol path %s", symStorePath), ex);
        }
    }
    
}
