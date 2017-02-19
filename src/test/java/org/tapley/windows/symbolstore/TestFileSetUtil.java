package org.tapley.windows.symbolstore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.model.FileSet;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class TestFileSetUtil extends TestCase {
    
    FileSetUtil fileSetUtil = new FileSetUtil();
    
    @Test
    public void testToFileList() throws IOException {
        String includes = "*.*";
        String excludes = "";
        File directory = new File(".");
        List<File> expectedPaths = new ArrayList<>();
        expectedPaths.add(new File("./file.dll"));
        FileSet fileSet = new FileSet();
        fileSet.setDirectory(directory.getAbsolutePath());
        fileSet.addInclude(includes);
        fileSet.addExclude(excludes);
        
        
        PowerMockito.mockStatic(FileUtils.class);
        PowerMockito.when(FileUtils.getFiles(new File(fileSet.getDirectory()), includes, excludes)).thenReturn(expectedPaths);
        
        List<File> actualFilePaths = fileSetUtil.toFileList(fileSet);
        
        Assert.assertArrayEquals(actualFilePaths.toArray(), expectedPaths.toArray());
    }
}
