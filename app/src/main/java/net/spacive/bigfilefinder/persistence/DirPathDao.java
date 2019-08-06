package net.spacive.bigfilefinder.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DirPathDao {

    @Query("select * from paths")
    LiveData<List<DirPathModel>> getDirPaths();

    @Query("delete from paths")
    void deleteAllDirPaths();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addDirPathModel(DirPathModel dirPathModel);

    @Delete
    void deleteDirPathModel(DirPathModel dirPathModel);
}
