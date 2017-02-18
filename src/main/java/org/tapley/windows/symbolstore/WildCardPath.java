package org.tapley.windows.symbolstore;

public class WildCardPath {
    private final String path;
    private final boolean recursive;

    public WildCardPath(String path, boolean recursive) {
        this.path = path;
        this.recursive = recursive;
    }
    
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the recursive
     */
    public boolean isRecursive() {
        return recursive;
    }
}