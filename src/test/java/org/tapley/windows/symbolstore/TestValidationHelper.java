package org.tapley.windows.symbolstore;

import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class TestValidationHelper {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    ValidationHelper helper = new ValidationHelper();
    
    @Test
    public void pathNull() throws MojoExecutionException {
        thrown.expect(MojoExecutionException.class);
        helper.validateFile("", null);
    }
    
    @Test
    public void pathEmpty() throws MojoExecutionException {
        thrown.expect(MojoExecutionException.class);
        helper.validateFile("", new File(""));
    }
    
    @Test
    public void pathDne() throws MojoExecutionException {
        thrown.expect(MojoExecutionException.class);
        helper.validateFile("", new File("/////does/not/exist"));
    }
    
    @Test
    public void pathOk() throws MojoExecutionException {
        helper.validateFile("", new File(System.getProperty("java.io.tmpdir")));
    }
}
