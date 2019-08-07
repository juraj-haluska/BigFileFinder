package net.spacive.dirpickerdialog;

import java.io.File;

public class DirItemModel{

    private String dirName;

    private File path;

    public DirItemModel(File path) {
        this.path = path;
        this.dirName = path.getName();
    }

    public String getDirName() {
        return dirName;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }
}
