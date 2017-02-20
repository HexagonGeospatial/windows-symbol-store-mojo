package org.tapley.windows.symbolstore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TestSymbolStoreActionCLI {
    
    File symStorePath = new File("symstore.exe");
    File repositoryPath = new File("\\\\data1\\symbols");
    String symbolsPath = "*.dll";
    
    @Mock
    ICommandRunnerFactory commandRunnerFactory;
    
    @Mock
    CommandRunner commandRunner;   
    
    @Mock
    IFileUtil fileUtil;   
    
    @InjectMocks
    SymbolStoreActionCLI action;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(action, "commandRunnerFactory", commandRunnerFactory);
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
        Assert.assertEquals("\"\"", entry.getApplicationVersion());
        Assert.assertEquals("\"\"", entry.getComment());
    }
    
    @Test
    public void runAddCommand() throws SymbolStoreException {
        String expectedOutput = "output from command";
        int expectedResponseCode = 0;
        
        Mockito.doNothing().when(commandRunner).run();
        Mockito.when(commandRunner.getOutput()).thenReturn(expectedOutput);
        Mockito.when(commandRunner.getExitValue()).thenReturn(expectedResponseCode);
        Mockito.when(commandRunnerFactory.getCommandRunner(Mockito.any())).thenReturn(commandRunner);
        
        String actualOutput = action.addPath(symStorePath, repositoryPath, symbolsPath, null, null, null, true, true, true);
        Assert.assertEquals(expectedOutput, actualOutput);
    }
    
    @Test
    public void runAddCommandThrows() throws SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        Mockito.when(commandRunnerFactory.getCommandRunner(Mockito.any())).thenThrow(new IllegalArgumentException("error"));
        action.addPath(symStorePath, repositoryPath, symbolsPath, null, null, null, true, true, true);
    }
    
    @Test
    public void runAddCommandBadExitValue() throws SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        int expectedResponseCode = 1;
        
        Mockito.doNothing().when(commandRunner).run();
        Mockito.when(commandRunner.getExitValue()).thenReturn(expectedResponseCode);
        Mockito.when(commandRunnerFactory.getCommandRunner(Mockito.any())).thenReturn(commandRunner);
        
        action.addPath(symStorePath, repositoryPath, symbolsPath, null, null, null, true, true, true);
    }
    
    @Test
    public void deleteCommand() {
        String transactionId = "00000001";
        List<String> expectedDeleteCommand = new ArrayList<>();
        expectedDeleteCommand.add("\"" + symStorePath.getAbsolutePath() + "\"");
        expectedDeleteCommand.add("del");
        expectedDeleteCommand.add("/i");
        expectedDeleteCommand.add(transactionId);
        expectedDeleteCommand.add("/s");
        expectedDeleteCommand.add(repositoryPath.getAbsolutePath());
        expectedDeleteCommand.add("/o");
        
        List<String> actualDeleteCommand = action.getDeleteCommand(symStorePath, repositoryPath, transactionId);
        Assert.assertArrayEquals(expectedDeleteCommand.toArray(), actualDeleteCommand.toArray());
    }
    
    @Test
    public void runDeleteCommand() throws SymbolStoreException {
        String expectedOutput = "output from command";
        int expectedResponseCode = 0;
        
        Mockito.doNothing().when(commandRunner).run();
        Mockito.when(commandRunner.getOutput()).thenReturn(expectedOutput);
        Mockito.when(commandRunner.getExitValue()).thenReturn(expectedResponseCode);
        Mockito.when(commandRunnerFactory.getCommandRunner(Mockito.any())).thenReturn(commandRunner);
        
        String actualOutput = action.deleteTransaction(symStorePath, repositoryPath, "0000001");
        Assert.assertEquals(expectedOutput, actualOutput);
    }
    
    @Test
    public void runDeleteCommandThrows() throws SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        Mockito.when(commandRunnerFactory.getCommandRunner(Mockito.any())).thenThrow(new IllegalArgumentException("error"));
        action.deleteTransaction(symStorePath, repositoryPath, "000001");
    }
    
    @Test
    public void runDeleteCommandBadExitValue() throws SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        int expectedResponseCode = 1;
        
        Mockito.doNothing().when(commandRunner).run();
        Mockito.when(commandRunner.getExitValue()).thenReturn(expectedResponseCode);
        Mockito.when(commandRunnerFactory.getCommandRunner(Mockito.any())).thenReturn(commandRunner);
        
        action.deleteTransaction(symStorePath, repositoryPath, "000001");
    }
    
    @Test
    public void linesThrows() throws IOException, SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        Mockito.when(fileUtil.getLinesFromFile(Mockito.any())).thenThrow(new IllegalArgumentException("arg"));
        action.getActiveTransactionList(symStorePath, repositoryPath);
    }
    
    @Test
    public void noLinesReturnsEmpty() throws IOException, SymbolStoreException {
        Mockito.when(fileUtil.getLinesFromFile(Mockito.any())).thenReturn(Stream.of(""));
        Assert.assertTrue(action.getActiveTransactionList(symStorePath, repositoryPath).isEmpty());
    }
    
    private void compareTransactionEntry(TransactionEntry entry, String id, int dayOfMonth, int month, int year, int hour, int minute, int second, String applicationName, String applicationVersion, String comment) {
        Assert.assertEquals(id, entry.getTransactionId());
        Assert.assertEquals(dayOfMonth, entry.getInsertingTime().getDayOfMonth());
        Assert.assertEquals(month, entry.getInsertingTime().getMonthValue());
        Assert.assertEquals(year, entry.getInsertingTime().getYear());
        Assert.assertEquals(hour, entry.getInsertingTime().getHour());
        Assert.assertEquals(minute, entry.getInsertingTime().getMinute());
        Assert.assertEquals(second, entry.getInsertingTime().getSecond());
        Assert.assertEquals(applicationName, entry.getApplicationName());
        Assert.assertEquals(applicationVersion, entry.getApplicationVersion());
        Assert.assertEquals(comment, entry.getComment());
    }
    
    @Test
    public void returnsMultiTransactions() throws IOException, SymbolStoreException {
        Mockito.when(fileUtil.getLinesFromFile(Mockito.any())).thenReturn(Stream.of("0000000001,add,file,02/07/2017,22:54:12,\"test\",\"\",\"\",",
                                                                                    "0000000002,add,file,02/08/2017,20:33:55,\"test\",\"1\",\"testcli\","));
        List<TransactionEntry> transactions = action.getActiveTransactionList(symStorePath, repositoryPath);
        Assert.assertEquals(2, transactions.size());
        
        compareTransactionEntry(transactions.get(0), "0000000001", 7, 2, 2017, 22, 54, 12, "\"test\"", "\"\"", "\"\"");
        compareTransactionEntry(transactions.get(1), "0000000002", 8, 2, 2017, 20, 33, 55, "\"test\"", "\"1\"", "\"testcli\"");
    }
}
