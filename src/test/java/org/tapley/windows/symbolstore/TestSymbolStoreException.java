package org.tapley.windows.symbolstore;

import org.hamcrest.Description;
import org.junit.Test;
import org.junit.Rule;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.ExpectedException;

public class TestSymbolStoreException {
    
    public TestSymbolStoreException() {
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void message() throws SymbolStoreException {
        String expectedMessage = "expectedExceptionMessage";
        
        thrown.expect(SymbolStoreException.class);
        thrown.expectMessage(expectedMessage);
        
        throw new SymbolStoreException(expectedMessage);
    }
    
    @Test
    public void messageAndInner() throws SymbolStoreException {
        String expectedMessage = "expectedExceptionMessage";
        String expectedInnerMessage = "expectedInnerExceptionMessage";
        thrown.expect(SymbolStoreException.class);
        thrown.expectMessage(expectedMessage);
        
        thrown.expectCause(new TypeSafeMatcher<IllegalStateException>() {
            
            @Override
            public boolean matchesSafely(IllegalStateException type) {
                return (type.getMessage() == null ? expectedInnerMessage == null : type.getMessage().equals(expectedInnerMessage));
            }

            @Override
            public void describeTo(Description description) {
            }
        });
        
        throw new SymbolStoreException(expectedMessage, new IllegalStateException(expectedInnerMessage));
    }
    
}
