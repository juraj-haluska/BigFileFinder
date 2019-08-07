package net.spacive.dirpickerdialog;

import java.io.File;

public class DirItemModel{

    private String dirName;

    private File file;

    public DirItemModel(File file) {
        this.file = file;
        this.dirName = file.getName();
    }

    public String getDirName() {
        return dirName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
