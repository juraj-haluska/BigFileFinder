package net.spacive.bigfilefinder.service;

import net.spacive.bigfilefinder.util.SizedSortedIterable;

import java.io.File;

public interface ClientContract {
    void updateProgress(String fileName);
    void onResultsReady(SizedSortedIterable<File> sortedIterable);
}
