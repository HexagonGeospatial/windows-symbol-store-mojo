package org.tapley.windows.symbolstore.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.tapley.windows.symbolstore.FileSetUtil;
import org.tapley.windows.symbolstore.ISymbolStoreAction;
import org.tapley.windows.symbolstore.SymbolStoreException;
import org.tapley.windows.symbolstore.ValidationHelper;
import org.tapley.windows.symbolstore.WildCardPath;

public class TestAddSymbols {

    @Mock
    FileSetUtil fileSetUtil;
    
    @Mock
    ValidationHelper validationHelper;
    
    @Mock
    ISymbolStoreAction symbolStoreAction;
    
    @Mock
    FileSet fileSet;
    
    @InjectMocks
    AddSymbols plugin = new AddSymbols();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    
    File symStorePath = new File("c:\\debuggers\\symstore.exe");
    File repositoryPath = new File("c:\\bin\\x64\\release");
    String applicationName = "AppName";
    String applicationVersion = "1.0.1";
    String comment = "comment asd asd asd";
    
    FileSet[] fileSetsArray = new FileSet[1];
       
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        fileSetsArray[0] = fileSet;
        
        ReflectionTestUtils.setField(plugin, "fileSets", fileSetsArray);
        ReflectionTestUtils.setField(plugin, "symStorePath", symStorePath);
        ReflectionTestUtils.setField(plugin, "repositoryPath", repositoryPath);
        ReflectionTestUtils.setField(plugin, "applicationName", applicationName);
        ReflectionTestUtils.setField(plugin, "applicationVersion", applicationVersion);
        ReflectionTestUtils.setField(plugin, "comment", comment);
    }
   
    @Test
    public void failValidateFile() throws MojoExecutionException, MojoFailureException {
        thrown.expect(MojoExecutionException.class);
        Mockito.doThrow(new MojoExecutionException("Bang!")).when(validationHelper).validateFile(Mockito.anyString(), Mockito.any(File.class));
        plugin.execute();
    }
    
    @Test
    public void failValidateFileSet() throws MojoExecutionException, MojoFailureException {
        thrown.expect(MojoExecutionException.class);
        ReflectionTestUtils.setField(plugin, "fileSets", null);
        plugin.execute();
    }
    
    @Test
    public void getSimplifiedWindowsWildcardsFails() throws IOException, MojoExecutionException, MojoFailureException, SymbolStoreException {
        
        List<File> files = new ArrayList<>();
        files.add(new File("C:\\temp\\file.dll"));
        
        Mockito.when(fileSetUtil.getSimplifiedWindowsWildcards(fileSet)).thenThrow(new IllegalStateException("unsupported"));
        Mockito.when(fileSetUtil.toFileList(fileSet)).thenReturn(files);
        
        plugin.execute();
        
        Mockito.verify(fileSetUtil).getSimplifiedWindowsWildcards(fileSet);
        Mockito.verify(fileSetUtil).toFileList(fileSet);
        Mockito.verify(symbolStoreAction).addPath(symStorePath, repositoryPath, files.get(0).getAbsolutePath(), applicationName, applicationVersion, comment, false, false, false);
    }
    
    @Test
    public void getSimplifiedWindowsWildcards() throws IOException, MojoExecutionException, MojoFailureException, SymbolStoreException {
        
        List<WildCardPath> files = new ArrayList<>();
        files.add(new WildCardPath("C:\\temp\\*.dll", true));
        
        Mockito.when(fileSetUtil.getSimplifiedWindowsWildcards(fileSet)).thenReturn(files);
        
        plugin.execute();
        
        Mockito.verify(fileSetUtil).getSimplifiedWindowsWildcards(fileSet);
        Mockito.verify(symbolStoreAction).addPath(symStorePath, repositoryPath, files.get(0).getPath(), applicationName, applicationVersion, comment, files.get(0).isRecursive(), false, false);
    }
    
    @Test
    public void getSimplifiedWindowsWildcardsThrows() throws IOException, MojoExecutionException, MojoFailureException, SymbolStoreException {
        
        thrown.expect(MojoExecutionException.class);
        
        List<WildCardPath> files = new ArrayList<>();
        files.add(new WildCardPath("C:\\temp\\*.dll", true));
        
        Mockito.when(fileSetUtil.getSimplifiedWindowsWildcards(fileSet)).thenReturn(files);
        Mockito.doThrow(new NullPointerException("bang")).when(symbolStoreAction).addPath(symStorePath, repositoryPath, files.get(0).getPath(), applicationName, applicationVersion, comment, files.get(0).isRecursive(), false, false);
        
        plugin.execute();
    }
}
