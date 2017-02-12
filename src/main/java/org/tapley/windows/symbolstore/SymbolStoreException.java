package org.tapley.windows.symbolstore;

public class SymbolStoreException extends Exception {
    public SymbolStoreException(String message) { 
        super(message);
    }
    public SymbolStoreException(String message, Throwable th) { 
        super(message, th);
    }
}
