package net.spacive.bigfilefinder.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchResultDao {

    @Query("select * from results")
    List<SearchResultModel> getSearchResults();

    @Query("delete from results")
    void deleteAllSearchResults();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSearchResult(SearchResultModel searchResultModel);

    @Delete
    void delete(SearchResultModel searchResultModel);
}
