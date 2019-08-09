package net.spacive.bigfilefinder;

import android.app.Application;

import androidx.room.Room;

import net.spacive.bigfilefinder.persistence.AppDatabase;

public class BigFileFinderApp extends Application {

    private static AppDatabase appDatabase;

    // single instance of appDatabase
    public synchronized AppDatabase getAppDatabase() {
        if (appDatabase == null) {

            appDatabase = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase.class)
                    .build();
        }

        return appDatabase;
    }
}
