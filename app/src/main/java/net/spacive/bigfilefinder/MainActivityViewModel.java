package net.spacive.bigfilefinder;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import net.spacive.bigfilefinder.persistence.DirPathDao;
import net.spacive.bigfilefinder.persistence.DirPathModel;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private DirPathDao dirPathDao;

    private LiveData<List<DirPathModel>> dirPaths;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        BigFileFinderApp app = (BigFileFinderApp) application;
        dirPathDao = app.getAppDatabase().dirPathDao();
    }

    public LiveData<List<DirPathModel>> getDirPaths() {
        return dirPathDao.getDirPaths();
    }
}
