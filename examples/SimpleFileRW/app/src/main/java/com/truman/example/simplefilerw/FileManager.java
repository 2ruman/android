package com.truman.example.simplefilerw;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FileManager {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "FileManager" +  TAG_SUFFIX;
    private final Context mContext;

    public static final String TEST_FILE_NAME = "test.txt";

    public FileManager(Context context) {
        mContext = context;
    }

    public byte[] read(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Failed to read file due to invalid file name");
            // throw new Exception("Invalid file name");
            return null;
        }
        return readInternal(fileName);
    }

    public byte[] readInternal(String fileName) {
        byte[] data = null;
        try (FileInputStream fis = mContext.openFileInput(fileName)) {
            data = new byte[fis.available()];
            fis.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public boolean write(byte[] data, String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Failed to write file due to invalid file name");
            // throw new Exception("Invalid file name");
            return false;
        }
        if (data == null) {
            data = new byte[0];
        }
        return writeInternal(data, fileName, Context.MODE_PRIVATE);
    }

    public boolean append(byte[] data, String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            Log.e(TAG, "Failed to append file due to invalid file name");
            // throw new Exception("Invalid file name");
            return false;
        }
        if (data == null) {
            data = new byte[0];
        }
        return writeInternal(data, fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);
    }

    private boolean writeInternal(byte[] data, String fileName, int mode) {
        boolean result = false;

        try (FileOutputStream fos = mContext.openFileOutput(fileName, mode)) {
            fos.write(data);
            fos.flush();
            fos.getFD().sync();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void clear(byte[] data) {
        if (data != null) {
            Arrays.fill(data, 0, data.length, (byte) 0);
        }
    }
}
