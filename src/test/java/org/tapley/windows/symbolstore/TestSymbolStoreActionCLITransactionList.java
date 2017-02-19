package org.tapley.windows.symbolstore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Files.class, Paths.class})
public class TestSymbolStoreActionCLITransactionList {
    
    File symStorePath = new File("symstore.exe");
    File repositoryPath = new File("\\\\data1\\symbols");
    String symbolsPath = "*.dll";
    
    @InjectMocks
    SymbolStoreActionCLI action;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Files.class);
        PowerMockito.mockStatic(Paths.class);
    }
    
    @Test
    public void linesThrows() throws IOException, SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        PowerMockito.when(Paths.get(Mockito.any())).thenThrow(new IllegalArgumentException("arg"));
        action.getActiveTransactionList(symStorePath, repositoryPath);
    }
    
    @Test
    public void noLinesReturnsEmpty() throws IOException, SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        Path path = Paths.get(System.getProperty("java.tmp.dir"));
        PowerMockito.when(Paths.get(Mockito.any())).thenReturn(path);
        PowerMockito.when(Files.lines(path)).thenReturn(Stream.of(""));
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
    public void linesReturnsEmpty() throws IOException, SymbolStoreException {
        thrown.expect(SymbolStoreException.class);
        Path path = Paths.get(System.getProperty("java.tmp.dir"));
        PowerMockito.when(Paths.get(Mockito.any())).thenReturn(path);
        PowerMockito.when(Files.lines(path)).thenReturn(Stream.of("0000000001,add,file,02/07/2017,22:54:12,\"test\",\"\",\"\",",
                                                                  "0000000002,add,file,02/08/2017,20:33:55,\"test\",\"1\",\"testcli\","));
        List<TransactionEntry> transactions = action.getActiveTransactionList(symStorePath, repositoryPath);
        Assert.assertEquals(2, transactions.size());
        
        compareTransactionEntry(transactions.get(0), "0000000001", 7, 2, 2017, 22, 54, 12, "\"test\"", "\"\"", "\"\"");
        compareTransactionEntry(transactions.get(0), "0000000002", 8, 2, 2017, 20, 33, 55, "\"test\"", "\"1\"", "\"testcli\"");
    }
}
