package org.tapley.windows.symbolstore;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestSymbolStoreActionCLI {
    
    SymbolStoreActionCLI action;
    
    @Before
    public void setUp() {
        action = new SymbolStoreActionCLI();
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
