package net.spacive.bigfilefinder.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import net.spacive.bigfilefinder.BigFileFinderApp;
import net.spacive.bigfilefinder.persistence.SearchResultDao;
import net.spacive.bigfilefinder.persistence.SearchResultModel;

import java.util.List;

public class ViewResultsActivityViewModel extends AndroidViewModel {

    private SearchResultDao searchResultDao;

    public ViewResultsActivityViewModel(@NonNull Application application) {
        super(application);
        BigFileFinderApp app = (BigFileFinderApp) application;
        searchResultDao = app.getAppDatabase().searchResultDao();
    }

    public List<SearchResultModel> getSearchResults() {
        return searchResultDao.getSearchResults();
    }

    public void deleteSearchResult(SearchResultModel searchResult) {
        searchResultDao.delete(searchResult);
    }
}
