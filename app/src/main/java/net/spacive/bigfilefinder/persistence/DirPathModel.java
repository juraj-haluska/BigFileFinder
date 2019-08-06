package net.spacive.bigfilefinder.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "paths")
public class DirPathModel {

    @PrimaryKey
    private String path;

    public DirPathModel(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
