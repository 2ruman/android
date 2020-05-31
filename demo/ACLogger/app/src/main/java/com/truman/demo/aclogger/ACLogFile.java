package com.truman.demo.aclogger;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.Queue;

public class ACLogFile {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "ACLogFile" +  TAG_SUFFIX;
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final long MAX_FILE_SIZE = 1 * 1024 * 1024; // 1 MiB
    private static final String FILE_NAME = "aclog";
    private static final String FILE_PATH =  ACLogUtil.getExternalCacheDir().getAbsolutePath() + "/" + FILE_NAME;
    private static final byte EOL = 10;
    private static final int EOL_SIZE = 1;
    private static final int LONG_SIZE = Long.SIZE / 8;
    private static final int FILE_OFFSET = 0;
    private static final int FILE_LATEST_VERSION = 1;
    private static final int FILE_RESERVED_LENGTH = 32;

    // File Pointer(8) + File Version(8) + Reserved(32) + EOL(1)
    private static final long MAX_HEADER_LENGTH = LONG_SIZE + LONG_SIZE + FILE_RESERVED_LENGTH + 1;

    // The accumulated logs will be written to the path,
    // /storage/emulated/0/Android/data/com.truman.demo.aclogger/cache/aclog, by default.
    public static void saveFile(@NonNull String filePath, @NonNull Queue<String> logQ) {
        LogI("Saving logs... [QS : " + logQ.size() + "]");
        LogD("Target path : " + filePath);

        try {

            RandomAccessFile file = new RandomAccessFile(filePath, "rwd");

            checkAndReset(file);

            file.seek(FILE_OFFSET);
            long filePointer = file.readLong();
            file.seek(filePointer);

            while (!logQ.isEmpty()) {
                String log = logQ.poll();
                if (log == null) {
                    continue;
                }
                byte[] data = log.getBytes();
                if (filePointer + data.length  + EOL_SIZE > MAX_FILE_SIZE) {
                    file.seek(MAX_HEADER_LENGTH);
                }
                file.write(data);
                file.write(EOL);

                filePointer = file.getFilePointer();
            }

            file.seek(FILE_OFFSET);
            file.writeLong(filePointer);

            LogI(String.format(Locale.US,
                    "Saving success! [FP : %d, FS : %d]", filePointer, file.length()));
        } catch (Exception e) {
            LogE("Failed to save logs : " + e.toString());
            e.printStackTrace();
        }
        return;
    }

    public static String getDefaultPath() {
        return FILE_PATH;
    }

    private static void checkAndReset(@NonNull RandomAccessFile file) throws IOException {
        byte[] resetMessageBytes = null;
        try {
            check(file);
        } catch (SecurityException e) {
            resetMessageBytes = ACLogUtil.makeDebugMessage(e.getMessage()).getBytes();
            LogD("Reset reason : " + e.getMessage());
        }

        if (resetMessageBytes != null) {
            long endOfFile = MAX_HEADER_LENGTH + resetMessageBytes.length + 1;
            // Fill the file header
            file.seek(FILE_OFFSET);
            file.writeLong(endOfFile);
            file.writeLong(FILE_LATEST_VERSION);
            file.write(new byte[FILE_RESERVED_LENGTH]);
            file.write(EOL);

            // Fill the reset message
            file.write(resetMessageBytes);
            file.write(EOL);
            file.setLength(endOfFile);
        }
    }

    private static void check(@NonNull RandomAccessFile file) throws SecurityException {
        try {
            file.seek(FILE_OFFSET);

            // File Header Check
            long fileLength = file.length();
            if (fileLength == 0) {
                throw new SecurityException("File created from scratch");
            } else if (fileLength <= MAX_HEADER_LENGTH) {
                throw new SecurityException("Broken file header");
            } else if (fileLength > MAX_FILE_SIZE) {
                throw new SecurityException("File size exceeded");
            }

            // File Validity Check
            long filePointer = file.readLong();
            if (filePointer > MAX_FILE_SIZE) {
                throw new SecurityException("File corrupted");
            }

            //File Version Check - Do migration if required
            long fileVersion = file.readLong();
            if (fileVersion != FILE_LATEST_VERSION) {
                throw new SecurityException("Version mismatched");
            }
        } catch (IOException e) {
            throw new SecurityException("Unexpected error", e);
        }

        LogD("Header Check : Passed!");
    }

    private static void LogD(String msg) {
        if (DEBUG && msg != null) {
            Log.d(TAG, msg);
        }
    }

    private static void LogI(String msg) {
        if (msg != null) {
            Log.i(TAG, msg);
        }
    }

    private static void LogE(String msg) {
        if (msg != null) {
            Log.e(TAG, msg);
        }
    }
}
