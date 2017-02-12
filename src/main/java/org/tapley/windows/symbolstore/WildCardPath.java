package org.tapley.windows.symbolstore;

public class WildCardPath {
    private String path;
    private boolean recursive;

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
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the recursive
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * @param recursive the recursive to set
     */
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
