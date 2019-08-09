package net.spacive.bigfilefinder.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {

    private static final String EMULATED = "emulated/";

    private static final String STORAGE = "storage/";

    private Context context;

    public StorageManager(@NonNull Context context) {
        this.context = context;
    }

    public List<String> getEmulatedStorage() {
        File[] files = ContextCompat.getExternalFilesDirs(context, null);

        List<String> emulated = new ArrayList<>();

        for (File f:files) {
            String path = f.getAbsolutePath();

            if (path.contains(EMULATED)) {
                String [] split = path.split(EMULATED);

                if (split.length != 2) {
                    continue;
                }

                int nextSeparatorIndex = split[1].indexOf(File.separator);

                if (nextSeparatorIndex < 1) {
                    continue;
                }

                String emulatedPath = split[0] +
                        EMULATED +
                        split[1].substring(0, nextSeparatorIndex);

                emulated.add(emulatedPath);
            }
        }

        return emulated;
    }

    public List<String> getExternalStorage() {
        File[] files = ContextCompat.getExternalFilesDirs(context, null);

        List<String> external = new ArrayList<>();

        for (File f:files) {
            String path = f.getAbsolutePath();

            if (!path.contains(EMULATED)) {
                String [] split = path.split(STORAGE);

                if (split.length != 2 && split[0].length() != 1) {
                    continue;
                }

                int nextSeparatorIndex = split[1].indexOf(File.separator);

                if (nextSeparatorIndex <= 0) {
                    continue;
                }

                String externalPath = split[0] +
                        STORAGE +
                        split[1].substring(0, nextSeparatorIndex);

                external.add(externalPath);
            }
        }

        return external;
    }
}
