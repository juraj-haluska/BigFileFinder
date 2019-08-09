package net.spacive.bigfilefinder.service;

import net.spacive.bigfilefinder.util.SizedIterable;

import java.io.File;

public interface ClientContract {

    void updateProgress(String fileName);

    void onResultsReady(SizedIterable<File> sizedIterable);
}
