package net.spacive.bigfilefinder.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DirPathModel.class, SearchResultModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DirPathDao dirPathDao();

    public abstract SearchResultDao searchResultDao();
}
