package net.spacive.bigfilefinder.persistence;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "results")
public class SearchResultModel {

    @PrimaryKey
    @NonNull
    private String path;

    private String name;

    private int order;

    private String size;

    public SearchResultModel(@NonNull String path, String name, int order, String size) {
        this.path = path;
        this.name = name;
        this.order = order;
        this.size = size;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
