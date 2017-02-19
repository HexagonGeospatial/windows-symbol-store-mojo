package org.tapley.windows.symbolstore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileUtil implements IFileUtil {

    @Override
    public Stream<String> getLinesFromFile(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath));
    }
    
}
