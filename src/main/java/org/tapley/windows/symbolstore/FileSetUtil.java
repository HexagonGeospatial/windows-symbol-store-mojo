package org.tapley.windows.symbolstore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.FileSet;
import org.codehaus.plexus.util.FileUtils;

public class FileSetUtil implements IFileSetUtil {

    @Override
    public List<File> toFileList(FileSet fileSet) throws IOException {
        File directory = new File(fileSet.getDirectory());
        String includes = String.join(", ", fileSet.getIncludes());
        String excludes = String.join(", ", fileSet.getExcludes());
        return FileUtils.getFiles(directory, includes, excludes);
    }

    private static final String FILESET_NOT_SUPPORTED = "fileset does not support simple wildcards";
    
    @Override
    public List<WildCardPath> getSimplifiedWindowsWildcards(FileSet fileSet) {
        
        if(fileSet == null) {
            throw new IllegalArgumentException("fileSet cannot be null");
        }
        
        List<WildCardPath> windowsWildCards = new ArrayList<>();

        String directory = fileSet.getDirectory();
        if(directory == null) {
            directory = ".";
        } else {
            directory = directory.replace('/', '\\');
        }

        if (fileSet.getExcludes() != null && !fileSet.getExcludes().isEmpty()) {
            throw new IllegalStateException(FILESET_NOT_SUPPORTED);
        }

        if (fileSet.getIncludes() == null || fileSet.getIncludes().isEmpty()) {
            throw new IllegalStateException(FILESET_NOT_SUPPORTED);
        }
        
        for (String include : fileSet.getIncludes()) {

            if ("**/**".equals(include)) {
                windowsWildCards.add(new WildCardPath(directory + "\\*.*", true));
            } else if (include.startsWith("**/*.")) {
                if (include.indexOf('/', 4) < 0) {
                    windowsWildCards.add(new WildCardPath(directory + '\\' + include.substring(3), true));
                } else {
                    throw new IllegalStateException(FILESET_NOT_SUPPORTED);
                }
            } else if (include.startsWith("*.")) {
                if (include.indexOf('/', 1) < 0) {
                    windowsWildCards.add(new WildCardPath(directory + '\\' + include, false));
                } else {
                    throw new IllegalStateException(FILESET_NOT_SUPPORTED);
                }
            } else {
                // Path can't have *'s in folder name
                int lastSlash = include.lastIndexOf("/");
                if(lastSlash >= 0 && include.substring(0, lastSlash).contains("*")) {
                    throw new IllegalStateException(FILESET_NOT_SUPPORTED);
                }
                windowsWildCards.add(new WildCardPath(directory + '\\' + include.replace('/', '\\'), false));
            }

        }

        return windowsWildCards;
    }
}
