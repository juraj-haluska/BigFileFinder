package net.spacive.bigfilefinder;

import android.app.Application;

import androidx.room.Room;

import net.spacive.bigfilefinder.persistence.AppDatabase;

public class BigFileFinderApp extends Application {

    public static final String DATABASE_NAME = "BIG_FILE_FINDER_APP_DB";

    private static AppDatabase appDatabase;

    // single instance of appDatabase
    public AppDatabase getAppDatabase() {
        if (appDatabase == null) {

            appDatabase = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME).build();
        }

        return appDatabase;
    }
}
