package net.spacive.bigfilefinder.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "paths")
public class DirPathModel {

    @PrimaryKey
    @NonNull
    private String path;

    public DirPathModel(@NonNull String path) {
        this.path = path;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }
}
