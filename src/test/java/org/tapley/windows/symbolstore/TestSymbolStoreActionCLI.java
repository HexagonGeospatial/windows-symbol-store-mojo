package org.tapley.windows.symbolstore;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestSymbolStoreActionCLI {
    
    File symStorePath = new File("symstore.exe");
    File repositoryPath = new File("\\\\data1\\symbols");
    String symbolsPath = "*.dll";
    
    SymbolStoreActionCLI action;
    
    @Before
    public void setUp() {
        action = new SymbolStoreActionCLI();
    }
    
    @Test
    public void getAddCommandQuotes() {
        List<String> command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, null, true, true, true);
        Assert.assertTrue(command.get(0).startsWith("\""));
        Assert.assertTrue(command.get(0).endsWith("\""));
    }
    
    @Test
    public void getAddCommandRecursive() {
        List<String> command;
        command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, null, false, true, true);
        Assert.assertFalse(command.contains("/r"));
        
        command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, null, true, true, true);
        Assert.assertTrue(command.contains("/r"));
    }
    
    @Test
    public void getAddCommandVerbose() {
        List<String> command;
        command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, null, false, true, false);
        Assert.assertFalse(command.contains("/o"));
        
        command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, null, false, true, true);
        Assert.assertTrue(command.contains("/o"));
    }
    
    @Test
    public void getAddCommandCompress() {
        List<String> command;
        command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, null, false, false, false);
        Assert.assertFalse(command.contains("/compress"));
        
        command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, null, false, true, false);
        Assert.assertTrue(command.contains("/compress"));
    }
    
    @Test
    public void getAddCommandApplicationName() {
        String expectedApplicationName = "MyApp";
        List<String> command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, expectedApplicationName, null, null, false, false, false);
        Assert.assertEquals(expectedApplicationName, command.get(command.indexOf("/t") + 1));
    }
    
    @Test
    public void getAddCommandComment() {
        String expectedComment = "my symbols";
        List<String> command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, null, expectedComment, false, false, false);
        Assert.assertEquals(expectedComment, command.get(command.indexOf("/c") + 1));
    }
    
    @Test
    public void getAddCommandApplicationVersion() {
        String expectedApplicationVersion = "1.0";
        List<String> command = action.getAddCommand(symStorePath, repositoryPath, symbolsPath, null, expectedApplicationVersion, null, false, false, false);
        Assert.assertEquals(expectedApplicationVersion, command.get(command.indexOf("/v") + 1));
    }

    @Test
    public void constructFromStringList() {
        String line = "0000000008,add,file,02/08/2017,20:43:28,\"test\",\"\",\"\",";
        List<String> items = Arrays.asList(line.split("\\s*,\\s*"));
        TransactionEntry entry = action.constructFromStringList(items);
        Assert.assertEquals("0000000008", entry.getTransactionId());
        Assert.assertEquals(8, entry.getInsertingTime().getDayOfMonth());
        Assert.assertEquals(2, entry.getInsertingTime().getMonthValue());
        Assert.assertEquals(2017, entry.getInsertingTime().getYear());
        Assert.assertEquals(20, entry.getInsertingTime().getHour());
        Assert.assertEquals(43, entry.getInsertingTime().getMinute());
        Assert.assertEquals(28, entry.getInsertingTime().getSecond());
        Assert.assertEquals("\"test\"", entry.getApplicationName());
        Assert.assertEquals("\"\"", entry.getComment());
    }
}
