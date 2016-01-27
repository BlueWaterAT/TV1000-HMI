package com.bwat.hmi.file;

import com.bwat.hmi.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * File watching service that listens for file creations, modifications, or deletions and notifies listeners
 * of events
 *
 * @author Kareem El-Faramawi
 */
public class FileWatcher {
    // Directories being watched
    private static ArrayList<String> directories = new ArrayList<String>();
    // Watch objects
    private static ArrayList<WatchService> watchers = new ArrayList<WatchService>();
    // Map of all listeners for files
    private static HashMap<String, ArrayList<FileListener>> fileListeners = new HashMap<String, ArrayList<FileListener>>();

    // Separates Thread that listens for changes at a certain delay
    private static Timer watcherThread = null;
    private static long filePollDelay = 1000;

    /**
     * Sets the delay between checking for changes
     *
     * @param delay Delay in milliseconds
     */
    public static void setPollDelay(long delay) {
        filePollDelay = delay;
    }

    /**
     * Watch for changes of a file at a given path
     *
     * @param path     Path to file
     * @param listener Listener to call when a change occurs
     */
    public static void watchFile(String path, FileListener listener) {
        if (path != null && path.length() > 0) {
            watchFile(FileUtils.getFile(path), listener);
        }
    }

    /**
     * Watch for changes of a file
     *
     * @param file     File to watch
     * @param listener Listener to call when a change occurs
     */
    public static void watchFile(File file, FileListener listener) {
        if (file == null) {
            return;
        }
        // Create a new directory watcher if needed
        String dir = file.isDirectory() ? file.getAbsolutePath() : file.getAbsoluteFile().getParent();
        if (!directories.contains(dir)) {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                Paths.get(dir).register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                directories.add(dir);
                watchers.add(watcher);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Add the FileListener to watch the specific file
        String path = file.getAbsolutePath();
        if (!fileListeners.containsKey(path)) {
            fileListeners.put(path, new ArrayList<FileListener>());
        }
        fileListeners.get(path).add(listener);

        // Start the watcher thread if it hasn't been started (runs once)
        if (watcherThread == null) {
            watcherThread = new Timer();
            watcherThread.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runWatchCycle();
                }
            }, 1, filePollDelay);
        }

    }

    /**
     * Start the watcher thread and listen for changes
     */
    private static void runWatchCycle() {
        for (int i = 0; i < watchers.size(); i++) {
            WatchService watcher = watchers.get(i);
            WatchKey key = watcher.poll();
            if (key != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    // Get the type of event
                    WatchEvent.Kind<?> kind = event.kind();

                    // Get the file that created the event
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    File file = ((Path) key.watchable()).resolve(ev.context()).toFile();
                    fireListenerAtPath(kind, file, file.getAbsolutePath());
                    fireListenerAtPath(kind, file, file.getParentFile().getAbsolutePath());
                }
                boolean valid = key.reset();
                if (!valid) {
                    watchers.remove(i--);
                }
            }
        }
    }

    /**
     * Fire all listeners, both file and directory, for an event
     *
     * @param kind Event type
     * @param file File that changed
     * @param path Path that was being watched
     */
    private static void fireListenerAtPath(WatchEvent.Kind<?> kind, File file, String path) {
        if (fileListeners.containsKey(path)) {
            if (kind == StandardWatchEventKinds.OVERFLOW) {
                return;
            } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                for (FileListener listener : fileListeners.get(path)) {
                    listener.fileCreated(file);
                }
            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                for (FileListener listener : fileListeners.get(path)) {
                    listener.fileChanged(file);
                }
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                for (FileListener listener : fileListeners.get(path)) {
                    listener.fileDeleted(file);
                }
            }
        }
    }
}
