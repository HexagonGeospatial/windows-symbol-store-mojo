package org.tapley.windows.symbolstore.plugin;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tapley.windows.symbolstore.ISymbolStoreAction;
import org.tapley.windows.symbolstore.SymbolStoreActionCLI;
import org.tapley.windows.symbolstore.SymbolStoreException;
import org.tapley.windows.symbolstore.TransactionEntry;
import org.tapley.windows.symbolstore.ValidationHelper;

@Mojo(name = "PurgeAfterDays")
public class PurgeAfterDaysMojo extends AbstractMojo {

    @Parameter(defaultValue="60", required=true)
    int days;
    
    @Parameter(required=true)
    File symStorePath;
    
    @Parameter(required=true)
    File repositoryPath;
    
    LocalDateTime now = LocalDateTime.now();
    ISymbolStoreAction symbolStoreAction = new SymbolStoreActionCLI();
    ValidationHelper validationHelper = new ValidationHelper();
    
    private void validate() throws MojoExecutionException {
        if(days < 0) {
            throw new MojoExecutionException("days cannot be less than zero");
        }
        
        validationHelper.validateFile("symStorePath", symStorePath);
        validationHelper.validateFile("repositoryPath", repositoryPath);
        
        getLog().info("Days to keep items in repository: " + days);
        getLog().info("Path to symstore.exe: " + symStorePath);
        getLog().info("Path to repository: " + repositoryPath);
    }
    
    public boolean validateDateForTransaction(TransactionEntry transactionEntry) {
        return transactionEntry.getInsertingTime().plusDays(days).isBefore(now);
    }
    
    public long getDaysSinceTransactionAdded(TransactionEntry transactionEntry) {
        return transactionEntry.getInsertingTime().until(now, ChronoUnit.DAYS);
    }
    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        validate();
        
        try {
            List<TransactionEntry> transactions = symbolStoreAction.getActiveTransactionList(symStorePath, repositoryPath);
            for(TransactionEntry transactionEntry : transactions) {
            //transactions.stream().forEach((transactionEntry) -> {
                if(validateDateForTransaction(transactionEntry)) {
                    getLog().info(String.format("Transaction %s (Application %s) added %d days ago: PURGING",
                            transactionEntry.getTransactionId(),
                            transactionEntry.getApplicationName(), 
                            getDaysSinceTransactionAdded(transactionEntry)));
                    
                    try {
                        String output = symbolStoreAction.deleteTransaction(symStorePath, repositoryPath, transactionEntry.getTransactionId());
                        getLog().debug(output);
                    } catch (SymbolStoreException ex) {
                        getLog().warn(ex);
                    }
                    
                } else {
                    getLog().info(String.format("Transaction %s (Application %s) added %d days ago: KEEPING",
                            transactionEntry.getTransactionId(),
                            transactionEntry.getApplicationName(), 
                            getDaysSinceTransactionAdded(transactionEntry)));
                }
            }
            //});
        } catch(Exception ex) {
            throw new MojoExecutionException("Unable to process purge list", ex);
        }
    }
    
}
