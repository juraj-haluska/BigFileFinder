package net.spacive.bigfilefinder.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;

import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import net.spacive.bigfilefinder.R;
import net.spacive.bigfilefinder.util.FileNode;
import net.spacive.bigfilefinder.util.SizedIterable;
import net.spacive.bigfilefinder.util.TreeTraverser;
import net.spacive.bigfilefinder.view.MainActivity;

import java.io.File;
import java.util.Comparator;

public class FinderService extends Service implements ServiceContract {

    public static final String INTENT_KEY_FILES = "INTENT_KEY_FILES";
    public static final String INTENT_KEY_MAX_FILES = "INTENT_KEY_MAX_FILES";

    private static final String CHANNEL_ID = "FINDER";
    private static final int NOTIFICATION_ID = 50;

    private NotificationCompat.Builder notification;
    private NotificationManager notificationManager;

    private ClientContract clientContract;
    private boolean isRunning = false;

    private static final Comparator<File> fileComparator = (fileA, fileB) -> {
        if (fileA.length() > fileB.length()) return 1;
        if (fileA.length() < fileB.length()) return -1;

        // case where files have the same length - they can be
        // the same file - in this case compare their path
        if (fileA.getAbsolutePath().equals(fileB.getAbsolutePath())){
            // they are same, ignore fileB than
            return 0;
        } else {
            // add different file with same length to set
            return 1;
        }
    };

    private void startForeground() {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, notification.build());
    }

    private void updateNotificationContent(String content) {
        notification.setContentText(content);
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    public class ServiceBinder extends Binder {
        public ServiceContract getServiceContract() {
            return FinderService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notif_title))
                .setSmallIcon(R.drawable.ic_search_24dp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        startForeground();

        String[] paths = intent.getStringArrayExtra(INTENT_KEY_FILES);
        int maxFiles = intent.getIntExtra(INTENT_KEY_MAX_FILES,
                getResources().getInteger(R.integer.default_max_files));

        if (paths != null) {
            startServiceThread(paths, maxFiles);
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();

    }

    @Override
    public void onClientReceived(ClientContract clientContract) {
        this.clientContract = clientContract;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    private void startServiceThread(String[] paths, int maxFiles) {
        new Thread(() -> {
            SizedIterable<File> sizedIterable =
                    new SizedIterable<>(maxFiles, fileComparator);

            for (String path : paths) {
                FileNode rootNode = new FileNode(new File(path));
                TreeTraverser treeTraverser = new TreeTraverser(rootNode);

                treeTraverser.traverse(fileNode -> {
                    File file = ((File) fileNode.getData());

                    if (file.isFile()) {
                        sizedIterable.add(file);

                        updateNotificationContent(file.getName());

                        if (clientContract != null) {
                            clientContract.updateProgress(file.getName());
                        }
                    }
                });
            }

            if (clientContract != null) {
                clientContract.updateProgress(getString(R.string.notif_message));
            }

            sizedIterable.sort();

            if (clientContract != null) {
                clientContract.onResultsReady(sizedIterable);
            }

            isRunning = false;
            stopSelf();
        }).start();
    }

    // source: https://developer.android.com/training/notify-user/build-notification
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
