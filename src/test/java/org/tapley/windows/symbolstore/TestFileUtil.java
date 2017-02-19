package org.tapley.windows.symbolstore;

import java.io.IOException;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class TestFileUtil {
    
    FileUtil fileUtil = new FileUtil();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testGetLinesFromFileDNE() throws IOException {
        thrown.expect(IOException.class);
        fileUtil.getLinesFromFile("path/does\\not///////exist");
    }
    
}
