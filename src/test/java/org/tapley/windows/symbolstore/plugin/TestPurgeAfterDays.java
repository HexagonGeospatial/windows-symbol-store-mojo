package org.tapley.windows.symbolstore.plugin;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.tapley.windows.symbolstore.ISymbolStoreAction;
import org.tapley.windows.symbolstore.SymbolStoreException;
import org.tapley.windows.symbolstore.TransactionEntry;
import org.tapley.windows.symbolstore.ValidationHelper;

public class TestPurgeAfterDays {
    
    @Mock
    ValidationHelper validationHelper;
    
    @Mock
    ISymbolStoreAction symbolStoreAction;
    
    @Mock
    FileSet fileSet;
    
    @Mock
    TransactionEntry entry;
    
    @InjectMocks
    PurgeAfterDaysMojo plugin = new PurgeAfterDaysMojo();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    int days = 7;
    File symStorePath = new File("c:\\debuggers\\symstore.exe");
    File repositoryPath = new File("c:\\bin\\x64\\release");
    LocalDateTime now = LocalDateTime.now();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(plugin, "symStorePath", symStorePath);
        ReflectionTestUtils.setField(plugin, "repositoryPath", repositoryPath);
        ReflectionTestUtils.setField(plugin, "days", days);
        ReflectionTestUtils.setField(plugin, "now", now);
        
    }
    
    @Test
    public void failValidateDaysLessThanZero() throws MojoExecutionException, MojoFailureException {
        thrown.expect(MojoExecutionException.class);
        thrown.expectMessage("days cannot be less than zero");
        ReflectionTestUtils.setField(plugin, "days", -1);
        plugin.execute();
    }
    
    @Test
    public void failValidateFile() throws MojoExecutionException, MojoFailureException {
        thrown.expect(MojoExecutionException.class);
        Mockito.doThrow(new MojoExecutionException("Bang!")).when(validationHelper).validateFile(Mockito.anyString(), Mockito.any(File.class));
        plugin.execute();
    }
    
    @Test
    public void noTransactions() throws SymbolStoreException, MojoExecutionException, MojoFailureException {
        List<TransactionEntry> transactions = new ArrayList<>();
        Mockito.when(symbolStoreAction.getActiveTransactionList(symStorePath, repositoryPath)).thenReturn(transactions);
        plugin.execute();
        
        Mockito.verify(symbolStoreAction, Mockito.times(1)).getActiveTransactionList(symStorePath, repositoryPath);
        Mockito.verifyNoMoreInteractions(symbolStoreAction);
    }
    
    @Test
    public void transactionTooNew() throws SymbolStoreException, MojoExecutionException, MojoFailureException {
        List<TransactionEntry> transactions = new ArrayList<>();
        Mockito.when(entry.getInsertingTime()).thenReturn(LocalDateTime.now());
        transactions.add(entry);
        Mockito.when(symbolStoreAction.getActiveTransactionList(symStorePath, repositoryPath)).thenReturn(transactions);
        plugin.execute();
        
        Mockito.verify(symbolStoreAction, Mockito.times(1)).getActiveTransactionList(symStorePath, repositoryPath);
        Mockito.verifyNoMoreInteractions(symbolStoreAction);
    }
    
    @Test
    public void transactionNeedsPurge() throws SymbolStoreException, MojoExecutionException, MojoFailureException {
        List<TransactionEntry> transactions = new ArrayList<>();
        Mockito.when(entry.getInsertingTime()).thenReturn(LocalDateTime.now().minusDays(days+2));
        String transactionId = "id";
        Mockito.when(entry.getTransactionId()).thenReturn(transactionId);
        transactions.add(entry);
        Mockito.when(symbolStoreAction.getActiveTransactionList(symStorePath, repositoryPath)).thenReturn(transactions);
        Mockito.when(symbolStoreAction.deleteTransaction(symStorePath, repositoryPath, transactionId)).thenReturn("output");
        
        plugin.execute();
        
        Mockito.verify(symbolStoreAction, Mockito.times(1)).getActiveTransactionList(symStorePath, repositoryPath);
        Mockito.verify(symbolStoreAction, Mockito.times(1)).deleteTransaction(symStorePath, repositoryPath, transactionId);
        Mockito.verifyNoMoreInteractions(symbolStoreAction);
    }
    
    @Test
    public void transactionNeedsPurgeDeleteThrows() throws SymbolStoreException, MojoExecutionException, MojoFailureException {
        List<TransactionEntry> transactions = new ArrayList<>();
        Mockito.when(entry.getInsertingTime()).thenReturn(LocalDateTime.now().minusDays(days+2));
        String transactionId = "id";
        Mockito.when(entry.getTransactionId()).thenReturn(transactionId);
        transactions.add(entry);
        Mockito.when(symbolStoreAction.getActiveTransactionList(symStorePath, repositoryPath)).thenReturn(transactions);
        Mockito.when(symbolStoreAction.deleteTransaction(symStorePath, repositoryPath, transactionId)).thenThrow(new SymbolStoreException("error"));
        
        plugin.execute();
        
        Mockito.verify(symbolStoreAction, Mockito.times(1)).getActiveTransactionList(symStorePath, repositoryPath);
        Mockito.verify(symbolStoreAction, Mockito.times(1)).deleteTransaction(symStorePath, repositoryPath, transactionId);
        Mockito.verifyNoMoreInteractions(symbolStoreAction);
    }
    
    @Test
    public void transactionListThrows() throws SymbolStoreException, MojoExecutionException, MojoFailureException {
        thrown.expect(MojoExecutionException.class);        
        Mockito.when(symbolStoreAction.getActiveTransactionList(symStorePath, repositoryPath)).thenThrow(new SymbolStoreException("error"));
        plugin.execute();
    }
    
    @Test
    public void validateDateForTransactionIsBefore() {
        Mockito.when(entry.getInsertingTime()).thenReturn(LocalDateTime.now().minusDays(days+2));
        Assert.assertTrue(plugin.validateDateForTransaction(entry));
    }
    
    @Test
    public void validateDateForTransactionIsNotBefore() {
        Mockito.when(entry.getInsertingTime()).thenReturn(LocalDateTime.now().minusDays(days/2));
        Assert.assertFalse(plugin.validateDateForTransaction(entry));
    }
    
    @Test
    public void getDaysSinceTransactionAdded() {
        for(int i = 0; i < 14; i++) {
            Mockito.when(entry.getInsertingTime()).thenReturn(now.minusDays(i));
            long days = plugin.getDaysSinceTransactionAdded(entry);
            Assert.assertEquals(i, (int)days);
        }
    }
}
