package net.spacive.bigfilefinder.service;

import net.spacive.bigfilefinder.util.SizedSortedSet;

import java.io.File;

public interface ClientContract {
    void updateProgress(String fileName);
    void onResultsReady(SizedSortedSet<File> sortedSet);
}
