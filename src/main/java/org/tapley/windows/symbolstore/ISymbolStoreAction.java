package org.tapley.windows.symbolstore;

import java.io.File;
import java.util.List;

public interface ISymbolStoreAction {
    String addPath(File symStorePath, File repositoryPath, String symbolsPath, String applicationName, String applicationVersion, String comment, boolean recursive, boolean compress, boolean verboseOutput) throws SymbolStoreException;
    String deleteTransaction(File symStorePath, File repositoryPath, String transactionId) throws SymbolStoreException;
    List<TransactionEntry> getActiveTransactionList(File symStorePath, File repositoryPath) throws SymbolStoreException;
}
