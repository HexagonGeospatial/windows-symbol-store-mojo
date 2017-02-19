package org.tapley.windows.symbolstore;

import java.util.List;
import org.junit.Assert;
import org.apache.maven.model.FileSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestFileSetUtilSimplifiedWindowsWildcards {
    FileSetUtil fileSetUtil = new FileSetUtil();
    
    FileSet fileSet;
    String baseFolder;
    String baseFolderExpected;
    private static final String EXPECTED_FILESET_NOT_SUPPORTED = "fileset does not support simple wildcards";
    
    @Before
    public void init() {
        baseFolder = "c:/temp";
        baseFolderExpected = baseFolder.replace('/', '\\');
        fileSet = new FileSet();
        fileSet.setDirectory(baseFolder);
    }
    
    @Rule
    public ExpectedException thrown= ExpectedException.none();
    
    @Test
    public void excludesExist() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXPECTED_FILESET_NOT_SUPPORTED);
        fileSet.addExclude("**/**");
        fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
    }
    
    @Test
    public void noIncludes() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXPECTED_FILESET_NOT_SUPPORTED);
        fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
    }
    
    @Test
    public void windowsSlashes() {
        fileSet.addInclude("dirs/to/the/file.dll");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertFalse(path.get(0).getPath().contains("/"));
    }
    
    @Test
    public void starDotStarRecursive() {
        fileSet.addInclude("**/**");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertEquals(baseFolderExpected + "\\*.*", path.get(0).getPath());
        Assert.assertTrue(path.get(0).isRecursive());
    }
    
    @Test
    public void starDotStarRecursiveButNotReallyAFile() {
        thrown.expect(IllegalStateException.class);
        fileSet.addInclude("**/*.dll/trickedyou");
        fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
    }
    
    @Test
    public void starDotStarRecursiveButNotReallyAFile2() {
        thrown.expect(IllegalStateException.class);
        fileSet.addInclude("**/*./");
        fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
    }
    
    @Test
    public void starDotStarNotRecursive() {
        fileSet.addInclude("*.*");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertEquals(baseFolderExpected + "\\*.*", path.get(0).getPath());
        Assert.assertFalse(path.get(0).isRecursive());
    }
    
    @Test
    public void allExtTypeRecursive() {
        fileSet.addInclude("**/*.pdb");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertEquals(baseFolderExpected + "\\*.pdb", path.get(0).getPath());
        Assert.assertTrue(path.get(0).isRecursive());
    }
    
    @Test
    public void allExtTypeNotRecursive() {
        fileSet.addInclude("*.pdb");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertEquals(baseFolderExpected + "\\*.pdb", path.get(0).getPath());
        Assert.assertFalse(path.get(0).isRecursive());
    }
    
    @Test
    public void allExtTypeNotRecursiveInvalidWildcard() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXPECTED_FILESET_NOT_SUPPORTED);
        fileSet.addInclude("*.pdb/*.pdb");
        fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
    }
    
    @Test
    public void allExtTypeWithFolder() {
        fileSet.addInclude("path/to/*.pdb");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertEquals(baseFolderExpected + "\\path\\to\\*.pdb", path.get(0).getPath());
        Assert.assertFalse(path.get(0).isRecursive());
    }
    
    @Test
    public void allExtTypeWithFolderInvalidWildcard() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXPECTED_FILESET_NOT_SUPPORTED);
        fileSet.addInclude("path/to/*.pdb/*.pdb");
        fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
    }
    
    @Test
    public void specificFile() {
        fileSet.addInclude("file.dll");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertEquals(baseFolderExpected + "\\file.dll", path.get(0).getPath());
        Assert.assertFalse(path.get(0).isRecursive());
    }
    
    @Test
    public void specificSubFolderAndFile() {
        fileSet.addInclude("path/to/this/file.dll");
        List<WildCardPath> path = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
        Assert.assertEquals(1, path.size());
        Assert.assertEquals(baseFolderExpected + "\\path\\to\\this\\file.dll", path.get(0).getPath());
        Assert.assertFalse(path.get(0).isRecursive());
    }
    
    @Test
    public void specificSubFolderAndFileInvalidWildcard() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(EXPECTED_FILESET_NOT_SUPPORTED);
        fileSet.addInclude("path/*/this/file.dll");
        fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
    }
}
