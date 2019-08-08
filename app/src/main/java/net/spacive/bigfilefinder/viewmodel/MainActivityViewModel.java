package net.spacive.bigfilefinder.viewmodel;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.spacive.bigfilefinder.BigFileFinderApp;
import net.spacive.bigfilefinder.persistence.DirPathDao;
import net.spacive.bigfilefinder.persistence.DirPathModel;
import net.spacive.bigfilefinder.persistence.SearchResultDao;
import net.spacive.bigfilefinder.persistence.SearchResultModel;
import net.spacive.bigfilefinder.service.ClientContract;
import net.spacive.bigfilefinder.service.FinderService;
import net.spacive.bigfilefinder.service.ServiceContract;
import net.spacive.bigfilefinder.util.SizedSortedSet;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements ClientContract {

    public static final String TAG = MainActivityViewModel.class.getSimpleName();

    private DirPathDao dirPathDao;

    private MutableLiveData<Boolean> finderServiceBound = new MutableLiveData<>();

    private ServiceConnection connection;

    private MutableLiveData<String> finderServiceProgress = new MutableLiveData<>();

    private MutableLiveData<Boolean> resultsReady = new MutableLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        BigFileFinderApp app = (BigFileFinderApp) application;
        dirPathDao = app.getAppDatabase().dirPathDao();
    }

    public LiveData<List<DirPathModel>> getDirPaths() {
        return dirPathDao.getDirPaths();
    }

    public LiveData<Boolean> isFinderServiceBound() {
        return finderServiceBound;
    }

    public LiveData<String> getFinderServiceProgress() {
        return finderServiceProgress;
    }

    public LiveData<Boolean> getResultsReady() {
        return resultsReady;
    }

    public void startFinderService(int maxFiles, String[] files) {
        Intent intent = new Intent(getApplication(), FinderService.class);
        intent.putExtra(FinderService.INTENT_KEY_FILES, files);
        intent.putExtra(FinderService.INTENT_KEY_MAX_FILES, maxFiles);

        ContextCompat.startForegroundService(getApplication(), intent);
    }

    public void bindFinderService() {
        Intent intent = new Intent(getApplication(), FinderService.class);

        final ClientContract clientContract = this;

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                FinderService.ServiceBinder serviceBinder = ((FinderService.ServiceBinder) iBinder);
                ServiceContract service = serviceBinder.getServiceContract();

                service.onClientReceived(clientContract);

                if (service.isRunning()) {
                    finderServiceBound.postValue(true);
                } else {
                    finderServiceBound.postValue(false);
                    getApplication().unbindService(this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                finderServiceBound.postValue(false);
            }
        };

        getApplication().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindFinderService() {
        if (connection != null) {
            Boolean serviceBound = finderServiceBound.getValue();
            if (serviceBound != null && serviceBound) {
                getApplication().unbindService(connection);
                finderServiceBound.postValue(false);
            }
        }
    }

    @Override
    public void updateProgress(String fileName) {
        finderServiceProgress.postValue(fileName);
    }

    @Override
    public void onResultsReady(SizedSortedSet<File> sortedSet) {
        unbindFinderService();

        SearchResultDao searchResultDao = ((BigFileFinderApp) getApplication())
                .getAppDatabase().searchResultDao();

        new Thread(() -> {
            searchResultDao.deleteAllSearchResults();

            Iterator<File> iterator = sortedSet.descendingIterator();

            int order = 1;
            while (iterator.hasNext()) {
                File f = iterator.next();
                searchResultDao.addSearchResult(
                        new SearchResultModel(f.getAbsolutePath(),
                                f.getName(), order++, readableFileSize(f.length())));
            }

            resultsReady.postValue(true);
        }).start();
    }

    // source: https://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
    private static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#")
                .format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
