package org.tapley.windows.symbolstore;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.codehaus.plexus.util.StringUtils;

/**
 * The symstore.exe wrapper class
 * 
 * @author Chris
 */
public class SymbolStoreActionCLI implements ISymbolStoreAction {

    ICommandRunnerFactory commandRunnerFactory = new CommandRunnerFactory();
    IFileUtil fileUtil = new FileUtil();
    
    private String wrapStringInQuotes(String path) {
        return "\"" + path + "\"";
    }
    
    /**
     * Gets an add command from input arguments
     * 
     * @param symStorePath The path to symstore.exe
     * @param repositoryPath The path to the target repository
     * @param symbolsPath The path to the symbols to be added to the repository
     * @param applicationName The application name to store for this transaction
     * @param applicationVersion The application version to store for this transaction
     * @param comment The comment to store for this version (optional)
     * @param recursive Should the search for symbols at symbolsPath be recursive
     * @param compress Should the stored symbols be compressed
     * @param verboseOutput Do you want verbose output from the symstore.exe invokation
     * 
     * @return symstore add command as a list of strings
     */
    public List<String> getAddCommand(File symStorePath, File repositoryPath, String symbolsPath, String applicationName, String applicationVersion, String comment, boolean recursive, boolean compress, boolean verboseOutput) {
        List<String> commandList = new ArrayList<>();
        
        commandList.add(wrapStringInQuotes(symStorePath.getAbsolutePath()));
        commandList.add("add");
        
        if(recursive) {
            commandList.add("/r");
        }
        
        if(verboseOutput) { 
            commandList.add("/o");
        }
        
        commandList.add("/s");
        commandList.add(wrapStringInQuotes(repositoryPath.getAbsolutePath()));
        
        commandList.add("/f");
        commandList.add(wrapStringInQuotes(symbolsPath));
        
        if(StringUtils.isNotEmpty(applicationName)) {
            commandList.add("/t");
            commandList.add(wrapStringInQuotes(applicationName));
        }
        
        if(StringUtils.isNotEmpty(applicationVersion)) {
            commandList.add("/v");
            commandList.add(wrapStringInQuotes(applicationVersion));
        }
        
        if(StringUtils.isNotEmpty(comment)) {
            commandList.add("/c");
            commandList.add(wrapStringInQuotes(comment));
        }
        
        if(compress) {
            commandList.add("/compress");
        }
        
        return commandList;
    }
    
    /**
     * Add symbols from symbolsPath to the symstore repository
     * 
     * @param symStorePath The path to symstore.exe
     * @param repositoryPath The path to the target repository
     * @param symbolsPath The path to the symbols to be added to the repository
     * @param applicationName The application name to store for this transaction
     * @param applicationVersion The application version to store for this transaction
     * @param comment The comment to store for this version (optional)
     * @param recursive Should the search for symbols at symbolsPath be recursive
     * @param compress Should the stored symbols be compressed
     * @param verboseOutput Do you want verbose output from the symstore.exe invokation
     * @return The output from the symstore.exe invokation
     * @throws SymbolStoreException When something went wrong
     */
    @Override
    public String addPath(File symStorePath, File repositoryPath, String symbolsPath, String applicationName, String applicationVersion, String comment, boolean recursive, boolean compress, boolean verboseOutput) throws SymbolStoreException {
        try {
            return runCommand(getAddCommand(symStorePath, repositoryPath, symbolsPath, applicationName, applicationVersion, comment, recursive, compress, verboseOutput));
        } catch(Exception ex) {
            throw new SymbolStoreException("Unable to add symbols from " + symbolsPath, ex);
        }
    }

    /**
     * Get the delete command to delete a transaction from the symbol server
     * 
     * @param symStorePath The path to symstore.exe
     * @param repositoryPath The path to the target repository
     * @param transactionId The id of the transaction to delete
     * @return the symstore.exe command to delete the transaction
     */
    public List<String> getDeleteCommand(File symStorePath, File repositoryPath, String transactionId) {
        List<String> commandList = new ArrayList<>();
        
        commandList.add(wrapStringInQuotes(symStorePath.getAbsolutePath()));
        commandList.add("del");
        
        commandList.add("/i");
        commandList.add(transactionId);
        
        commandList.add("/s");
        commandList.add(wrapStringInQuotes(repositoryPath.getAbsolutePath()));
        
        commandList.add("/o");
        
        return commandList;
    }
    
    private String runCommand(List<String> commandList) {
        CommandRunner runner = commandRunnerFactory.getCommandRunner(commandList);
        
        runner.run();
        
        if(runner.getExitValue() != 0) {
            throw new IllegalStateException(String.format("Command '%s' exited with code %d.  Output was '%s'", commandList, runner.getExitValue(), runner.getOutput()));
        }
        
        return runner.getOutput();
    }
    
    /**
     * Delete a transaction from the symbol server
     * 
     * @param symStorePath The path to symstore.exe
     * @param repositoryPath The path to the target repository
     * @param transactionId The id of the transaction to delete
     * @return The output from the symstore.exe command
     * @throws SymbolStoreException When something went wrong
     */
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
    
    /**
     * Parses a TransactionEntry from a symstore transaction
     * 
     * @param items Transaction parts
     * @return parsed TransactionEntry
     */
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
    
    /**
     * Gets the list of active transactions on the server
     * 
     * @param symStorePath The path to symstore.exe
     * @param repositoryPath The path to the target repository
     * @return The list of parsed transactions
     * @throws SymbolStoreException When something went wrong
     */
    @Override
    public List<TransactionEntry> getActiveTransactionList(File symStorePath, File repositoryPath) throws SymbolStoreException {
        try {
            List<TransactionEntry> transactionEntries = new ArrayList<>();
            File serverTxt = getServerListPath(repositoryPath);
           
            try (Stream<String> lines = fileUtil.getLinesFromFile(serverTxt.getAbsolutePath())) {
                lines.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String line) {
                        if(!line.trim().isEmpty()) {
                            List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
                            if(items != null && items.size() == 8) {
                                transactionEntries.add(constructFromStringList(items));
                            }
                        }
                    }
                });
            }
            return transactionEntries;
        } catch(Exception ex) {
            throw new SymbolStoreException(String.format("Unable to parse transaction list from symbol path %s", symStorePath), ex);
        }
    }
    
}
