package org.tapley.windows.symbolstore;

import java.io.IOException;
import java.util.stream.Stream;

public interface IFileUtil {
    Stream<String> getLinesFromFile(String filePath) throws IOException;
}
