package com.truman.example.atomicfilerw;

import android.content.Context;
import android.util.AtomicFile;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AtomicFileManager {
    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "AtomicFileManager" +  TAG_SUFFIX;
    private final Context mContext;

    public static final String TEST_FILE_NAME = "test.txt";

    public AtomicFileManager(Context context) {
        mContext = context;
    }

    public byte[] read(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Failed to read file due to invalid file name");
            return null;
        }
        return readFromPrivate(fileName);
    }

    private byte[] readFromPrivate(String fileName) {
        return readInternal(new File(mContext.getFilesDir(),fileName));
    }

    private byte[] readFromExtCache(String fileName) {
        return readInternal(new File(mContext.getExternalCacheDir(),fileName));
    }

    private byte[] readInternal(File file) {
        AtomicFile aFile = new AtomicFile(file);
        byte[] ret = null;
        try {
            ret = aFile.readFully();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean write(byte[] data, String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Failed to write file due to invalid file name");
            return false;
        }
        if (data == null) {
            Log.e(TAG, "Failed to write file due to invalid data");
            return false;
        }
        return writeToPrivate(data, fileName);
    }

    private boolean writeToPrivate(byte[] data, String fileName) {
        return writeInternal(data, new File(mContext.getFilesDir(), fileName));
    }

    private boolean writeToExtCache(byte[] data, String fileName) {
        return writeInternal(data, new File(mContext.getExternalCacheDir(), fileName));
    }

    private boolean writeInternal(byte[] data, File file) {
        AtomicFile aFile = new AtomicFile(file);
        FileOutputStream fos = null;
        try {
            fos = aFile.startWrite();
            fos.write(data);
            aFile.finishWrite(fos);
        } catch (IOException e) {
            if (fos != null) {
                aFile.failWrite(fos);
            }
            e.printStackTrace();
        }
        return true;
    }

    public String[] scanPrivate() {
        File privateDir = mContext.getFilesDir();
        if (!privateDir.exists()) {
            return null;
        }
        return privateDir.list();
    }

    public String[] scanExtCache() {
        File extCacheDir = mContext.getExternalCacheDir();
        if (!extCacheDir.exists()) {
            return null;
        }
        return extCacheDir.list();
    }

    public boolean writeToPrivateTest() {
        byte[] data = "This is a test file!".getBytes();

        File file = new File(mContext.getFilesDir(), TEST_FILE_NAME);
        AtomicFile aFile = new AtomicFile(file);
        FileOutputStream fos = null;
        try {
            fos = aFile.startWrite();
            fos.write(data);
            if (true) {
                throw new IOException("This is a test!");
            }
            aFile.finishWrite(fos);
        } catch (IOException e) {
            if (fos != null) {
//              aFile.failWrite(fos);
            }
            Log.d(TAG, "Exception!!! " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
