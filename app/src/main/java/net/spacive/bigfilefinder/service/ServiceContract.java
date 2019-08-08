package net.spacive.bigfilefinder.service;

public interface ServiceContract {
    void onClientReceived(ClientContract clientContract);
    boolean isRunning();
}