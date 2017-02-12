package org.tapley.windows.symbolstore.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tapley.windows.symbolstore.FileSetUtil;
import org.tapley.windows.symbolstore.IFileSetUtil;
import org.tapley.windows.symbolstore.ISymbolStoreAction;
import org.tapley.windows.symbolstore.SymbolStoreActionCLI;
import org.tapley.windows.symbolstore.SymbolStoreException;
import org.tapley.windows.symbolstore.ValidationHelper;
import org.tapley.windows.symbolstore.WildCardPath;

@Mojo(name = "AddSymbols")
public class AddSymbols  extends AbstractMojo {

    @Parameter(required=true)
    FileSet[] fileSets;
    
    @Parameter(required=true)
    File symStorePath;
    
    @Parameter(required=true)
    File repositoryPath;
    
    @Parameter(required=true)
    String applicationName;
    
    @Parameter(required=false, defaultValue="")
    String applicationVersion;
    
    @Parameter(required=false, defaultValue="")
    String comment;
    
    @Parameter(required=false, defaultValue="false")
    boolean compress;
    
    ISymbolStoreAction symbolStoreAction = new SymbolStoreActionCLI();
    ValidationHelper validationHelper = new ValidationHelper();
    IFileSetUtil fileSetUtil = new FileSetUtil();
    
    private void validate() throws MojoExecutionException {
        validationHelper.validateFile("symStorePath", symStorePath);
        validationHelper.validateFile("repositoryPath", repositoryPath);
        
        if(fileSets == null || fileSets.length == 0) {
            throw new MojoExecutionException("At least one fileset must be specified");
        }
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        validate();
        
        try {
            List<FileSet> fileSetsList = Arrays.asList(fileSets);
            for(FileSet fileSet : fileSetsList) {
                
                List<WildCardPath> wildCards;
                
                try {
                    wildCards = fileSetUtil.getSimplifiedWindowsWildcards(fileSet);
                } catch(IllegalStateException ex) {
                    wildCards = null;
                    getLog().debug("Specified fileset is not compatible with windows wildcards, running symstore for each file");
                }
                
                if(wildCards != null) {
                    for(WildCardPath path : wildCards) {
                        try {
                            symbolStoreAction.addPath(symStorePath, repositoryPath, path.getPath(), applicationName, applicationVersion, comment, path.isRecursive(), compress, false);
                        } catch (SymbolStoreException ex) {
                            getLog().warn(ex);
                        }
                    }
                } else {
                    for(File file : fileSetUtil.toFileList(fileSet)) {
                        try {
                            symbolStoreAction.addPath(symStorePath, repositoryPath, file.getAbsolutePath(), applicationName, applicationVersion, comment, false, compress, false);
                        } catch (SymbolStoreException ex) {
                            getLog().warn(ex);
                        }
                    }
                }
            }
        } catch(Exception ex) {
            throw new MojoExecutionException("Unable to add symbols", ex);
        }
    }
    
}
