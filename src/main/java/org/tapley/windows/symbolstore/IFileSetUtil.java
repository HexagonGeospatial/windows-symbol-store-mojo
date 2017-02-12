package org.tapley.windows.symbolstore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.maven.model.FileSet;

public interface IFileSetUtil {
    List<WildCardPath> getSimplifiedWindowsWildcards(FileSet fileSet);
    List<File> toFileList(FileSet fileSet) throws IOException;
}
