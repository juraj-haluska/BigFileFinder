package net.spacive.bigfilefinder.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchResultDao {

    @Query("select * from results")
    List<SearchResultModel> getSearchResults();

    @Query("delete from results")
    void deleteAllSearchResults();

    @Insert
    void addSearchResult(SearchResultModel searchResultModel);
}
