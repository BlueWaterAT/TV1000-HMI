package com.bwat.hmi.file;

import java.io.File;

public abstract class FileListenerAdapter implements FileListener {
    @Override
    public void fileCreated(File f) {
    }

    @Override
    public void fileChanged(File f) {
    }

    @Override
    public void fileDeleted(File f) {
    }
}
