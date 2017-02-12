package org.tapley.windows.symbolstore;

import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;

public class ValidationHelper {
    public void validateString(String stringName, String value) throws MojoExecutionException {
        if(value == null || value.isEmpty()) {
            throw new MojoExecutionException(stringName + " cannot be null or empty");
        }
        if(!new File(value).exists()) {
            throw new MojoExecutionException(stringName + " must reference a valid path");
        }
    }
    public void validateFile(String stringName, File value) throws MojoExecutionException {
        if(value == null || value.getAbsolutePath().isEmpty()) {
            throw new MojoExecutionException(stringName + " cannot be null or empty");
        }
        if(!value.exists()) {
            throw new MojoExecutionException(stringName + " must reference a valid path");
        }
    }
    
}
